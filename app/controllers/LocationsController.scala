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

import play.api._
import play.api.mvc._
import play.api.http.Status._
import com.github.nscala_time.time.Imports._
import javax.inject.Inject
import io.swagger.annotations._
import scala.language.postfixOps
import util._
import models.Location
import services.locations.{ LocationAccess, JsonFormat }

// scalastyle:off magic.number
// scalastyle:off line.size.limit

@Api(value = "locations")
class LocationsController @Inject()(locationAccess: LocationAccess) extends Controller {

  @ApiOperation(
    value = "Get metada for MET API locations.",
    notes = "Get metadata for the location names defined in the MET API. Use the query parameters to filter the set of location names returned. Leave the query parameters blank to select **all** location names.",
    response = classOf[models.LocationResponse],
    httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid parameter value or malformed request."),
    new ApiResponse(code = 401, message = "Unauthorized client ID."),
    new ApiResponse(code = 404, message = "No data was found for the list of query Ids."),
    new ApiResponse(code = 500, message = "Internal server error.")))
  def getLocations( // scalastyle:ignore public.methods.have.type
    @ApiParam(value = "The MET API location names that you want metadata for. Enter a comma-separated list to select multiple location names. Leave blank to get all names.",
              required = false)
              names: Option[String],
    @ApiParam(value = "Get MET API location names by geometry. Geometries are specified as either a POINT or POLYGON using <a href='https://en.wikipedia.org/wiki/Well-known_text'>WKT</a>; see the reference section on the <a href=reference/index.html#geometry_specification>Geometry Specification</a> for documentation and examples.",
              required = false)
              geometry: Option[String],
    @ApiParam(value = "A comma-separated list of the fields that should be present in the response. If set, only those properties listed here will be visible in the result set; e.g.: name,geometry will show only those two entries in the data set.",
              required = false)
              fields: Option[String],
    @ApiParam(value = "The output format of the result.",
              allowableValues = "jsonld",
              defaultValue = "jsonld",
              required = true)
              format: String) = no.met.security.AuthorizedAction {
    implicit request =>
    // Start the clock
    val start = DateTime.now(DateTimeZone.UTC)
    Try  {
      val nameList : Array[String] = names match {
        case Some(name) => name.toLowerCase.split(",").map(_.trim)
        case _ => Array()
      }
      val fieldList : Set[String] = fields match {
          case Some(x) => x.toLowerCase.split(",").map(_.trim).toSet
          case _ => Set()
      }
      locationAccess.getLocations(nameList, geometry, fieldList)
    } match {
      case Success(data) =>
        if (data isEmpty) {
          NotFound("Could not find any data locations for location names " + names.getOrElse("<all>"))
        } else {
          format.toLowerCase() match {
            case "jsonld" => Ok(new JsonFormat().format(start, data)) as "application/vnd.no.met.data.locations-v0+json"
            case x        => BadRequest(s"Invalid output format: $x")
          }
        }
      case Failure(x) => BadRequest(x getLocalizedMessage)
    }
  }

}

// scalastyle:on
