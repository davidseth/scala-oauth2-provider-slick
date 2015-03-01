name := """play-oath2-server-slick-titan"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "com.nulab-inc" %% "play2-oauth2-provider" % "0.12.0",
  "joda-time" % "joda-time" % "2.4",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)