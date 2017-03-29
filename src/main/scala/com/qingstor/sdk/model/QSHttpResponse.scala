package com.qingstor.sdk.model

import akka.http.scaladsl.model.{HttpEntity, ResponseEntity}
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants

class QSHttpResponse {
  private var statusCode: Int = _
  private var requestID: String = _
  private var entity: ResponseEntity = HttpEntity.Empty
  private var connection: String = _
  private var date: String = _
  private var eTag: String = _
  private var server: String = _
  private var contentRange: String = _
  private var xQSEncryptionCustomerAlgorithm: String = _
  private var contentLength: String = _
  private var contentType: String = _
  private var accessControlAllowHeaders: String = _
  private var accessControlAllowMethods: String = _
  private var accessControlAllowOrigin: String = _
  private var accessControlExposeHeaders: String = _
  private var accessControlMaxAge: String = _
  private var lastModified: String = _

  def setStatusCode(statusCode: Int): Unit = this.statusCode = statusCode
  def setRequestID(requestID: String): Unit = this.requestID = requestID
  def setEntity(entity: ResponseEntity): Unit = this.entity = entity
  def setConnection(conn: String): Unit = this.connection = conn
  def setDate(date: String): Unit = this.date = date
  def setETag(tag: String): Unit = this.eTag = tag
  def setServer(server: String): Unit = this.server = server
  def setContentRange(range: String): Unit = this.contentRange = range
  def setXQSEncryptionCustomerAlgorithm(algorithm: String): Unit =
    this.xQSEncryptionCustomerAlgorithm = algorithm
  def setContentLength(length: String): Unit = this.contentLength = length
  def setContentType(t: String): Unit = this.contentType = t
  def setAccessControlAllowHeaders(headers: String): Unit =
    this.accessControlAllowHeaders = headers
  def setAccessControlAllowMethods(methods: String): Unit =
    this.accessControlAllowMethods = methods
  def setAccessControlAllowOrigin(str: String): Unit =
    this.accessControlAllowOrigin = str
  def setAccessControlExposeHeaders(str: String): Unit =
    this.accessControlExposeHeaders = str
  def setAccessControlMaxAge(str: String): Unit =
    this.accessControlMaxAge = str
  def setLastModified(str: String): Unit = this.lastModified = str

  @ParamAnnotation(location = "StatusCode")
  def getStatusCode: Int = this.statusCode

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "X-QS-Request-ID")
  def getRequestID: String = this.requestID

  @ParamAnnotation(location = "entity")
  def getEntity: ResponseEntity = this.entity

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Connection")
  def getConnection: String = this.connection

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Date")
  def getDate: String = this.date

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "ETag")
  def getETag: String = this.eTag

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Server")
  def getServer: String = this.server

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Content-Range")
  def getContentRange: String = this.contentRange

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "X-QS-Encryption-Customer-Algorithm")
  def getXQSEncryptionCustomerAlgorithm: String =
    this.xQSEncryptionCustomerAlgorithm

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Content-Length")
  def getContentLength: String = this.contentLength

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Content-Type")
  def getContentType: String = this.contentType

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Access-Control-Allow-Headers")
  def getAccessControlAllowHeaders: String = this.accessControlAllowHeaders

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Access-Control-Allow-Methods")
  def getAccessControlAllowMethods: String = this.accessControlAllowMethods

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Access-Control-Allow-Origin")
  def getAccessControlAllowOrigin: String = this.accessControlAllowOrigin

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Access-Control-Expose-Headers")
  def getAccessControlExposeHeaders: String = this.accessControlExposeHeaders

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Access-Control-Max-Age")
  def getAccessControlMaxAge: String = this.accessControlMaxAge

  @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
    name = "Last-Modified")
  def getLastModified: String = this.lastModified
}
