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

package no.met.location

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError
import org.joda.time.DateTime
import no.met.geojson.Geometry
import no.met.geojson.JsonReads._

/**
 * Defines some implicit readers to handle kartverkets geojson properties format
 */

object JsonReads {
  import org.joda.time.DateTime
  import org.joda.time.format.DateTimeFormat
  import org.joda.time.format.DateTimeFormatter

  private def parseDate(input: String, format: DateTimeFormatter, path: JsPath): JsResult[DateTime] =
    scala.util.control.Exception.allCatch[DateTime].opt(DateTime.parse(input, format)) match {
      case Some(d) => JsSuccess(d)
      case None => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jodadate.format", format.toString()))))
    }

  private def intToDateTime(n: BigDecimal): JsResult[org.joda.time.DateTime] = {
    val df8 = DateTimeFormat.forPattern("yyyyMMdd")
    val df10 = DateTimeFormat.forPattern("yyyyMMddHH")
    val df12 = DateTimeFormat.forPattern("yyyyMMddHHmm")
    val df14 = DateTimeFormat.forPattern("yyyyMMddHHmmss")

    n.toString() match {
      case s if s.length() == 8 => parseDate( s, df8, JsPath())
      case s if s.length() == 10 => parseDate(s, df10, JsPath())
      case s if s.length() == 12 => parseDate(s, df12, JsPath())
      case s if s.length() == 14 => parseDate(s, df14, JsPath())
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.date (number encoded on the form yyyyMMdd[[[HH]mm]ss]"))))
    }
  }

  /**
   * Reads for the 'org.joda.time.DateTime' type.
   *
   * The default reader in play match numbers before strings
   * so it will not work when date is encodes as yyyyMMdd or something
   * similar.
   *
   * It seems that in kartverkets "stedsnavn" database the dates is coded
   * as integer on the form yyyyMMdd
   *
   * This reader interprets dates as integers on the form yyyyMMdd[[[hh]mm]ss].
   *
   * @param pattern a date pattern, as specified in `java.text.SimpleDateFormat`. Used
   *        when the date is encoded as strings.
   * @param corrector a simple string transformation function that can be used to transform input String before parsing.
   *        Useful when standards are not exactly respected and require a few tweaks
   */
  def jodaDateTimeReads(pattern: String, corrector: String => String = identity): Reads[org.joda.time.DateTime] = new Reads[org.joda.time.DateTime] {
    val dfs = DateTimeFormat.forPattern(pattern)

    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsNumber(d) => intToDateTime(d)
      case JsString(s) => parseDate(corrector(s), dfs, JsPath())
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.date"))))
    }
  }

  implicit val jodaDateReads = jodaDateTimeReads("yyyy-MM-dd")

  implicit val propertiesReads: Reads[Properties] = (
    (JsPath \ "skr_snskrstat").read[String] and
    (JsPath \ "enh_ssr_id").read[Long] and
    (JsPath \ "for_kartid").read[String] and
    (JsPath \ "for_regdato").read[DateTime] and
    (JsPath \ "skr_sndato").read[DateTime] and
    (JsPath \ "enh_snmynd").read[String] and
    (JsPath \ "for_sist_endret_dt").read[DateTime] and
    (JsPath \ "enh_snspraak").read[String] and
    (JsPath \ "nty_gruppenr").read[Int] and
    (JsPath \ "enh_snavn").read[String] and
    (JsPath \ "enh_komm").read[Int] and
    (JsPath \ "enh_ssrobj_id").read[Long] and
    (JsPath \ "enh_sntystat").read[String] and
    (JsPath \ "enh_navntype").read[Int] and
    (JsPath \ "for_snavn").read[String] and
    (JsPath \ "kom_fylkesnr").read[Int] and
    (JsPath \ "kpr_tekst").read[String])(Properties.apply _)

  def locationFeatureReader: Reads[LocationFeature] = new Reads[LocationFeature] {
    def reads(json: JsValue): JsResult[LocationFeature] = {
      (json \ "type").validate[String] match {
        case t: JsSuccess[String] => t.get.toLowerCase() match {
          case "feature" => parse(json)
          case s => JsError(Seq(JsPath() -> Seq(ValidationError("Type '" + s + "' not supported."))))
        }
        case e: JsError => e
      }
    }

    private def parse(js: JsValue): JsResult[LocationFeature] = {
      val prop = (js \ "properties").validate[Properties]
      val geom = (js \ "geometry").validate[Geometry]
      (prop, geom) match {
        case (JsSuccess(p, _), JsSuccess(g, _)) => JsSuccess(LocationFeature(p, g))
        case (e: JsError, _) => e
        case (_, e: JsError) => e
        case _ => JsError("LocationFeature, Uknown parseerror.")
      }
    }
  }

  implicit val locationFeatureReads: Reads[LocationFeature] = locationFeatureReader

}
