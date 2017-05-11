# Installation Guide

## Requirement

This SDK isn't available from maven central now. The latest release is `1.0.0` 
and is built against Scala 2.11.x, and 2.12.x.

## Install from maven

**This SDK has not been published to maven yet**, Please install from binary release

If you use [sbt](http://www.scala-sbt.org/index.html), you can include this 
SDK in your project with

```sbtshell
libraryDependencies += "com.qingstor" %%  "qingstor-sdk-scala" % "1.0.0"
```

## Install from binary release

You can download jar from [release](https://github.com/cheerx/qingstor-sdk-scala/releases) page and put it into `lib` directory.
`qingstor-sdk-scala_xxx_fat.jar` are packed with all dependencies, `qingstor-sdk-scala_xxx.jar` are packed without dependency.
You should add dependencies below to your sbt file before use these packed without dependency.

```sbtshell
libraryDependencies ++= {
  val akkaHttpVersion = "10.0.5"
  val circeVersion = "0.7.1"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.yaml" % "snakeyaml" % "1.17",
    "de.heikoseeberger" %% "akka-http-circe" % "1.15.0"
  )
}
```