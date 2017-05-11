package com.qingstor.sdk.constant

import akka.actor.ActorSystem
import io.circe.Printer

import scala.io.Source

object QSConstants {
  private def getSDKVersion: String = {
    val pattern = """(?<!\\)(\"|\')(.*?)(?<!\\)\1""".r
    val versionLine = Source.fromFile("build.sbt").getLines().find(_.startsWith("version := ")).getOrElse("")
    pattern.findFirstIn(versionLine).getOrElse("").replace("\"", "")
  }

  private val scalaVersion: String = util.Properties.versionNumberString
  private val osName: String = util.Properties.osName
  private val arch: String = System.getProperty("os.arch")

  final val QingStorSystem: ActorSystem = ActorSystem("QingStor")

  val LogFatal: String = "fatal"
  val LogError: String = "error"
  val LogWarn: String = "warn"
  val LogDebug: String = "debug"
  val LogInfo: String = "info"

  final val ParamsLocationHeader = "headers"
  final val ParamsLocationParam = "params"
  final val ParamsLocationElement = "elements"
  final val ParamsLocationBody = "body"

  val APIGETService: String = "GET Service"

  val ACLPermissions = List("READ", "WRITE", "FULL_CONTROL")
  val GranteeTypes = List("user", "group")
  val CORSHttpMethods = List("GET", "PUT", "POST", "DELETE", "HEAD")
  val StatementEffects = List("allow", "deny")

  val BucketNamePlaceHolder: String = "{bucketName}"
  val ObjectKeyPlaceHolder: String = "{objectKey}"

  val UserAgent: String = s"qingstor-sdk-scala/$getSDKVersion (Scala $scalaVersion; $osName $arch)"

  val printer = Printer(preserveOrder = true, dropNullKeys = true, indent = "")
}
