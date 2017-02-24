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
      code: String,
      message: String,
      request_id: String,
      url: String
  )

  case class Operation(
      config: QSConfig,
      apiName: String,
      method: String,
      requestUri: String,
      statusCodes: Array[Int],
      zone: String = "",
      bucketName: String = ""
  )

  class Test {
    var foo = ""

    def getFoo: String = foo
  }
}
