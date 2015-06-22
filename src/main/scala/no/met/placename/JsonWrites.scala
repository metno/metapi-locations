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

package no.met.placename

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.tools.cmd.Opt.Implicit
import play.api.data.validation.ValidationError
import org.joda.time.DateTime
import no.met.geojson.Geometry
import no.met.geojson.JsonWrites._

/**
 * Defines some implicit writers to write kartverkets geojson properties format
 */

object JsonWrites {

  def jodaDateTimeWrites(pattern: String = ""): Writes[org.joda.time.DateTime] = new Writes[org.joda.time.DateTime] {
    import org.joda.time.DateTime
    import org.joda.time.format.DateTimeFormat
    import org.joda.time.format.DateTimeFormatter

    def writes(dt: DateTime): JsValue = pattern match {
      case s if !s.isEmpty() => JsString(dt.toString(s))
      case _ => JsNumber(BigDecimal(dt.toString("yyyyMMdd")))
    }
  }

  implicit val jodaDateWrites = jodaDateTimeWrites()

  implicit val propertiesWrites: Writes[Properties] = new Writes[Properties] {
    def writes(prop: Properties): JsValue = Json.obj(
      "skr_snskrstat" -> prop.skrSnskrstat,
      "enh_ssr_id" -> prop.enhSsrId,
      "for_kartid" -> prop.forKartId,
      "for_regdato" -> prop.forRegdato,
      "skr_sndato" -> prop.skrSndato,
      "enh_snmynd" -> prop.enhSnmynd,
      "for_sist_endret_dt" -> prop.forSistEndretDt,
      "enh_snspraak" -> prop.enhSnspraak,
      "nty_gruppenr" -> prop.ntyGruppenr,
      "enh_snavn" -> prop.enhSnavn,
      "enh_komm" -> prop.enhKomm,
      "enh_ssrobj_id" -> prop.enhSsrObjId,
      "enh_sntystat" -> prop.enhSntystat,
      "enh_navntype" -> prop.enhNavntype,
      "for_snavn" -> prop.forSnavn,
      "kom_fylkesnr" -> prop.komFylkesnr,
      "kpr_tekst" -> prop.kprTekst)
  }

  implicit val placenameFeatureWrites: Writes[PlacenameFeature] =
    new Writes[PlacenameFeature] {
      def writes(pn: PlacenameFeature): JsValue = Json.obj(
        "type" -> "Feature",
        "properties" -> pn.prop,
        "geometry" -> pn.geometry)
    }

  implicit val featureCollectionWrites: Writes[PlacenameFeatureCollection] =
    new Writes[PlacenameFeatureCollection] {
      def writes(fc: PlacenameFeatureCollection ): JsValue = Json.obj(
        "type" -> "FeatureCollection",
        "features" -> fc.features )
    }
}
