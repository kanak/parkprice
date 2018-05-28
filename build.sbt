import Dependencies._

lazy val root = (project in file(".")).
    settings(
        inThisBuild(List(
            organization := "com.example",
            scalaVersion := "2.12.6",
            version := "0.1.0-SNAPSHOT"
        )),
        name := "parkprice",
        libraryDependencies ++= Seq(
            guava,
            scallop,
            scalaLogging
        ),
        libraryDependencies ++= http4s,
        libraryDependencies ++= circe,
        libraryDependencies += scalaTest % Test
    )
