package com.qingstor.sdk.model

import com.qingstor.sdk.config.QSConfig

object QSModels {

  abstract class Output(
    var statusCode: Option[Int] = None,
    var requestID: Option[String] = None
  )

  abstract class Input

  case class ErrorMessage(
    requestID: String,
    var statusCode: Option[Int] = None,
    code: Option[String] = None,
    message: Option[String] = None,
    url: Option[String] = None
  )

  case class Operation(
    config: QSConfig,
    apiName: String,
    method: String,
    requestUri: String,
    statusCodes: Array[Int],
    zone: String = "",
    bucketName: String = "",
    objectKey: String = ""
  )
}
