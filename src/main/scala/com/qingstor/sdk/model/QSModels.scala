package com.qingstor.sdk.model

import akka.http.scaladsl.model.{HttpEntity, ResponseEntity}
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.config.QSConfig

object QSModels {

  abstract class Output
  abstract class Input
  class QSHttpResponse() {
    protected var statusCode: Int = _
    protected var requestID: String = _
    protected var entity: ResponseEntity = HttpEntity.Empty

    def setStatusCode(statusCode: Int): Unit = this.statusCode = statusCode
    def setRequestID(requestID: String): Unit = this.requestID = requestID
    def setEntity(entity: ResponseEntity): Unit = this.entity = entity

    @ParamAnnotation(location = "StatusCode")
    def getStatusCode: Int = this.statusCode

    @ParamAnnotation(location = "requestID")
    def getRequestID: String = this.requestID

    @ParamAnnotation(location = "entity")
    def getEntity: ResponseEntity = this.entity
  }

  case class ErrorMessage(
      requestID: String,
      statusCode: Option[Int] = None,
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
      bucketName: String = ""
  ) {
    require(config != null && apiName != null && method != null && requestUri != null && statusCodes != null)
    require(requestUri.startsWith("/"),
      "requestUri can't be empty and must start with '/'")
  }
}
