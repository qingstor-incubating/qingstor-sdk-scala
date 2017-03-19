package com.qingstor.sdk.constant

import akka.actor.ActorSystem

object QSConstants {
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
}
