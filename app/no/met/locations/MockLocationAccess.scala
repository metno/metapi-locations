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

package no.met.locations

import javax.inject.Singleton
import models.Location
import play.Logger

@Singleton
class MockLocationAccess extends LocationAccess("") {
  // Mock Locations
  val locations = List[Location](
    new Location(
      "Moen",
      "POINT(8.118306 58.221361)"),
    new Location(
      "Ulsvannet",
      "POINT(8.207000 58.224531)")
  )

  def getLocations(name: Option[String]): List[Location] = {

    val nameList : Array[String] = name match {
      case Some(name) => name.toLowerCase.split(",")
      case _ => Array[String]()
    }

    if (name.isDefined) {
      locations filter (loc => nameList.contains(loc.name.toLowerCase) || nameList.length == 0)
    }
    else {
      locations
    }

  }




}
