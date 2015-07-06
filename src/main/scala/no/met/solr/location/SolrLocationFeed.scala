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

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.{ HttpSolrClient, CloudSolrClient }
import org.apache.solr.common.SolrInputDocument
import scala.collection.mutable.{ HashMap, HashSet }
import scala.util.{ Try, Success, Failure }
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import java.nio.file._
import no.met.json._
import no.met.location._
import no.met.location.JsonWrites._
import org.apache.solr.client.solrj.response.UpdateResponse
import scala.util.{Failure, Success}

/**
 * Class to populate a Solr database with kartverkets location database that
 * is distributed in geojson format.
 *
 * The schema is defined in src/solr_home/location/conf/solrconfig.xml.
 */

class SolrLocationFeed(solr: no.met.solr.SClient, cacheDocumentsBeforeSend: Integer ) {
  private var countCache = 0
  private var count = 0

  def docsSendt: Int = count
  def newLocation(place: LocationFeature): Try[SolrInputDocument] = Try {
    val doc: SolrInputDocument = new SolrInputDocument()

    doc.setField("ssrId", place.prop.enhSsrId)
    doc.setField("ssrObjId", place.prop.enhSsrObjId)
    doc.setField("name_type", place.prop.enhNavntype)
    doc.setField("location", place.prop.enhSnavn)
    doc.setField("language", place.prop.enhSnspraak)
    doc.setField("geometry", place.geometry.geom.asWkt)
    doc.setField("jsonObj", Json.toJson(place))
    doc
  }

  def commitDocuments(flush: Boolean = false): Try[Unit] = Try {
    if (countCache > cacheDocumentsBeforeSend || (flush && countCache > 0)) {
      val res = solr.client.commit( solr.collection )
      count += countCache
      countCache = 0
    }
    ()
  }

  def addDocument(doc: SolrInputDocument): Try[Unit] = Try {
    val res = solr.client.add(solr.collection, doc)
    countCache += 1
  }

  def addLocation(location: LocationFeature): Try[Unit] =
    newLocation(location).flatMap(addDocument(_)).flatMap(_ => commitDocuments())
}
