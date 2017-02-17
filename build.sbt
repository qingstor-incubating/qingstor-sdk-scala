name := "qingstor-sdk-scala"
version := "1.0"
scalaVersion := "2.12.1"

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.3"
  Seq (
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "org.yaml" % "snakeyaml" % "1.17",
  "io.spray" %%  "spray-json" % "1.3.3",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
}
