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

import scala.language.postfixOps
import play.api.Play.current
import play.api._
import play.api.db._
import anorm._
import anorm.SqlParser._
import java.sql.Connection
import javax.inject.Singleton
import models.Location

//$COVERAGE-OFF$Not testing database queries
@Singleton
class DbLocationAccess extends LocationAccess("") {

  val parser: RowParser[Location] = {
    get[String]("name") ~
    get[String]("geo") map {
      case name~geo => Location(name, geo)
    }
  }

  def getLocations(name: Option[String]): List[Location] = {
    val nameList = name map { _.toUpperCase } map { _.replaceAll("\\s+", " ") } map { _.trim } filter { _.length != 0 }
    //Logger.debug(name.get)
    //Logger.debug(nameList.toString)
    val locQ = nameList map (nameStr => {
      val names = nameStr.split(",").map(_.trim)
      val qNameList = names.mkString("','")
      s"UPPER(name) IN ('$qNameList')"
    } ) getOrElse "TRUE"
    val query = s"""
      |SELECT
        |name, ST_AsText(geo) AS geo
      |FROM
        |locationFeature
      |WHERE
        |$locQ
      |ORDER BY
        |name""".stripMargin

    Logger.debug(query)

    DB.withConnection("locations") { implicit connection =>
      SQL(query).as( parser * )
    }
  }

}
// $COVERAGE-ON$
