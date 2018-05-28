import sbt._

object Dependencies {
    lazy val guava = "com.google.guava" % "guava" % "25.1-jre"
    lazy val scallop = "org.rogach" %% "scallop" % "3.1.2"
    lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
    lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"

    lazy val Http4sVersion = "0.18.11"
    val Specs2Version = "4.2.0"
    val LogbackVersion = "1.2.3"
    lazy val http4s = Seq(
        "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
        "org.http4s" %% "http4s-circe" % Http4sVersion,
        "org.http4s" %% "http4s-dsl" % Http4sVersion,
        "ch.qos.logback" % "logback-classic" % LogbackVersion
    )

    lazy val circeVersion = "0.9.3"
    lazy val circe = Seq(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-generic",
        "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)
}
