name := """metapi-solr-placenames"""

version := "0.1-SNAPSHOT"

organization := "no.met"

licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")

description := "Load norwegians placenames into a solr database."

publishTo := {
  val nexus = "http://maven.met.no/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")


scalaVersion := "2.11.6"

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := true

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 80

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := false

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := """
    <empty>;
"""

libraryDependencies ++= Seq(
  "no.met" %% "metapi-util" % "0.1-SNAPSHOT",
  "joda-time" % "joda-time" % "2.7",
  "org.slf4j" % "slf4j-log4j12" % "1.7.12", 
  "commons-logging" % "commons-logging" % "1.2",
  "org.apache.solr" % "solr-solrj" % "5.1.0",
  "org.apache.solr" % "solr-test-framework" % "5.1.0" % "test",
  "org.apache.solr" % "solr-core" % "5.1.0",
  "org.specs2" %% "specs2-core" % "3.6.1" % "test",
  "org.specs2" %% "specs2-junit" % "3.6.1" % "test",
  "junit" % "junit" % "4.12" % "test"
)


 //"jp.sf.amateras.solr.scala" %% "solr-scala-client" % "0.0.12",

resolvers ++= Seq( 
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "sonatype-releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "sonatype-central" at "https://repo1.maven.org/maven2"
)

//"metno repo" at "http://maven.met.no/content/groups/public"

parallelExecution in Test := false

scalacOptions in Test ++= Seq("-Yrangepos")