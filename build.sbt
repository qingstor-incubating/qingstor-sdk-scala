name := "qingstor-sdk-scala"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.12.2"
organization := "com.qingstor"
crossScalaVersions := Seq("2.12.2", "2.11.0")
crossVersion := CrossVersion.binary

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.5"
  val cucumberVersion = "1.2.5"
  val circeVersion = "0.7.1"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "org.yaml" % "snakeyaml" % "1.17",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "info.cukes" % "cucumber-java8" % cucumberVersion % "test",
    "info.cukes" % "cucumber-junit" % cucumberVersion % "test",
    "de.heikoseeberger" %% "akka-http-circe" % "1.15.0",
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion
  )
}

scalacOptions ++= Seq("-deprecation", "-unchecked")
trapExit := false

assemblyOutputPath in assembly := file(
  s"release/${name.value}-${version.value}-fat_${scalaVersion.value}.jar")
test in assembly := {}
logLevel in assembly := Level.Error