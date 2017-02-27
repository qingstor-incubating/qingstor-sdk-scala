package com.qingstor.sdk.service

import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Object.{GetObjectHttpResponse, GetObjectOutput, PutObjectInput}
import com.qingstor.sdk.service.Types.{ObjectKeyModel, ObjectModel}

import scala.concurrent.{ExecutionContextExecutor, Future}
import java.io.File

class Object(private val _config: QSConfig,
             private val _bucketName: String,
             private val _zone: String)(implicit val system: ActorSystem,
                                        val mat: ActorMaterializer,
                                        val ec: ExecutionContextExecutor) {
  val config: QSConfig = _config
  val bucketName: String = _bucketName
  val zone: String = _zone

  def getObject(obj: ObjectKeyModel, input: Input): Future[Either[ErrorMessage, GetObjectOutput]] = {
    val operation = Operation(
      config = this.config,
      apiName = "GET Object",
      method = "GET",
      requestUri = "/%s".format(obj.key),
      statusCodes = Array(200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[GetObjectHttpResponse]()
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, operation.statusCodes)) {
        val bytesFuture = Unmarshal(response.getEntity).to[Array[Byte]]
        bytesFuture.map { bytes =>
          Right(GetObjectOutput(
            contentRange = response.getContentRange,
            eTag = response.getETag,
            encryptionAlgorithm = response.getXQSEncryptionCustomerAlgorithm,
            contentBytes = bytes
          ))
        }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }
  }

  def putObject(objectKey: String, input: PutObjectInput): Future[Either[ErrorMessage, Int]] = {
    val operation = Operation(
      config = this.config,
      apiName = "PUT Object",
      method = "PUT",
      requestUri = "/%s".format(objectKey),
      statusCodes = Array(201),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[QSHttpResponse]()
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, operation.statusCodes)) {
        Future{ Right(response.getStatusCode) }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }
  }
}

object Object {
  def apply(config: QSConfig, bucketName: String, zone: String)(
      implicit system: ActorSystem,
      mat: ActorMaterializer,
      ec: ExecutionContextExecutor
  ): Object = new Object(config, bucketName, zone)

  def apply(bucket: Bucket)(
      implicit system: ActorSystem,
      mat: ActorMaterializer,
      ec: ExecutionContextExecutor
  ): Object = new Object(bucket.config, bucket.bucketName, bucket.zone)

  case class GetObjectInput(`response-expires`: String = null,
                             `response-cache-control`: String = null,
                             `response-content-type`: String = null,
                             `response-content-language`: String = null,
                             `response-content-encoding`: String = null,
                             `response-content-disposition`: String = null,
                             Range: String = null,
                             `If-Modified-Since`: ZonedDateTime = null,
                             `If-Unmodified-Since`: ZonedDateTime = null,
                             `If-Match`: String = null,
                             `If-None-Match`: String = null,
                             `X-QS-Encryption-Customer-Algorithm`: String = null,
                             `X-QS-Encryption-Customer-Key`: String = null,
                             `X-QS-Encryption-Customer-Key-MD5`: String = null
                            ) extends Input {
    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-expires")
    def getResponseExpires: String = this.`response-expires`

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-cache-control")
    def getResponseCacheControl: String = this.`response-cache-control`

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-type")
    def getResponseContentType: String = this.`response-content-type`

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-language")
    def getResponseContentLanguage: String = this.`response-content-language`

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-encoding")
    def getResponseContentEncoding: String = this.`response-content-encoding`

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-disposition")
    def getResponseContentDisposition: String = this.`response-content-disposition`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Range")
    def getRange: String = this.Range

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-Modified-Since")
    def getIfModifiedSince: ZonedDateTime = this.`If-Modified-Since`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-Unmodified-Since")
    def getIfUnmodifiedSince: ZonedDateTime = this.`If-Unmodified-Since`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-Match")
    def getIfMatch: String = this.`If-Match`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-None-Match")
    def getIfNoneMatch: String = this.`If-None-Match`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomAlgorithm: String = this.`X-QS-Encryption-Customer-Algorithm`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomKey: String = this.`X-QS-Encryption-Customer-Key`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomKeyMD5: String = this.`X-QS-Encryption-Customer-Key-MD5`
  }

  class GetObjectHttpResponse extends QSHttpResponse {
    private var contentRange: String = _
    private var eTag: String = _
    private var xQSEncryptionCustomerAlgorithm: String = _

    def setContentRange(x: String): Unit = this.contentRange = x
    def setETag(x: String): Unit = this.eTag = x
    def setXQSEncryptionCustomerAlgorithm(x: String): Unit = this.xQSEncryptionCustomerAlgorithm = x

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-Range")
    def getContentRange: String = this.contentRange

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "ETag")
    def getETag: String = this.eTag

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm: String = this.xQSEncryptionCustomerAlgorithm
  }
  case class GetObjectOutput(contentRange: String = "", eTag: String = "",
                             encryptionAlgorithm: String = "", contentBytes: Array[Byte] = Array.emptyByteArray
                             ) extends Output

  case class PutObjectInput(`Content-Length`: Long, `Content-MD5`: String = null,
                            `Content-Type`: String = null, Expect: String = null,
                            body: File = null
                           ) extends Input {
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-Length")
    def getContentLength: Long = this.`Content-Length`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-MD5")
    def getContentMD5: String = this.`Content-MD5`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-Type")
    def getContentType: String = this.`Content-Type`

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Expect")
    def getExcept: String = this.Expect

    @ParamAnnotation(location = QSConstants.ParamsLocationBody, name = "Body")
    def getBody: File = this.body
  }
}
