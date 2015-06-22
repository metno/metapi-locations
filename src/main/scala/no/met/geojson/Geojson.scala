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

/**
 * This is a start of something that can evolve to a geojson
 * parser in the future. The future has jet to come :-)
 *
 * At the moment it is just enough to support the decoding
 * of "kartverkets stedsnavn (placenames)" database.
 */

package no.met.geojson

/**
 * Basetype for all geometries
 */
sealed trait GeomType {
  /**
   * Return the geometry as an WKT string. ex POINT( 9 62 )
   */
  def asWkt:String
}

case class Point( longitude: Double, latitude: Double, altitude: Option[Double] = None ) extends GeomType {
  def asWkt:String = "POINT( " + longitude + " " + latitude + " )"
  override def toString():String = asWkt
}

/**
 * Helper class to decode geojson, geometry.
 */
case class Geometry( geom: GeomType )

