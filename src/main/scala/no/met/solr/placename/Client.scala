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
