/*
    MET-API

    Copyright (C) 2014 met.no
    Contact information:
    Norwegian Meteorological Institute
    Box 43 Blindern
    0313 OSLO
    NORWAY
    E-mail: met-api@met.no

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
    MA 02110-1301, USA
*/

package no.met.solr.location

import java.io.{ File, Reader, FileReader }
import scala.util.{ Try, Failure, Success }
import play.api.libs.json._
import no.met.fileutil._
import no.met.fileutil.FileHelper.fileIter
import no.met.location._
import no.met.location.JsonWrites._
import no.met.location.JsonReads._
import java.nio.file.FileSystems
import scala.collection.mutable.ListBuffer
import scala.util.Failure
import java.io.FileReader
import scala.util.matching.Regex
import no.met.json.JsonHelper
import scala.util.Failure
import scala.util.Success
import scala.util.Failure
import scala.util.Failure

class SolrError(msg: String) extends RuntimeException(msg) {}

/**
 * Class to populate a solr database with location data from
 * 'kartverkets' location database.
 */
private class Populate(solr: SolrLocationFeed, maxElements: Long = Long.MaxValue) {
  var toMannyErrorsCount = 0;
  var lastErrorMsg = ""
  var count: Long = 0
  var max: Long = 0
  val lb = ListBuffer[LocationFeature]()

  class MaxException extends RuntimeException {}

  def maxProccessed: Unit = {
    count += 1
    max += 1
    if (max > maxElements) {
      throw new MaxException()
    }
  }

  def dryrun(v: JsValue, path: String): Unit = {
    maxProccessed
    val place = v.validate[LocationFeature]
    val js = Json.toJson(place.get)
    println(path + s"\n${"-" * path.length()}\n" + Json.prettyPrint(js) + "\n")
  }

  def jsParse(in: java.io.Reader, jsonPath: JsonHelper.Path, f: (JsValue, String) => Unit): Try[Unit] = {
    JsonHelper.Parser.parse(in, jsonPath, f) recover {
      case _: MaxException => ()
      case e => throw e
    }
  }

  def index(v: JsValue, path: String): Unit = v.validate[LocationFeature] match {
    case JsSuccess(place, _) => solr.addLocation(place) match {
      case Failure(e) =>
        println(s"Failed to index document(s) '$path'. Reason: ${e.getMessage}")
        val msg = e.getMessage
        toMannyErrorsCount += 1

        if (msg == lastErrorMsg) {
          throw new SolrError(msg)
        } else if (toMannyErrorsCount > 10) {
          throw new SolrError(s"To manny errors: Last error '$msg'")
        }

        lastErrorMsg = msg
      case _ => maxProccessed
    }
    case _ => println(s"Validation error: '$path'.[$v]")
  }

  def doPopulate(file: String, dryRun: Boolean = false): Try[Long] = Try {
    val func = if (dryRun) dryrun _ else index _
    val dataPath = JsonHelper.Path.parse("/features/@")

    fileIter(file, ".*\\.geojson$".r) match {
      case Success(iter) =>
        for (reader <- iter) {
          jsParse(reader, dataPath.get, func) match {
            case Failure(e) =>
              throw e
            case _ =>
          }
          if (!dryRun) solr.commitDocuments(true)
        }
      case Failure(e) => throw e
    }

    count
  }
}

object Populate {
  class SolrError(msg: String) extends RuntimeException(msg) {}

  def doPopulate(solr: SolrLocationFeed, file: String, dryRun: Boolean = false,
    maxElements: Long = Long.MaxValue): Try[Long] = {
    val populate = new Populate(solr, maxElements)

    populate.doPopulate(file, dryRun)
  }

}
