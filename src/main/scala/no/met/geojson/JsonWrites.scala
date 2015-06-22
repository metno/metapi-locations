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

package no.met.geojson

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.tools.cmd.Opt.Implicit
import play.api.data.validation.ValidationError

/**
 * Defines some implicit writers to handle geojson types
 */

object JsonWrites {

  implicit val pointWrite: Writes[Point] = new Writes[Point] {
    def writes(point: Point):JsValue = {
      point.altitude match {
        case Some(a) => Json.toJson(Seq(point.longitude, point.latitude, a))
        case _ => Json.toJson(Seq(point.longitude, point.latitude))
      }
    }
  }

  implicit val geometryWrite: Writes[Geometry] = new Writes[Geometry] {
    def writes(g: Geometry): JsValue = g.geom match {
      case p: Point =>
        Json.obj("type" -> "Point",
          "coordinates" -> p)
      case gt => JsUndefined("Invalid or unimplmented geometry type '" + gt.toString() + "'.")
    }
  }
}
