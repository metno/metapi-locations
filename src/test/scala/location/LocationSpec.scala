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
import no.met.location
import no.met.location.JsonReads._
import org.joda.time.DateTime
import play.api.libs.json._
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class LocationSpec extends mutable.Specification {

  def dateTime(dt: String): DateTime = new DateTime(dt)

  "Parse 'kartverket'" should {

    "parse 'dates' as string with weird patterns 'yyyy?MM?dd' to jodatime" in {
      implicit val jodaDateReads = jodaDateTimeReads("yyyy?MM?dd")
      val js = Json.parse("""{"date": "2015?04?15" }""")
      val dt = (js \ "date").validate[DateTime].asOpt

      dt must beSome.which(_ == dateTime("2015-04-15"))
    }

    "parse 'dates' as string on iso format 'yyyy-MM-dd' to jodatime" in {
      val js = Json.parse("""{"date": "2015-04-15" }""")
      val dt = (js \ "date").validate[DateTime].asOpt

      dt must beSome.which(_ == dateTime("2015-04-15"))
    }

    "parse 'dates' as integer on the form 'yyyyMMdd' to jodatime" in {
      val js = Json.parse("""{"date": 20150415 }""")
      val dt = (js \ "date").validate[DateTime].asOpt

      dt must beSome.which(_ == dateTime("2015-04-15"))
    }

    "parse 'dates' as integer on the form 'yyyyMMddHH' to jodatime" in {
      val js = Json.parse("""{"date": 2015041505 }""")
      val dt = (js \ "date").validate[DateTime].asOpt

      dt must beSome.which(_ == dateTime("2015-04-15T05"))
    }

    "parse 'dates' as integer on the form 'yyyyMMddHHmm' to jodatime" in {
      val js = Json.parse("""{"date": 201504150502 }""")
      val dt = (js \ "date").validate[DateTime].asOpt

      dt must beSome.which(_ == dateTime("2015-04-15T05:02"))
    }

    "parse 'dates' as integer on the form 'yyyyMMddHHmmss' to jodatime" in {
      val js = Json.parse("""{"date": 20150415050210 }""")
      val dt = (js \ "date").validate[DateTime].asOpt

      dt must beSome.which(_ == dateTime("2015-04-15T05:02:10"))
    }

    "parse 'location' properites" in {
      val js = Json.parse(
        """{
              "properties": { "skr_snskrstat":"G",
                              "enh_ssr_id":1619,
                              "for_kartid":"1411-2",
                              "for_regdato":19870707,
                              "skr_sndato":19590101,
                              "enh_snmynd":"SK",
                              "for_sist_endret_dt":20060714,
                              "enh_snspraak":"NO",
                              "nty_gruppenr":5,
                              "enh_snavn":"Nome",
                              "enh_komm":1021,
                              "enh_ssrobj_id":1622,
                              "enh_sntystat":"H",
                              "enh_navntype":108,
                              "for_snavn":"Nome",
                              "kom_fylkesnr":10,
                              "kpr_tekst":"Norge 1:50 000"
                            }
           }""")

      val prop = (js \ "properties").validate[location.Properties].asOpt

      prop must beSome.which { p =>
        p match {
          case p: location.Properties =>
            p.skrSnskrstat == "G" &&
              p.enhSsrId == 1619 &&
              p.forKartId == "1411-2" &&
              p.forRegdato == dateTime("1987-07-07") &&
              p.skrSndato == dateTime("1959-01-01") &&
              p.enhSnmynd == "SK" &&
              p.forSistEndretDt == dateTime("2006-07-14") &&
              p.enhSnspraak == "NO" &&
              p.ntyGruppenr == 5 &&
              p.enhSnavn == "Nome" &&
              p.enhKomm == 1021 &&
              p.enhSsrObjId == 1622 &&
              p.enhSntystat == "H" &&
              p.enhNavntype == 108 &&
              p.forSnavn == "Nome" &&
              p.komFylkesnr == 10 &&
              p.kprTekst == "Norge 1:50 000"
          case _ => false
        }
      }
    }

    "parse 'location' LocationFeature" in {
      val js = Json.parse(
        """{
            "type":"Feature",
            "properties":{ "skr_snskrstat":"G",
                           "enh_ssr_id":1619,
                           "for_kartid":"1411-2",
                           "for_regdato":19870707,
                           "skr_sndato":19590101,
                           "enh_snmynd":"SK",
                           "for_sist_endret_dt":20060714,
                           "enh_snspraak":"NO",
                           "nty_gruppenr":5,
                           "enh_snavn":"Nome",
                           "enh_komm":1021,
                           "enh_ssrobj_id":1622,
                           "enh_sntystat":"H",
                           "enh_navntype":108,
                           "for_snavn":"Nome",
                           "kom_fylkesnr":10,
                           "kpr_tekst":"Norge 1:50 000"
                         },
              "geometry":{ "type":"Point",
                           "coordinates":[7.184372,58.245103 ]
                         }
           }""")

      val loc = js.validate[location.LocationFeature].asOpt

      loc must beSome.which { p =>
        p match {
          case p: location.LocationFeature =>
            p.prop.isInstanceOf[location.Properties] &&
              p.geometry.isInstanceOf[Geometry]
          case _ => false
        }
      }
    }
  }
}
