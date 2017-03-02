package com.qingstor.sdk.constant

import akka.actor.ActorSystem

object QSConstants {
  final val QSActorSystem = ActorSystem("QingStor")

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
  val APIGetObject: String = "GET Object"
  val APIPutObject: String = "PUT Object"

  val `X-QS-Request-ID`: String = "X-QS-Request-ID"

  val ACLPermissions = List("READ", "WRITE", "FULL_CONTROL")
  val GranteeTypes = List("user", "group")
  val CORSHttpMethods = List("GET", "PUT", "POST", "DELETE", "HEAD")
  val StatementEffects = List("allow", "deny")
}
