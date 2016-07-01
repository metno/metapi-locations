/*
    MET-API

    Copyright (C) 2016 met.no
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

package services

import play.api.mvc._
import play.api.libs.json._
import com.github.nscala_time.time.Imports._
import models.{ Location, ResponseData }
import no.met.data.BasicResponseData
import no.met.data.format.json.BasicJsonFormat

/**
 * Creating a json representation of locations data
 */
object JsonFormat extends BasicJsonFormat {

  implicit val locationWriter: Writes[Location] = new Writes[Location] {

    private def withoutValue(v: JsValue): Boolean = v match {
      case JsNull => true
      case JsString("") => true
      case _ => false
    }

    def writes(location: Location): JsObject = {
      val js = Json.obj(
        "@type" -> "Location",
        "name" -> location.name,
        "geo" -> location.geo)
      JsObject(js.fields.filterNot(t => withoutValue(t._2)))
    }
  }

  implicit val responseDataWrites: Writes[ResponseData] = new Writes[ResponseData] {
    def writes(response: ResponseData): JsObject = {
      header(response.header) + ("data", Json.toJson(response.data))
    }
  }

  /**
   * Create json representation of the given list
   *
   * @param start Start time of the query processing.
   * @param locations The list to create a representation of.
   * @return json representation, as a string
   */
  def format[A](start: DateTime, locations: Traversable[Location])(implicit request: Request[A]): String = {
    val size = locations.size
    val duration = new Duration(DateTime.now.getMillis() - start.getMillis())
    // Create json representation
    val header = BasicResponseData("Response", "Locations", "v0", duration, size, size, size, 0, None, None)
    val response = ResponseData(header, locations)
    Json.prettyPrint(Json.toJson(response))
  }

}
