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
import org.specs2._
import org.specs2.runner._
import org.junit.runner._
import no.met.json._
import no.met.geojson.JsonReads._
import play.api.libs.json._
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class GeojsonSpec extends mutable.Specification {

  "geojson" should {

    "Process geometry 'point' without altitude" in {
      val js = Json.parse("""{"geometry": { "type":"Point",
                        "coordinates":[7,58 ]
                      }}""")

      val geom = (js\"geometry").validate[Geometry].asOpt

      geom must beSome

      geom must beSome.which { g =>
        g.geom match {
          case p: Point if ! p.altitude.isDefined => p.longitude == 7 && p.latitude == 58
          case _ => false
        }
      }
    }

    "Process geometry 'point' with altitude" in {
      val js = Json.parse("""{"geometry": { "type":"Point",
                        "coordinates":[7,58,104 ]
                      }}""")

      val geom = (js\"geometry").validate[Geometry].asOpt

      geom must beSome
      geom must beSome.which { g =>
        g.geom match {
          case p: Point if p.altitude.isDefined => p.longitude == 7 && p.latitude == 58 && p.altitude.get == 104
          case _ => false
        }
      }
    }


    "Should fail on unrecognized geometry." in {
      val js = Json.parse("""{"geometry": { "type":"MultiPoint",
                        "coordinates":[7,58 ]
                      }}""")

      val geom = (js\"geometry").validate[Geometry].asOpt
      geom must beNone
    }
  }
}
