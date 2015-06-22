/*
    MET-API

    Copyright (C) 2015 met.no
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

package no.met.geojson

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.tools.cmd.Opt.Implicit
import play.api.data.validation.ValidationError

/**
 * Defines some implicit readers to handle geojson types
 */


object JsonReads {
  implicit val pointRead: Reads[Point] = (
    (JsPath \ "coordinates")(0).read[Double] and
    (JsPath \ "coordinates")(1).read[Double] and
    (JsPath \ "coordinates")(2).read[Option[Double]])(Point.apply _)

  def geometryReader: Reads[Geometry] = new Reads[Geometry] {
    def reads(json: JsValue): JsResult[Geometry] = {
      (json \ "type").validate[String] match {
        case t: JsSuccess[String] => t.get.toLowerCase().trim match {
          case "point" => json.validate[Point].map(Geometry(_))
          case s => JsError(Seq(JsPath() -> Seq(ValidationError("Geometry '" + s + "' not supported."))))
        }
        case e: JsError => e
      }
    }
  }

  implicit val geometryReads: Reads[Geometry] = geometryReader
}
