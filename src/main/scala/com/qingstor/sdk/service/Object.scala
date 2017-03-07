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
import com.qingstor.sdk.service.Object._

import scala.concurrent.{ExecutionContextExecutor, Future}
import java.io.File

import QSJsonProtocol._
import com.qingstor.sdk.service.Types.PartModel

class Object(private val _config: QSConfig,
             private val _bucketName: String,
             private val _zone: String)(implicit val system: ActorSystem,
                                        val mat: ActorMaterializer,
                                        val ec: ExecutionContextExecutor) {
  val config: QSConfig = _config
  val bucketName: String = _bucketName
  val zone: String = _zone

  def getObject(objectKey: String, input: Input)
    : Future[Either[ErrorMessage, GetObjectOutput]] = {
    val operation = Operation(
      config = this.config,
      apiName = "GET Object",
      method = "GET",
      requestUri = "/%s".format(objectKey),
      statusCodes = Array(200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send()
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
    val futureResponse = QSRequest(operation, input).send()
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, operation.statusCodes)) {
        Future{ Right(response.getStatusCode) }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }
  }

  def initiateMultipartUpload(objectKey: String, input: InitiateMultipartUploadInput)
    : Future[Either[ErrorMessage, InitiateMultipartUploadOutput]] = {
    val operation = Operation (
      config = this.config,
      apiName = "Initiate Multipart Upload",
      method = "POST",
      requestUri = "/%s?uploads".format(objectKey),
      statusCodes = Array(200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutputOrErrorMessage[
      InitiateMultipartUploadOutput](futureResponse, operation.statusCodes)
  }

  def uploadMultipart(objectKey: String, input: UploadMultipartInput)
    : Future[Either[ErrorMessage, UploadMultipartOutput]] = {
    val operation = Operation (
      config = this.config,
      apiName = "Upload Multipart",
      method = "PUT",
      requestUri = "/%s".format(objectKey),
      statusCodes = Array(201),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send()
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, operation.statusCodes)) {
        Future {
          Right(UploadMultipartOutput(
            response.getStatusCode,
            response.getXQSEncryptionCustomerAlgorithm
          ))
        }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }
  }

  def listMultipart(objectKey: String, input: ListMultipartInput)
    : Future[Either[ErrorMessage, ListMultipartOutput]] = {
    val operation = Operation (
      config = this.config,
      apiName = "List Multipart",
      method = "GET",
      requestUri = "/%s".format(objectKey),
      statusCodes = Array(200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutputOrErrorMessage[ListMultipartOutput](futureResponse, operation.statusCodes)
  }

  def abortMultipartUpload(objectKey: String, input: AbortMultipartUploadInput)
    : Future[Either[ErrorMessage, Int]] = {
    val operation = Operation (
      config = this.config,
      apiName = "Abort Multipart Upload",
      method = "DELETE",
      requestUri = "/%s".format(objectKey),
      statusCodes = Array(204),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send()
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, operation.statusCodes)) {
        Future { Right(response.getStatusCode) }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }
  }

  def completeMultipartUpload(objectKey: String, input: CompleteMultipartUploadInput)
    : Future[Either[ErrorMessage, CompleteMultipartUploadOutput]] = {
    val operation = Operation (
      config = this.config,
      apiName = "Complete Multipart Upload",
      method = "POST",
      requestUri = "/%s".format(objectKey),
      statusCodes = Array(201),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send()
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, operation.statusCodes)) {
        Future {
          Right(CompleteMultipartUploadOutput(
            response.getStatusCode,
            response.getXQSEncryptionCustomerAlgorithm
          ))
        }
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

  case class GetObjectInput(
     responseExpires: Option[String] = None, responseCacheControl: Option[String] = None,
     responseContentType: Option[String] = None, responseContentLanguage: Option[String] = None,
     responseContentEncoding: Option[String] = None, responseContentDisposition: Option[String] = None,
     range: Option[String] = None, ifModifiedSince: Option[ZonedDateTime] = None,
     ifUnmodifiedSince: Option[ZonedDateTime] = None, ifMatch: Option[String] = None,
     ifNoneMatch: Option[String] = None, xQSEncryptionCustomerAlgorithm: Option[String] = None,
     xQSEncryptionCustomerKey: Option[String] = None, xQSEncryptionCustomerKeyMD5: Option[String] = None
  ) extends Input {
    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-expires")
    def getResponseExpires: Option[String] = this.responseExpires

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-cache-control")
    def getResponseCacheControl: Option[String] = this.responseCacheControl

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-type")
    def getResponseContentType: Option[String] = this.responseContentType

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-language")
    def getResponseContentLanguage: Option[String] = this.responseContentLanguage

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-encoding")
    def getResponseContentEncoding: Option[String] = this.responseContentEncoding

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "response-content-disposition")
    def getResponseContentDisposition: Option[String] = this.responseContentDisposition

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Range")
    def getRange: Option[String] = this.range

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-Modified-Since")
    def getIfModifiedSince: Option[ZonedDateTime] = this.ifModifiedSince

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-Unmodified-Since")
    def getIfUnmodifiedSince: Option[ZonedDateTime] = this.ifUnmodifiedSince

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-Match")
    def getIfMatch: Option[String] = this.ifMatch

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "If-None-Match")
    def getIfNoneMatch: Option[String] = this.ifNoneMatch

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomAlgorithm: Option[String] = this.xQSEncryptionCustomerAlgorithm

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomKey: Option[String] = this.xQSEncryptionCustomerKey

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomKeyMD5: Option[String] = this.xQSEncryptionCustomerKeyMD5
  }

  case class GetObjectOutput(contentRange: String = "", eTag: String = "",
                             encryptionAlgorithm: String = "", contentBytes: Array[Byte] = Array.emptyByteArray
                             ) extends Output

  case class PutObjectInput(contentMD5: Option[String] = None, expect: Option[String] = None,
                            body: File = null) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-MD5")
    def getContentMD5: Option[String] = this.contentMD5

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Expect")
    def getExcept: Option[String] = this.expect

    @ParamAnnotation(location = QSConstants.ParamsLocationBody, name = "Body")
    def getBody: File = this.body
  }

  case class InitiateMultipartUploadInput(contentType: Option[String] = None) extends Input {
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-Type")
    def getContentType: Option[String] = this.contentType
  }

  case class InitiateMultipartUploadOutput(bucket: String, key: String, upload_id: String) extends Output

  case class UploadMultipartInput(uploadID: String, partNumber: Int, body: Array[Byte],
                                  xQSEncryptionCustomerAlgorithm: Option[String] = None,
                                  xQSEncryptionCustomerKey: Option[String] = None,
                                  xQSEncryptionCustomerKeyMD5: Option[String] = None
                                 ) extends Input {
    require(uploadID != null && uploadID.nonEmpty && partNumber >= 0,
      "upload_id can't be empty and part_number must equal or greater than 0")
    require(body != null)
    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "upload_id")
    def getUploadID: String = this.uploadID

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "part_number")
    def getPartNumber: Int = this.partNumber

    @ParamAnnotation(location = QSConstants.ParamsLocationBody, name = "Body")
    def getBody: Array[Byte] = this.body

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm: Option[String] = this.xQSEncryptionCustomerAlgorithm

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey: Option[String] = this.xQSEncryptionCustomerKey

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5: Option[String] = this.xQSEncryptionCustomerKeyMD5
  }
  case class UploadMultipartOutput(statusCode: Int, xQSEncryptionCustomerAlgorithm: String = "") extends Output

  case class ListMultipartInput(uploadID: String, partNumberMaker: Option[Int] = None, limit: Option[Int] = None)
    extends Input {
    require(limit.isEmpty || limit.get <= 1000, "limit can't greater than 1000")
    require(uploadID != null && uploadID.nonEmpty, "upload_id can't be empty")
    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "upload_id")
    def getUploadID: String = this.uploadID

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "part_number_marker")
    def getPartNumberMaker: Option[Int] = this.partNumberMaker

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "limit")
    def getLimit: Option[Int] = this.limit
  }
  case class ListMultipartOutput(count: Int, object_parts: List[PartModel]) extends Output

  case class AbortMultipartUploadInput(uploadID: String) extends Input {
    require(uploadID != null && uploadID.nonEmpty, "upload_id can't be empty")

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "upload_id")
    def getUploadID: String = this.uploadID
  }

  case class CompleteMultipartUploadInput(uploadID: String, eTag: Option[String] = None,
                                          xQSEncryptionCustomerAlgorithm: Option[String] = None,
                                          xQSEncryptionCustomerKey: Option[String] = None,
                                          xQSEncryptionCustomerKeyMD5: Option[String] = None,
                                         objectPart: List[PartModel]) extends Input {
    require(uploadID != null && uploadID.nonEmpty, "upload_id can't be empty")
    require(objectPart.forall(part => part.created.isEmpty && part.etag.isEmpty && part.size.isEmpty),
      "For CompleteMultipartUploadInput only need param \"part_number\" in objectPart")

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "upload_id")
    def getUploadID: String = this.uploadID

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "ETag")
    def getETag: Option[String] = this.eTag

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm: Option[String] = this.xQSEncryptionCustomerAlgorithm

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey: Option[String] = this.xQSEncryptionCustomerKey

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5: Option[String] = this.xQSEncryptionCustomerKeyMD5

    @ParamAnnotation(location = QSConstants.ParamsLocationElement, name = "object_parts")
    def getObjectPart: List[PartModel] = this.objectPart
  }

  case class CompleteMultipartUploadOutput(statusCode: Int, xQSEncryptionCustomerAlgorithm: String = "") extends Output
}
