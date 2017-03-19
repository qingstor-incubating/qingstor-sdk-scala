package com.qingstor.sdk.service

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Types._
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.QSJsonProtocol._
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContextExecutor, Future}
import com.qingstor.sdk.service.Bucket._

class Bucket(_config: QSConfig, _bucketName: String, _zone: String) {
  implicit val system = QSConstants.QingStorSystem
  implicit val materializer = ActorMaterializer()
  implicit val ece: ExecutionContextExecutor = system.dispatcher
  val config: QSConfig = _config
  val bucketName: String = _bucketName
  val zone: String = _zone

  // Delete does Delete a bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/delete.html
  def deleteBucket(input: DeleteBucketInput): Future[DeleteBucketOutput] = {
    val request = deleteBucketRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeleteRequest creates request and output object of DeleteBucket.
  def deleteBucketRequest(input: DeleteBucketInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket",
      method = "DELETE",
      requestUri = "/{bucketName}",
      statusCodes = 204 +: // Bucket deleted
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // DeleteCORS does Delete CORS information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/cors/delete_cors.html
  def deleteBucketCORS(
      input: DeleteBucketCORSInput): Future[DeleteBucketCORSOutput] = {
    val request = deleteBucketCORSRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketCORSOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeleteCORSRequest creates request and output object of DeleteBucketCORS.
  def deleteBucketCORSRequest(input: DeleteBucketCORSInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket CORS",
      method = "DELETE",
      requestUri = "/{bucketName}?cors",
      statusCodes = 204 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // DeleteExternalMirror does Delete external mirror of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/external_mirror/delete_external_mirror.html
  def deleteBucketExternalMirror(input: DeleteBucketExternalMirrorInput)
    : Future[DeleteBucketExternalMirrorOutput] = {
    val request = deleteBucketExternalMirrorRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketExternalMirrorOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeleteExternalMirrorRequest creates request and output object of DeleteBucketExternalMirror.
  def deleteBucketExternalMirrorRequest(
      input: DeleteBucketExternalMirrorInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket External Mirror",
      method = "DELETE",
      requestUri = "/{bucketName}?mirror",
      statusCodes = 204 +: // No content
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // DeletePolicy does Delete policy information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/policy/delete_policy.html
  def deleteBucketPolicy(
      input: DeleteBucketPolicyInput): Future[DeleteBucketPolicyOutput] = {
    val request = deleteBucketPolicyRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketPolicyOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeletePolicyRequest creates request and output object of DeleteBucketPolicy.
  def deleteBucketPolicyRequest(input: DeleteBucketPolicyInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket Policy",
      method = "DELETE",
      requestUri = "/{bucketName}?policy",
      statusCodes = 204 +: // No content
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // DeleteMultipleObjects does Delete multiple objects from the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/delete_multiple.html
  def deleteMultipleObjects(input: DeleteMultipleObjectsInput)
    : Future[DeleteMultipleObjectsOutput] = {
    val request = deleteMultipleObjectsRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToOutput[DeleteMultipleObjectsOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeleteMultipleObjectsRequest creates request and output object of DeleteMultipleObjects.
  def deleteMultipleObjectsRequest(
      input: DeleteMultipleObjectsInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "Delete Multiple Objects",
      method = "POST",
      requestUri = "/{bucketName}?delete",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // GetACL does Get ACL information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/get_acl.html
  def getBucketACL(input: GetBucketACLInput): Future[GetBucketACLOutput] = {
    val request = getBucketACLRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToOutput[GetBucketACLOutput](futureResponse,
                                                        operation.statusCodes)
  }

  // GetACLRequest creates request and output object of GetBucketACL.
  def getBucketACLRequest(input: GetBucketACLInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket ACL",
      method = "GET",
      requestUri = "/{bucketName}?acl",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // GetCORS does Get CORS information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/cors/get_cors.html
  def getBucketCORS(input: GetBucketCORSInput): Future[GetBucketCORSOutput] = {
    val request = getBucketCORSRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToOutput[GetBucketCORSOutput](futureResponse,
                                                         operation.statusCodes)
  }

  // GetCORSRequest creates request and output object of GetBucketCORS.
  def getBucketCORSRequest(input: GetBucketCORSInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket CORS",
      method = "GET",
      requestUri = "/{bucketName}?cors",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // GetExternalMirror does Get external mirror of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/external_mirror/get_external_mirror.html
  def getBucketExternalMirror(input: GetBucketExternalMirrorInput)
    : Future[GetBucketExternalMirrorOutput] = {
    val request = getBucketExternalMirrorRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToOutput[GetBucketExternalMirrorOutput](
      futureResponse,
      operation.statusCodes)
  }

  // GetExternalMirrorRequest creates request and output object of GetBucketExternalMirror.
  def getBucketExternalMirrorRequest(
      input: GetBucketExternalMirrorInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket External Mirror",
      method = "GET",
      requestUri = "/{bucketName}?mirror",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // GetPolicy does Get policy information of the bucket.
  // Documentation URL: https://https://docs.qingcloud.com/qingstor/api/bucket/policy/get_policy.html
  def getBucketPolicy(
      input: GetBucketPolicyInput): Future[GetBucketPolicyOutput] = {
    val request = getBucketPolicyRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToOutput[GetBucketPolicyOutput](
      futureResponse,
      operation.statusCodes)
  }

  // GetPolicyRequest creates request and output object of GetBucketPolicy.
  def getBucketPolicyRequest(input: GetBucketPolicyInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket Policy",
      method = "GET",
      requestUri = "/{bucketName}?policy",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // GetStatistics does Get statistics information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/get_stats.html
  def getBucketStatistics(
      input: GetBucketStatisticsInput): Future[GetBucketStatisticsOutput] = {
    val request = getBucketStatisticsRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToOutput[GetBucketStatisticsOutput](
      futureResponse,
      operation.statusCodes)
  }

  // GetStatisticsRequest creates request and output object of GetBucketStatistics.
  def getBucketStatisticsRequest(input: GetBucketStatisticsInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket Statistics",
      method = "GET",
      requestUri = "/{bucketName}?stats",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // Head does Check whether the bucket exists and available.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/head.html
  def headBucket(input: HeadBucketInput): Future[HeadBucketOutput] = {
    val request = headBucketRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[HeadBucketOutput](
      futureResponse,
      operation.statusCodes)
  }

  // HeadRequest creates request and output object of HeadBucket.
  def headBucketRequest(input: HeadBucketInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "HEAD Bucket",
      method = "HEAD",
      requestUri = "/{bucketName}",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // ListMultipartUploads does List multipart uploads in the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/list_multipart_uploads.html
  def listMultipartUploads(
      input: ListMultipartUploadsInput): Future[ListMultipartUploadsOutput] = {
    val request = listMultipartUploadsRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToOutput[ListMultipartUploadsOutput](
      futureResponse,
      operation.statusCodes)
  }

  // ListMultipartUploadsRequest creates request and output object of ListMultipartUploads.
  def listMultipartUploadsRequest(
      input: ListMultipartUploadsInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "List Multipart Uploads",
      method = "GET",
      requestUri = "/{bucketName}?uploads",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // ListObjects does Retrieve the object list in a bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/get.html
  def listObjects(input: ListObjectsInput): Future[ListObjectsOutput] = {
    val request = listObjectsRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker
      .unpackToOutput[ListObjectsOutput](futureResponse, operation.statusCodes)
  }

  // ListObjectsRequest creates request and output object of ListObjects.
  def listObjectsRequest(input: ListObjectsInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket (List Objects)",
      method = "GET",
      requestUri = "/{bucketName}",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // Put does Create a new bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/put.html
  def putBucket(input: PutBucketInput): Future[PutBucketOutput] = {
    val request = putBucketRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutRequest creates request and output object of PutBucket.
  def putBucketRequest(input: PutBucketInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket",
      method = "PUT",
      requestUri = "/{bucketName}",
      statusCodes = 201 +: // Bucket created
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // PutACL does Set ACL information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/put_acl.html
  def putBucketACL(input: PutBucketACLInput): Future[PutBucketACLOutput] = {
    val request = putBucketACLRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketACLOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutACLRequest creates request and output object of PutBucketACL.
  def putBucketACLRequest(input: PutBucketACLInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket ACL",
      method = "PUT",
      requestUri = "/{bucketName}?acl",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // PutCORS does Set CORS information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/cors/put_cors.html
  def putBucketCORS(input: PutBucketCORSInput): Future[PutBucketCORSOutput] = {
    val request = putBucketCORSRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketCORSOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutCORSRequest creates request and output object of PutBucketCORS.
  def putBucketCORSRequest(input: PutBucketCORSInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket CORS",
      method = "PUT",
      requestUri = "/{bucketName}?cors",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // PutExternalMirror does Set external mirror of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/external_mirror/put_external_mirror.html
  def putBucketExternalMirror(input: PutBucketExternalMirrorInput)
    : Future[PutBucketExternalMirrorOutput] = {
    val request = putBucketExternalMirrorRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketExternalMirrorOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutExternalMirrorRequest creates request and output object of PutBucketExternalMirror.
  def putBucketExternalMirrorRequest(
      input: PutBucketExternalMirrorInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket External Mirror",
      method = "PUT",
      requestUri = "/{bucketName}?mirror",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

  // PutPolicy does Set policy information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/policy/put_policy.html
  def putBucketPolicy(
      input: PutBucketPolicyInput): Future[PutBucketPolicyOutput] = {
    val request = putBucketPolicyRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketPolicyOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutPolicyRequest creates request and output object of PutBucketPolicy.
  def putBucketPolicyRequest(input: PutBucketPolicyInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket Policy",
      method = "PUT",
      requestUri = "/{bucketName}?policy",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )
    QSRequest(operation, input)
  }

}

object Bucket {
  def apply(config: QSConfig, bucketName: String, zone: String): Bucket =
    new Bucket(config, bucketName, zone)

  case class DeleteBucketInput() extends Input
  case class DeleteBucketOutput() extends Output

  case class DeleteBucketCORSInput() extends Input
  case class DeleteBucketCORSOutput() extends Output

  case class DeleteBucketExternalMirrorInput() extends Input
  case class DeleteBucketExternalMirrorOutput() extends Output

  case class DeleteBucketPolicyInput() extends Input
  case class DeleteBucketPolicyOutput() extends Output

  case class DeleteMultipleObjectsInput(
      // Object MD5sum
      contentMD5: String,
      // A list of keys to delete
      objects: List[KeyModel],
      // Whether to return the list of deleted objects
      quiet: Option[Boolean] = None
  ) extends Input {

    require(contentMD5 != null, "contentMD5 can't be empty")
    require(contentMD5.nonEmpty, """contentMD5 can't be empty""")

    require(objects != null, "objects can't be empty")
    require(objects.nonEmpty, """objects can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Content-MD5")
    def getContentMD5 = this.contentMD5

    @ParamAnnotation(location = QSConstants.ParamsLocationElement,
                     name = "objects")
    def getObjects = this.objects
    @ParamAnnotation(location = QSConstants.ParamsLocationElement,
                     name = "quiet")
    def getQuiet = this.quiet

  }
  case class DeleteMultipleObjectsOutput(
      // List of deleted objects
      `deleted`: Option[List[KeyModel]] = None,
      // Error messages
      `errors`: Option[List[KeyDeleteErrorModel]] = None
  ) extends Output

  case class GetBucketACLInput() extends Input
  case class GetBucketACLOutput(
      // Bucket ACL rules
      `acl`: Option[List[ACLModel]] = None,
      // Bucket owner
      `owner`: Option[OwnerModel] = None
  ) extends Output

  case class GetBucketCORSInput() extends Input
  case class GetBucketCORSOutput(
      // Bucket CORS rules
      `cors_rules`: Option[List[CORSRuleModel]] = None
  ) extends Output

  case class GetBucketExternalMirrorInput() extends Input
  case class GetBucketExternalMirrorOutput(
      // Source site url
      `source_site`: Option[String] = None
  ) extends Output

  case class GetBucketPolicyInput() extends Input
  case class GetBucketPolicyOutput(
      // Bucket policy statement
      `statement`: Option[List[StatementModel]] = None
  ) extends Output

  case class GetBucketStatisticsInput() extends Input
  case class GetBucketStatisticsOutput(
      // Objects count in the bucket
      `count`: Option[Int] = None,
      // Bucket created time
      `created`: Option[String] = None,
      // QingCloud Zone ID
      `location`: Option[String] = None,
      // Bucket name
      `name`: Option[String] = None,
      // Bucket storage size
      `size`: Option[Int] = None,
      // Bucket status
      // status's available values: active, suspended
      `status`: Option[String] = None,
      // URL to access the bucket
      `url`: Option[String] = None
  ) extends Output

  case class HeadBucketInput() extends Input
  case class HeadBucketOutput() extends Output

  case class ListMultipartUploadsInput(
      // Put all keys that share a common prefix into a list
      delimiter: Option[String] = None,
      // Results count limit
      limit: Option[Int] = None,
      // Limit results to keys that start at this marker
      marker: Option[String] = None,
      // Limits results to keys that begin with the prefix
      prefix: Option[String] = None
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "delimiter")
    def getDelimiter = this.delimiter
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "limit")
    def getLimit = this.limit
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "marker")
    def getMarker = this.marker
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "prefix")
    def getPrefix = this.prefix

  }
  case class ListMultipartUploadsOutput(
      // Other object keys that share common prefixes
      `common_prefixes`: Option[List[String]] = None,
      // Delimiter that specified in request parameters
      `delimiter`: Option[String] = None,
      // Limit that specified in request parameters
      `limit`: Option[Int] = None,
      // Marker that specified in request parameters
      `marker`: Option[String] = None,
      // Bucket name
      `name`: Option[String] = None,
      // The last key in keys list
      `next_marker`: Option[String] = None,
      // Prefix that specified in request parameters
      `prefix`: Option[String] = None,
      // Multipart uploads
      `uploads`: Option[List[UploadsModel]] = None
  ) extends Output

  case class ListObjectsInput(
      // Put all keys that share a common prefix into a list
      delimiter: Option[String] = None,
      // Results count limit
      limit: Option[Int] = None,
      // Limit results to keys that start at this marker
      marker: Option[String] = None,
      // Limits results to keys that begin with the prefix
      prefix: Option[String] = None
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "delimiter")
    def getDelimiter = this.delimiter
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "limit")
    def getLimit = this.limit
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "marker")
    def getMarker = this.marker
    @ParamAnnotation(location = QSConstants.ParamsLocationParam,
                     name = "prefix")
    def getPrefix = this.prefix

  }
  case class ListObjectsOutput(
      // Other object keys that share common prefixes
      `common_prefixes`: Option[List[String]] = None,
      // Delimiter that specified in request parameters
      `delimiter`: Option[String] = None,
      // Object keys
      `keys`: Option[List[KeyModel]] = None,
      // Limit that specified in request parameters
      `limit`: Option[Int] = None,
      // Marker that specified in request parameters
      `marker`: Option[String] = None,
      // Bucket name
      `name`: Option[String] = None,
      // The last key in keys list
      `next_marker`: Option[String] = None,
      // Bucket owner
      `owner`: Option[OwnerModel] = None,
      // Prefix that specified in request parameters
      `prefix`: Option[String] = None
  ) extends Output

  case class PutBucketInput() extends Input
  case class PutBucketOutput() extends Output

  case class PutBucketACLInput(
      // Bucket ACL rules
      aCL: List[ACLModel]
  ) extends Input {

    require(aCL != null, "aCL can't be empty")
    require(aCL.nonEmpty, """aCL can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationElement,
                     name = "acl")
    def getACL = this.aCL

  }
  case class PutBucketACLOutput() extends Output

  case class PutBucketCORSInput(
      // Bucket CORS rules
      cORSRules: List[CORSRuleModel]
  ) extends Input {

    require(cORSRules != null, "cORSRules can't be empty")
    require(cORSRules.nonEmpty, """cORSRules can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationElement,
                     name = "cors_rules")
    def getCORSRules = this.cORSRules

  }
  case class PutBucketCORSOutput() extends Output

  case class PutBucketExternalMirrorInput(
      // Source site url
      sourceSite: String
  ) extends Input {

    require(sourceSite != null, "sourceSite can't be empty")
    require(sourceSite.nonEmpty, """sourceSite can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationElement,
                     name = "source_site")
    def getSourceSite = this.sourceSite

  }
  case class PutBucketExternalMirrorOutput() extends Output

  case class PutBucketPolicyInput(
      // Bucket policy statement
      statement: List[StatementModel]
  ) extends Input {

    require(statement != null, "statement can't be empty")
    require(statement.nonEmpty, """statement can't be empty""")

    @ParamAnnotation(location = QSConstants.ParamsLocationElement,
                     name = "statement")
    def getStatement = this.statement

  }
  case class PutBucketPolicyOutput() extends Output

}
