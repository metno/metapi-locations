resolvers ++= Seq(
    "jgit-repo" at "http://download.eclipse.org/jgit/maven",
    "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
)

// Git plugin

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4")

//Test plugins
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

//Scalastyle Plugin
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
