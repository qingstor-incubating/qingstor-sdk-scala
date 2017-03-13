name := "qingstor-sdk-scala"
version := "1.0"
scalaVersion := "2.12.1"
organization := "com.qingstor"

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.3"
  val cucumberVersion = "1.2.5"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "org.yaml" % "snakeyaml" % "1.17",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "info.cukes" % "cucumber-java8" % cucumberVersion,
    "info.cukes" % "cucumber-junit" % cucumberVersion
  )
}

assemblyOutputPath in assembly := file("test/steps/qingstor-fat.jar")