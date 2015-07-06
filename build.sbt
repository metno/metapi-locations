name := """locations"""

version := "0.1-SNAPSHOT"

organization := "no.met.data"

licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")

description := "Helper classes to load norwegians locations into a solr database."

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

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 95

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := false

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := """
    <empty>;
"""

EclipseKeys.eclipseOutput := Some(".eclipse_build")

libraryDependencies ++= Seq(
  "no.met" %% "metapi-util" % "0.1-SNAPSHOT",
  "joda-time" % "joda-time" % "2.7",
  "org.slf4j" % "slf4j-log4j12" % "1.7.12",
  "commons-logging" % "commons-logging" % "1.2",
  "org.apache.solr" % "solr-solrj" % "5.2.0",
  "org.apache.solr" % "solr-test-framework" % "5.2.0" % "test",
  "org.apache.solr" % "solr-core" % "5.2.0",
  "org.specs2" %% "specs2-core" % "3.6.1" % "test",
  "org.specs2" %% "specs2-junit" % "3.6.1" % "test",
  "junit" % "junit" % "4.12" % "test"
)

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "sonatype-releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "sonatype-central" at "https://repo1.maven.org/maven2",
  "restlet" at "http://maven.restlet.com",
  "metno repo" at "http://maven.met.no/content/groups/public"
)

parallelExecution in Test := false

scalacOptions in Test ++= Seq("-Yrangepos")
