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

import javax.inject.Singleton
import play.Logger
import no.met.geometry.Point
import models._

@Singleton
class MockLocationAccess extends LocationAccess("") {
  // Mock Locations
  val locations = List[Location](
    new Location(
      Some("Moen"),
      None,
      Some(Point(coordinates=Seq(8.118306, 58.221361)))),
    new Location(
      Some("Roa"),
      Some("Small town"),
      Some(Point(coordinates=Seq(10.6159, 60.2909)))),
    new Location(
      Some("Blindern"),
      Some("Part of a city"),
      Some(Point(coordinates=Seq(10.7231, 59.9406)))),
    new Location(
      Some("Tromso"),
      Some("City or large town"),
      Some(Point(coordinates=Seq(18.9551, 69.6489))))
  )

  def getLocations(names: Array[String], geometry: Option[String], fields: Set[String]): List[Location] = {
    locations.
      filter (loc => names.contains(loc.name.get.toLowerCase) || names.length == 0)
  }

}
