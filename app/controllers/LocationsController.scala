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

package controllers

import scala.language.postfixOps
import play.api._
import play.api.mvc._
import play.api.http.Status._
import javax.inject.Inject
import util._
import javax.ws.rs.{ QueryParam, PathParam }
import com.github.nscala_time.time.Imports._
import com.wordnik.swagger.annotations._
import models.Location
import no.met.locations.{ LocationAccess, JsonFormat }

// scalastyle:off magic.number

@Api(value = "/locations", description = "Descriptions of MET API locations")
class LocationsController @Inject()(locationService: LocationAccess) extends Controller {

  /**
   * GET locations data
   * @param ids list of ids to retrieve, comma-separated
   */
  @ApiOperation(
    nickname = "getLocations",
    value = "Returns information about the locations of the API",
    response = classOf[String],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "An error in the request"),
    new ApiResponse(code = 404, message = "No data was found")))
  def getLocations( // scalastyle:ignore public.methods.have.type
    @ApiParam(value = "If specified, select the location ids listed.", required = false)@QueryParam("name") name: Option[String],
    @ApiParam(value = "output format", required = true, allowableValues = "jsonld",
      defaultValue = "jsonld")@PathParam("format") format: String) = no.met.security.AuthorizedAction {
    implicit request =>
    // Start the clock
    val start = DateTime.now(DateTimeZone.UTC)
    Try  {
      locationService.getLocations(name)
    } match {
      case Success(data) =>
        if (data isEmpty) {
          NotFound("Could not find any data locations for location name " + name)
        } else {
          format.toLowerCase() match {
            case "jsonld" => Ok(JsonFormat.format(start, data)) as "application/vnd.no.met.data.locations-v0+json"
            case x        => BadRequest(s"Invalid output format: $x")
          }
        }
      case Failure(x) => BadRequest(x getLocalizedMessage)
    }
  }

}

// scalastyle:on
