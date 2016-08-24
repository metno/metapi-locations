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

import scala.language.postfixOps
import play.api.Play.current
import play.api._
import play.api.db._
import anorm._
import anorm.SqlParser._
import java.sql.Connection
import javax.inject.Singleton
import no.met.geometry.Point
import models._

//$COVERAGE-OFF$Not testing database queries

@Singleton
class DbLocationAccess extends LocationAccess("") {

  val parser: RowParser[Location] = {
    get[String]("name") ~
    get[Option[String]]("feature") ~
    get[Double]("lon") ~ 
    get[Double]("lat") map {
      case name~feature~lon~lat => Location(name, feature, Point("Point", Array(lon, lat)))
    }
  }

  def getLocations(nameList: Array[String]): List[Location] = {

    val locQ = if (nameList.length > 0) {
      val names = nameList.mkString("','")
      s"LOWER(name) IN ('$names')"
    } else "TRUE"
    
    val query = s"""
      |SELECT
        |t1.name AS name, t2.name AS feature, ST_X(geo) AS lon, ST_Y(geo) AS lat
      |FROM
        |locationFeature t1 LEFT OUTER JOIN featureType t2 ON (t1.feature_type = t2.id)
      |WHERE
        |$locQ
      |ORDER BY
        |t1.name""".stripMargin

    Logger.debug(query)

    DB.withConnection("locations") { implicit connection =>
      SQL(query).as( parser * )
    }
  }

}
// $COVERAGE-ON$
