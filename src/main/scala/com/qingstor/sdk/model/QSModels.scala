package com.qingstor.sdk.model

import com.qingstor.sdk.config.QSConfig
import io.circe.{Decoder, Encoder}

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

  implicit val decodeErrorMessage: Decoder[ErrorMessage] = Decoder.forProduct5(
    "request_id", "status_code", "code", "message", "url"
  )(ErrorMessage.apply)
  implicit val encodeErrorMessage: Encoder[ErrorMessage] = Encoder.forProduct5(
    "request_id", "status_code", "code", "message", "url"
  )(e => (e.requestID, e.statusCode, e.code, e.message, e.url))
}
