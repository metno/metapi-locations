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
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.mvc
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import TestUtil._

import scala.concurrent.Future

// scalastyle:off magic.number
/*
 * Note that these tests primarily exercise the routes and very basic controller
 * functionality; they are no guarantee that the queries against the database
 * will actually return correct data, as they are being run against mock data
 */
@RunWith(classOf[JUnitRunner])
class ControllersSpec extends Specification {

  "metapi /elements" should {

    "return a result with no id or code in the route" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \ "data").as[JsArray].value.size must equalTo(11)
    }

    "return a result with an id in the route" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?id=sum(precipitation_amount%201m)")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      contentType(response) must beSome.which(_ == "application/vnd.no.met.data.elements-v0+json")
      (json \ "data").as[JsArray].value.size must equalTo(1)
    }

    "return a result with a list of ids in the route" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?id=air_temperature,sum(precipitation_amount%201M)")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \ "data").as[JsArray].value.size must equalTo(2)
    }

    "return nothing for incorrect id" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?id=dummy")).get

      status(response) must equalTo(NOT_FOUND)
    }

    "return a result with a kdvh code in the route" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?code=TA")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \ "data").as[JsArray].value.size must equalTo(1)
    }

    "return a result with a list of codes in the route" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?code=TA,TAX")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \ "data").as[JsArray].value.size must equalTo(2)
    }

    "return nothing for incorrect code" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?code=dummy")).get

      status(response) must equalTo(NOT_FOUND)
    }

    "returns correct contentType for getElements" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.jsonld?id=sum(precipitation_amount%201m)")).get

      status(response) must equalTo(OK)
      contentType(response) must beSome.which(_ == "application/vnd.no.met.data.elements-v0+json")
    }

    "returns error if format is incorrect" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/v0.txt?id=sum(precipitation_amount%201m)")).get

      status(response) must equalTo(BAD_REQUEST)
    }

    "return a result for valid getById" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/sum(precipitation_amount%201M)/v0.jsonld")).get

      status(response) must equalTo(OK)

      val json = Json.parse(contentAsString(response))
      (json \ "data").as[JsArray].value.size must equalTo(1)
    }

    "return nothing for invalid getById" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/dummy/v0.jsonld")).get

      status(response) must equalTo(NOT_FOUND)
    }

    "returns correct contentType for getElementById" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/sum(precipitation_amount%201m)/v0.jsonld")).get

      status(response) must equalTo(OK)
      contentType(response) must beSome.which(_ == "application/vnd.no.met.data.elements-v0+json")
    }

    "returns error for incorrect format in getElementById" in new WithApplication(TestUtil.app) {
      val response = route(FakeRequest(GET, "/sum(precipitation_amount%201m)/v0.txt")).get

      status(response) must equalTo(BAD_REQUEST)
    }

  }

}

// scalastyle:on
