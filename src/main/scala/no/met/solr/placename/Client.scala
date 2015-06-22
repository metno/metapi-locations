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

package no.met.solr.placename

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.common.SolrInputDocument
import scala.collection.mutable.{ HashMap, HashSet }
import scala.util.{ Try, Success, Failure }
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import java.nio.file._
import no.met.json._
import no.met.placename._
import no.met.placename.JsonWrites._
import org.apache.solr.client.solrj._
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrDocumentList

class Client(val solr: no.met.solr.SClient) {
  val client: SolrClient = solr.client

  def result( res: SolrDocumentList ): Seq[Place] = {
    val places = for (p <- res.asScala) yield {
      for{
        ssrId <- Try(p.getFieldValue("ssrId").asInstanceOf[Long]);
        ssrObjId <- Try(p.getFieldValue("ssrObjId").asInstanceOf[Long]);
        nameType <- Try(p.getFieldValue("name_type").asInstanceOf[Int]);
        placename <- Try( p.getFieldValue("placename").asInstanceOf[String]);
        language <- Try(p.getFieldValue("language").asInstanceOf[String]);
        location <- Try(p.getFieldValue("location").asInstanceOf[String]);
        p=Place( ssrId, ssrObjId, nameType, placename, language, location)
      } yield (p)
    }

    places filter( _.isSuccess ) map{ _.get  }
  }

  def query(lon: Double, lat: Double, dist: Int, limit: Option[Int]): Try[Seq[Place]] = Try {
    val q = new SolrQuery("*:*")
      .addFilterQuery(s"{!geofilt}&sfield=location&pt=${lon} ${lat}&d=${dist}")
      .addSort("geodist()", SolrQuery.ORDER.asc)
      .setFields("ssrId,ssrObjId,name_type,placename,language,location", "_dist_:geodist()")

    val res = client.query(q)

    result( res.getResults)
  }

  def query(namePattern: String, limit: Option[Int]): Try[Seq[Place]] = Try{
    val q = new SolrQuery(s"placename_search:$namePattern")
      .addSort("placename", SolrQuery.ORDER.asc)
      .setFields("ssrId,ssrObjId,name_type,placename,language,location")

    val res = client.query(q)
    result(res.getResults)
  }
}
