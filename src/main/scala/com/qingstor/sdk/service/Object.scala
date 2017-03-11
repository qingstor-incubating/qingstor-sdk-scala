package com.qingstor.sdk.service

import java.time.ZonedDateTime
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Types._
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.QSJsonProtocol._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.qingstor.sdk.service.Object._
import java.io.File

class Object(_config: QSConfig, _bucketName: String, _zone: String)(
    implicit val system: ActorSystem,
    val mat: ActorMaterializer,
    val ec: ExecutionContextExecutor
) {
  val config: QSConfig = _config
  val bucketName: String = _bucketName
  val zone: String = _zone

  // AbortMultipartUpload does Abort multipart upload.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/abort_multipart_upload.html
  def abortMultipartUpload(
      objectKey: String,
      input: AbortMultipartUploadInput): Future[Either[ErrorMessage, Int]] = {
    val operation = Operation(
      config = config,
      apiName = "Abort Multipart Upload",
      method = "DELETE",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 204 +: // Object multipart deleted
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes))
        Future { Right(response.getStatusCode) } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // CompleteMultipartUpload does Complete multipart upload.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/complete_multipart_upload.html
  def completeMultipartUpload(objectKey: String,
                              input: CompleteMultipartUploadInput)
    : Future[Either[ErrorMessage, Int]] = {
    val operation = Operation(
      config = config,
      apiName = "Complete multipart upload",
      method = "POST",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 201 +: // Object created
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes))
        Future { Right(response.getStatusCode) } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // DeleteObject does Delete the object.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/delete.html
  def deleteObject(
      objectKey: String,
      input: DeleteObjectInput): Future[Either[ErrorMessage, Int]] = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Object",
      method = "DELETE",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 204 +: // Object deleted
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes))
        Future { Right(response.getStatusCode) } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // GetObject does Retrieve the object.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/get.html
  def getObject(
      objectKey: String,
      input: GetObjectInput): Future[Either[ErrorMessage, GetObjectOutput]] = {
    val operation = Operation(
      config = config,
      apiName = "GET Object",
      method = "GET",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 200 +: // OK
        206 +: // Partial content
        304 +: // Not modified
        412 +: // Precondition failed
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes)) {
        val bytesFuture = Unmarshal(response.getEntity).to[Array[Byte]]
        bytesFuture.map { bytes =>
          Right(
            GetObjectOutput(
              `Content-Range` = Option(response.getContentRange),
              `ETag` = Option(response.getETag),
              `X-QS-Encryption-Customer-Algorithm` =
                Option(response.getXQSEncryptionCustomerAlgorithm),
              body = bytes
            ))
        }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // HeadObject does Check whether the object exists and available.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/head.html
  def headObject(objectKey: String, input: HeadObjectInput)
    : Future[Either[ErrorMessage, HeadObjectOutput]] = {
    val operation = Operation(
      config = config,
      apiName = "HEAD Object",
      method = "HEAD",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes)) {
        val bytesFuture = Unmarshal(response.getEntity).to[Array[Byte]]
        bytesFuture.map { bytes =>
          Right(
            HeadObjectOutput(
              `Content-Length` =
                response.getEntity.contentLengthOption.map(_.toInt),
              `Content-Type` =
                Option(response.getEntity.contentType.toString()),
              `ETag` = Option(response.getETag),
              `Last-Modified` = Option(response.getLastModified),
              `X-QS-Encryption-Customer-Algorithm` =
                Option(response.getXQSEncryptionCustomerAlgorithm)
            ))
        }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // InitiateMultipartUpload does Initial multipart upload on the object.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/initiate_multipart_upload.html
  def initiateMultipartUpload(objectKey: String,
                              input: InitiateMultipartUploadInput)
    : Future[Either[ErrorMessage, InitiateMultipartUploadOutput]] = {
    val operation = Operation(
      config = config,
      apiName = "Initiate Multipart Upload",
      method = "POST",
      requestUri = "/<bucket-name>/<object-key>?uploads",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes)) {
        val bytesFuture = Unmarshal(response.getEntity).to[Array[Byte]]
        bytesFuture.map { bytes =>
          Right(
            InitiateMultipartUploadOutput(
              `X-QS-Encryption-Customer-Algorithm` =
                Option(response.getXQSEncryptionCustomerAlgorithm)
            ))
        }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // ListMultipart does List object parts.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/list_multipart.html
  def listMultipart(objectKey: String, input: ListMultipartInput)
    : Future[Either[ErrorMessage, ListMultipartOutput]] = {
    val operation = Operation(
      config = config,
      apiName = "List Multipart",
      method = "GET",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    ResponseUnpacker.unpackToOutputOrErrorMessage[ListMultipartOutput](
      futureResponse,
      operation.statusCodes)

  }

  // OptionsObject does Check whether the object accepts a origin with method and header.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/options.html
  def optionsObject(objectKey: String, input: OptionsObjectInput)
    : Future[Either[ErrorMessage, OptionsObjectOutput]] = {
    val operation = Operation(
      config = config,
      apiName = "OPTIONS Object",
      method = "OPTIONS",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes)) {
        val bytesFuture = Unmarshal(response.getEntity).to[Array[Byte]]
        bytesFuture.map { bytes =>
          Right(
            OptionsObjectOutput(
              `Access-Control-Allow-Headers` =
                Option(response.getAccessControlAllowHeaders),
              `Access-Control-Allow-Methods` =
                Option(response.getAccessControlAllowMethods),
              `Access-Control-Allow-Origin` =
                Option(response.getAccessControlAllowOrigin),
              `Access-Control-Expose-Headers` =
                Option(response.getAccessControlExposeHeaders),
              `Access-Control-Max-Age` =
                Option(response.getAccessControlMaxAge)
            ))
        }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // PutObject does Upload the object.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/put.html
  def putObject(objectKey: String,
                input: PutObjectInput): Future[Either[ErrorMessage, Int]] = {
    val operation = Operation(
      config = config,
      apiName = "PUT Object",
      method = "PUT",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 201 +: // Object created
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes))
        Future { Right(response.getStatusCode) } else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }

  }

  // UploadMultipart does Upload object multipart.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/object/multipart/upload_multipart.html
  def uploadMultipart(
      objectKey: String,
      input: UploadMultipartInput): Future[Either[ErrorMessage, Int]] = {
    val operation = Operation(
      config = config,
      apiName = "Upload Multipart",
      method = "PUT",
      requestUri = "/<bucket-name>/<object-key>",
      statusCodes = 201 +: // Object multipart created
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName,
      objectKey = objectKey
    )

    val futureResponse = QSRequest(operation, input).send()

    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
                                             operation.statusCodes))
        Future { Right(response.getStatusCode) } else {
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

  case class AbortMultipartUploadInput(
      // Object multipart upload ID
      uploadID: String
  ) extends Input {

    require(uploadID != null, "uploadID can't be empty")
    require(uploadID.nonEmpty, """uploadID can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "upload_id")
    def getUploadID = this.uploadID

  }

  case class CompleteMultipartUploadInput(
      // Object multipart upload ID
      uploadID: String,
      // MD5sum of the object part
      eTag: Option[String] = None,
      // Encryption algorithm of the object
      xQSEncryptionCustomerAlgorithm: Option[String] = None,
      // Encryption key of the object
      xQSEncryptionCustomerKey: Option[String] = None,
      // MD5sum of encryption key
      xQSEncryptionCustomerKeyMD5: Option[String] = None,
      // Object parts
      objectParts: Option[List[ObjectPartModel]] = None
  ) extends Input {

    require(uploadID != null, "uploadID can't be empty")
    require(uploadID.nonEmpty, """uploadID can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "upload_id")
    def getUploadID = this.uploadID

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "ETag")
    def getETag = this.eTag
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm = this.xQSEncryptionCustomerAlgorithm
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey = this.xQSEncryptionCustomerKey
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5 = this.xQSEncryptionCustomerKeyMD5

    @ParamAnnotation(location = QSConstants.ParamsLocationElement,
                     name = "object_parts")
    def getObjectParts = this.objectParts

  }

  case class DeleteObjectInput() extends Input

  case class GetObjectInput(
      // Specified the Cache-Control response header
      responseCacheControl: Option[String] = None,
      // Specified the Content-Disposition response header
      responseContentDisposition: Option[String] = None,
      // Specified the Content-Encoding response header
      responseContentEncoding: Option[String] = None,
      // Specified the Content-Language response header
      responseContentLanguage: Option[String] = None,
      // Specified the Content-Type response header
      responseContentType: Option[String] = None,
      // Specified the Expires response header
      responseExpires: Option[String] = None,
      // Check whether the ETag matches
      ifMatch: Option[String] = None,
      // Check whether the object has been modified
      ifModifiedSince: Option[String] = None,
      // Check whether the ETag does not match
      ifNoneMatch: Option[String] = None,
      // Check whether the object has not been modified
      ifUnmodifiedSince: Option[String] = None,
      // Specified range of the object
      range: Option[String] = None,
      // Encryption algorithm of the object
      xQSEncryptionCustomerAlgorithm: Option[String] = None,
      // Encryption key of the object
      xQSEncryptionCustomerKey: Option[String] = None,
      // MD5sum of encryption key
      xQSEncryptionCustomerKeyMD5: Option[String] = None
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "response-cache-control")
    def getResponseCacheControl = this.responseCacheControl
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "response-content-disposition")
    def getResponseContentDisposition = this.responseContentDisposition
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "response-content-encoding")
    def getResponseContentEncoding = this.responseContentEncoding
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "response-content-language")
    def getResponseContentLanguage = this.responseContentLanguage
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "response-content-type")
    def getResponseContentType = this.responseContentType
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "response-expires")
    def getResponseExpires = this.responseExpires

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-Match")
    def getIfMatch = this.ifMatch
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-Modified-Since")
    def getIfModifiedSince = this.ifModifiedSince
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-None-Match")
    def getIfNoneMatch = this.ifNoneMatch
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-Unmodified-Since")
    def getIfUnmodifiedSince = this.ifUnmodifiedSince
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Range")
    def getRange = this.range
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm = this.xQSEncryptionCustomerAlgorithm
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey = this.xQSEncryptionCustomerKey
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5 = this.xQSEncryptionCustomerKeyMD5

  }
  case class GetObjectOutput(
      // Range of response data content
      `Content-Range`: Option[String] = None,
      // MD5sum of the object
      `ETag`: Option[String] = None,
      // Encryption algorithm of the object
      `X-QS-Encryption-Customer-Algorithm`: Option[String] = None
      // The response body
      ,
      body: Array[Byte] = null
  ) extends Output

  case class HeadObjectInput(
      // Check whether the ETag matches
      ifMatch: Option[String] = None,
      // Check whether the object has been modified
      ifModifiedSince: Option[String] = None,
      // Check whether the ETag does not match
      ifNoneMatch: Option[String] = None,
      // Check whether the object has not been modified
      ifUnmodifiedSince: Option[String] = None,
      // Encryption algorithm of the object
      xQSEncryptionCustomerAlgorithm: Option[String] = None,
      // Encryption key of the object
      xQSEncryptionCustomerKey: Option[String] = None,
      // MD5sum of encryption key
      xQSEncryptionCustomerKeyMD5: Option[String] = None
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-Match")
    def getIfMatch = this.ifMatch
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-Modified-Since")
    def getIfModifiedSince = this.ifModifiedSince
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-None-Match")
    def getIfNoneMatch = this.ifNoneMatch
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "If-Unmodified-Since")
    def getIfUnmodifiedSince = this.ifUnmodifiedSince
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm = this.xQSEncryptionCustomerAlgorithm
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey = this.xQSEncryptionCustomerKey
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5 = this.xQSEncryptionCustomerKeyMD5

  }
  case class HeadObjectOutput(
      // Object content length
      `Content-Length`: Option[Int] = None,
      // Object content type
      `Content-Type`: Option[String] = None,
      // MD5sum of the object
      `ETag`: Option[String] = None,
      `Last-Modified`: Option[String] = None,
      // Encryption algorithm of the object
      `X-QS-Encryption-Customer-Algorithm`: Option[String] = None
  ) extends Output

  case class InitiateMultipartUploadInput(
      // Object content type
      contentType: Option[String] = None,
      // Encryption algorithm of the object
      xQSEncryptionCustomerAlgorithm: Option[String] = None,
      // Encryption key of the object
      xQSEncryptionCustomerKey: Option[String] = None,
      // MD5sum of encryption key
      xQSEncryptionCustomerKeyMD5: Option[String] = None
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Content-Type")
    def getContentType = this.contentType
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm = this.xQSEncryptionCustomerAlgorithm
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey = this.xQSEncryptionCustomerKey
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5 = this.xQSEncryptionCustomerKeyMD5

  }
  case class InitiateMultipartUploadOutput(
      // Encryption algorithm of the object
      `X-QS-Encryption-Customer-Algorithm`: Option[String] = None,
      // Bucket name
      `bucket`: Option[String] = None,
      // Object key
      `key`: Option[String] = None,
      // Object multipart upload ID
      `upload_id`: Option[String] = None
  ) extends Output

  case class ListMultipartInput(
      // Limit results count
      limit: Option[Int] = None,
      // Object multipart upload part number
      partNumberMarker: Option[Int] = None,
      // Object multipart upload ID
      uploadID: String
  ) extends Input {

    require(uploadID != null, "uploadID can't be empty")
    require(uploadID.nonEmpty, """uploadID can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "limit")
    def getLimit = this.limit
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "part_number_marker")
    def getPartNumberMarker = this.partNumberMarker
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "upload_id")
    def getUploadID = this.uploadID

  }
  case class ListMultipartOutput(
      // Object multipart count
      `count`: Option[Int] = None,
      // Object parts
      `object_parts`: Option[List[ObjectPartModel]] = None
  ) extends Output

  case class OptionsObjectInput(
      // Request headers
      accessControlRequestHeaders: Option[String] = None,
      // Request method
      accessControlRequestMethod: String,
      // Request origin
      origin: String
  ) extends Input {

    require(accessControlRequestMethod != null,
            "accessControlRequestMethod can't be empty")
    require(accessControlRequestMethod.nonEmpty,
            """accessControlRequestMethod can't be empty""")

    require(origin != null, "origin can't be empty")
    require(origin.nonEmpty, """origin can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Access-Control-Request-Headers")
    def getAccessControlRequestHeaders = this.accessControlRequestHeaders
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Access-Control-Request-Method")
    def getAccessControlRequestMethod = this.accessControlRequestMethod
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Origin")
    def getOrigin = this.origin

  }
  case class OptionsObjectOutput(
      // Allowed headers
      `Access-Control-Allow-Headers`: Option[String] = None,
      // Allowed methods
      `Access-Control-Allow-Methods`: Option[String] = None,
      // Allowed origin
      `Access-Control-Allow-Origin`: Option[String] = None,
      // Expose headers
      `Access-Control-Expose-Headers`: Option[String] = None,
      // Max age
      `Access-Control-Max-Age`: Option[String] = None
  ) extends Output

  case class PutObjectInput(
      // Object content size
      contentLength: Int,
      // Object MD5sum
      contentMD5: Option[String] = None,
      // Object content type
      contentType: Option[String] = None,
      // Used to indicate that particular server behaviors are required by the client
      expect: Option[String] = None,
      // Copy source, format (/<bucket-name>/<object-key>)
      xQSCopySource: Option[String] = None,
      // Encryption algorithm of the object
      xQSCopySourceEncryptionCustomerAlgorithm: Option[String] = None,
      // Encryption key of the object
      xQSCopySourceEncryptionCustomerKey: Option[String] = None,
      // MD5sum of encryption key
      xQSCopySourceEncryptionCustomerKeyMD5: Option[String] = None,
      // Check whether the copy source matches
      xQSCopySourceIfMatch: Option[String] = None,
      // Check whether the copy source has been modified
      xQSCopySourceIfModifiedSince: Option[String] = None,
      // Check whether the copy source does not match
      xQSCopySourceIfNoneMatch: Option[String] = None,
      // Check whether the copy source has not been modified
      xQSCopySourceIfUnmodifiedSince: Option[String] = None,
      // Encryption algorithm of the object
      xQSEncryptionCustomerAlgorithm: Option[String] = None,
      // Encryption key of the object
      xQSEncryptionCustomerKey: Option[String] = None,
      // MD5sum of encryption key
      xQSEncryptionCustomerKeyMD5: Option[String] = None,
      // Check whether fetch target object has not been modified
      xQSFetchIfUnmodifiedSince: Option[String] = None,
      // Fetch source, should be a valid url
      xQSFetchSource: Option[String] = None,
      // Move source, format (/<bucket-name>/<object-key>)
      xQSMoveSource: Option[String] = None,
      body: File = null
      // The request body
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Content-Length")
    def getContentLength = this.contentLength
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Content-MD5")
    def getContentMD5 = this.contentMD5
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Content-Type")
    def getContentType = this.contentType
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Expect")
    def getExpect = this.expect
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source")
    def getXQSCopySource = this.xQSCopySource
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source-Encryption-Customer-Algorithm")
    def getXQSCopySourceEncryptionCustomerAlgorithm =
      this.xQSCopySourceEncryptionCustomerAlgorithm
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source-Encryption-Customer-Key")
    def getXQSCopySourceEncryptionCustomerKey =
      this.xQSCopySourceEncryptionCustomerKey
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source-Encryption-Customer-Key-MD5")
    def getXQSCopySourceEncryptionCustomerKeyMD5 =
      this.xQSCopySourceEncryptionCustomerKeyMD5
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source-If-Match")
    def getXQSCopySourceIfMatch = this.xQSCopySourceIfMatch
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source-If-Modified-Since")
    def getXQSCopySourceIfModifiedSince = this.xQSCopySourceIfModifiedSince
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source-If-None-Match")
    def getXQSCopySourceIfNoneMatch = this.xQSCopySourceIfNoneMatch
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Copy-Source-If-Unmodified-Since")
    def getXQSCopySourceIfUnmodifiedSince = this.xQSCopySourceIfUnmodifiedSince
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm = this.xQSEncryptionCustomerAlgorithm
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey = this.xQSEncryptionCustomerKey
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5 = this.xQSEncryptionCustomerKeyMD5
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Fetch-If-Unmodified-Since")
    def getXQSFetchIfUnmodifiedSince = this.xQSFetchIfUnmodifiedSince
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Fetch-Source")
    def getXQSFetchSource = this.xQSFetchSource
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Move-Source")
    def getXQSMoveSource = this.xQSMoveSource

    @ParamAnnotation(location = QSConstants.ParamsLocationBody, name = "Body")
    def getBody = this.body

  }

  case class UploadMultipartInput(
      // Object multipart upload part number
      partNumber: Int,
      // Object multipart upload ID
      uploadID: String,
      // Object multipart content length
      contentLength: Option[Int] = None,
      // Object multipart content MD5sum
      contentMD5: Option[String] = None,
      // Encryption algorithm of the object
      xQSEncryptionCustomerAlgorithm: Option[String] = None,
      // Encryption key of the object
      xQSEncryptionCustomerKey: Option[String] = None,
      // MD5sum of encryption key
      xQSEncryptionCustomerKeyMD5: Option[String] = None,
      body: File = null
      // The request body
  ) extends Input {

    require(uploadID != null, "uploadID can't be empty")
    require(uploadID.nonEmpty, """uploadID can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "part_number")
    def getPartNumber = this.partNumber
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "upload_id")
    def getUploadID = this.uploadID

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Content-Length")
    def getContentLength = this.contentLength
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Content-MD5")
    def getContentMD5 = this.contentMD5
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Algorithm")
    def getXQSEncryptionCustomerAlgorithm = this.xQSEncryptionCustomerAlgorithm
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key")
    def getXQSEncryptionCustomerKey = this.xQSEncryptionCustomerKey
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "X-QS-Encryption-Customer-Key-MD5")
    def getXQSEncryptionCustomerKeyMD5 = this.xQSEncryptionCustomerKeyMD5

    @ParamAnnotation(location = QSConstants.ParamsLocationBody, name = "Body")
    def getBody = this.body

  }

}
