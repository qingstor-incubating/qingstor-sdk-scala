package com.qingstor.sdk.model

import akka.http.scaladsl.model.{HttpEntity, ResponseEntity}
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants

object QSModels {

  abstract class Output
  abstract class Input

  class QSHttpResponse() {
    private var statusCode: Int = _
    private var requestID: String = _
    private var entity: ResponseEntity = HttpEntity.Empty
    private var connection: String = _
    private var date: String = _
    private var eTag: String = _
    private var server: String = _
    private var contentRange: String = _
    private var xQSEncryptionCustomerAlgorithm: String = _

    def setStatusCode(statusCode: Int): Unit = this.statusCode = statusCode
    def setRequestID(requestID: String): Unit = this.requestID = requestID
    def setEntity(entity: ResponseEntity): Unit = this.entity = entity
    def setConnection(conn: String): Unit = this.connection = conn
    def setDate(date: String): Unit = this.date = date
    def setETag(tag: String): Unit = this.eTag = tag
    def setServer(server: String): Unit = this.server = server
    def setContentRange(range: String): Unit = this.contentRange = range
    def setXQSEncryptionCustomerAlgorithm(algorithm: String): Unit = this.xQSEncryptionCustomerAlgorithm = algorithm

    @ParamAnnotation(location = "StatusCode")
    def getStatusCode: Int = this.statusCode

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Request-ID")
    def getRequestID: String = this.requestID

    @ParamAnnotation(location = "entity")
    def getEntity: ResponseEntity = this.entity

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Connection")
    def getConnection: String = this.connection

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Date")
    def getDate: String = this.date

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "ETag")
    def getETag: String = this.eTag

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Server")
    def getServer: String = this.server

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-Range")
    def getContentRange: String = this.contentRange

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm: String = this.xQSEncryptionCustomerAlgorithm
  }

  case class ErrorMessage(
      requestID: String, statusCode: Option[Int] = None,
      code: Option[String] = None, message: Option[String] = None, url: Option[String] = None
  )

  case class Operation(
      config: QSConfig, apiName: String, method: String,
      requestUri: String, statusCodes: Array[Int], zone: String = "",
      bucketName: String = ""
  ) {
    require(config != null && apiName != null && method != null && requestUri != null && statusCodes != null)
    require(requestUri.startsWith("/"),
      "requestUri can't be empty and must start with '/'")
  }
}
