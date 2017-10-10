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

package services.locations

import play.api.Play.current
import play.api._
import play.api.db._
import anorm._
import anorm.SqlParser._
import java.sql.Connection
import javax.inject.Singleton
import scala.language.postfixOps
import no.met.data.BadRequestException
import no.met.data.AnormUtil._
import no.met.geometry._
import models._

//$COVERAGE-OFF$Not testing database queries

@Singleton
class DbLocationAccess extends LocationAccess("") {

  private val parser: RowParser[Location] = {
    get[Option[String]]("name") ~
    get[Option[String]]("feature") ~
    get[Option[Double]]("lon") ~
    get[Option[Double]]("lat") map {
      case name~feature~lon~lat =>
        Location(name, feature, if (lon.isEmpty || lat.isEmpty) None else Some(Point("Point", Seq(lon.get, lat.get))))
    }
  }

  private def getSelectQuery(fields: Set[String]) : String = {
    val legalFields = Set("name", "feature", "geometry")
    val illegalFields = fields -- legalFields
    if (!illegalFields.isEmpty) {
      throw new BadRequestException(
        "Invalid fields in the query parameter: " + illegalFields.mkString(","),
        Some(s"Supported fields: ${legalFields.mkString(", ")}"))
    }
    val fieldStr = fields.mkString(", ")
      .replace("geometry", "lon, lat")
    val missing = legalFields -- fields
    if (missing.isEmpty) {
      fieldStr
    }
    else {
      val missingStr = missing.map( x => "NULL AS " + x ).mkString(", ").replace("NULL AS geometry", "NULL AS lon, NULL AS lat")
      fieldStr + "," + missingStr
    }
  }

  def getLocations(names: Array[String], geometry: Option[String], fields: Set[String]): List[Location] = {
    val selectQ = if (fields.isEmpty) "*" else getSelectQuery(fields)
    val namesQ = if (names.length > 0) "LOWER(name) IN ({names})" else "TRUE"

    val query = if (geometry.isEmpty) {
      s"""
      |SELECT
        |$selectQ
      |FROM
        |get_locations_v
      |WHERE
        |$namesQ
      |ORDER BY
        |name""".stripMargin
    }
    else {
      val geom = Geometry.decode(geometry.get)
      if (geom.isInterpolated) {
        s"""
        |SELECT
          |$selectQ
        |FROM
          |get_locations_v
        |WHERE
          |$namesQ
        |ORDER BY
          | ST_MakePoint(lon, lat)::geography::geometry <-> ST_GeomFromText('${geom.asWkt}',4326), name
        |LIMIT 1""".stripMargin
      }
      else {
        s"""
        |SELECT
          |$selectQ
        |FROM
          |get_locations_v
        |WHERE
          |$namesQ AND
          |ST_WITHIN(ST_MakePoint(lon, lat)::geography::geometry, ST_GeomFromText('${geom.asWkt}',4326))
        |ORDER BY
          |name""".stripMargin
      }
    }

    Logger.debug(query)

    DB.withConnection("locations") { implicit connection =>
      SQL(insertPlaceholders(query, List(("names", names.size))))
        .on(onArg(List(("names", names.toList))): _*)
        .as( parser * )
    }

  }

}
