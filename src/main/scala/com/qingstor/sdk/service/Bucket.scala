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
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket",
      method = "DELETE",
      requestUri = "/<bucket-name>",
      statusCodes = 204 +: // Bucket deleted
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeleteCORS does Delete CORS information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/cors/delete_cors.html
  def deleteBucketCORS(
      input: DeleteBucketCORSInput): Future[DeleteBucketCORSOutput] = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket CORS",
      method = "DELETE",
      requestUri = "/<bucket-name>?cors",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketCORSOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeleteExternalMirror does Delete external mirror of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/external_mirror/delete_external_mirror.html
  def deleteBucketExternalMirror(input: DeleteBucketExternalMirrorInput)
    : Future[DeleteBucketExternalMirrorOutput] = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket External Mirror",
      method = "DELETE",
      requestUri = "/<bucket-name>?mirror",
      statusCodes = 204 +: // No content
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketExternalMirrorOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeletePolicy does Delete policy information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/policy/delete_policy.html
  def deleteBucketPolicy(
      input: DeleteBucketPolicyInput): Future[DeleteBucketPolicyOutput] = {
    val operation = Operation(
      config = config,
      apiName = "DELETE Bucket Policy",
      method = "DELETE",
      requestUri = "/<bucket-name>?policy",
      statusCodes = 204 +: // No content
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[DeleteBucketPolicyOutput](
      futureResponse,
      operation.statusCodes)
  }

  // DeleteMultipleObjects does Delete multiple objects from the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/delete_multiple.html
  def deleteMultipleObjects(input: DeleteMultipleObjectsInput)
    : Future[DeleteMultipleObjectsOutput] = {
    val operation = Operation(
      config = config,
      apiName = "Delete Multiple Objects",
      method = "POST",
      requestUri = "/<bucket-name>?delete",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutput[DeleteMultipleObjectsOutput](
      futureResponse,
      operation.statusCodes)
  }

  // GetACL does Get ACL information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/get_acl.html
  def getBucketACL(input: GetBucketACLInput): Future[GetBucketACLOutput] = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket ACL",
      method = "GET",
      requestUri = "/<bucket-name>?acl",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutput[GetBucketACLOutput](futureResponse,
                                                        operation.statusCodes)
  }

  // GetCORS does Get CORS information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/cors/get_cors.html
  def getBucketCORS(input: GetBucketCORSInput): Future[GetBucketCORSOutput] = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket CORS",
      method = "GET",
      requestUri = "/<bucket-name>?cors",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutput[GetBucketCORSOutput](futureResponse,
                                                         operation.statusCodes)
  }

  // GetExternalMirror does Get external mirror of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/external_mirror/get_external_mirror.html
  def getBucketExternalMirror(input: GetBucketExternalMirrorInput)
    : Future[GetBucketExternalMirrorOutput] = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket External Mirror",
      method = "GET",
      requestUri = "/<bucket-name>?mirror",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutput[GetBucketExternalMirrorOutput](
      futureResponse,
      operation.statusCodes)
  }

  // GetPolicy does Get policy information of the bucket.
  // Documentation URL: https://https://docs.qingcloud.com/qingstor/api/bucket/policy/get_policy.html
  def getBucketPolicy(
      input: GetBucketPolicyInput): Future[GetBucketPolicyOutput] = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket Policy",
      method = "GET",
      requestUri = "/<bucket-name>?policy",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutput[GetBucketPolicyOutput](
      futureResponse,
      operation.statusCodes)
  }

  // GetStatistics does Get statistics information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/get_stats.html
  def getBucketStatistics(
      input: GetBucketStatisticsInput): Future[GetBucketStatisticsOutput] = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket Statistics",
      method = "GET",
      requestUri = "/<bucket-name>?stats",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutput[GetBucketStatisticsOutput](
      futureResponse,
      operation.statusCodes)
  }

  // Head does Check whether the bucket exists and available.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/head.html
  def headBucket(input: HeadBucketInput): Future[HeadBucketOutput] = {
    val operation = Operation(
      config = config,
      apiName = "HEAD Bucket",
      method = "HEAD",
      requestUri = "/<bucket-name>",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[HeadBucketOutput](
      futureResponse,
      operation.statusCodes)
  }

  // ListObjects does Retrieve the object list in a bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/get.html
  def listObjects(input: ListObjectsInput): Future[ListObjectsOutput] = {
    val operation = Operation(
      config = config,
      apiName = "GET Bucket (List Objects)",
      method = "GET",
      requestUri = "/<bucket-name>",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker
      .unpackToOutput[ListObjectsOutput](futureResponse, operation.statusCodes)
  }

  // Put does Create a new bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/put.html
  def putBucket(input: PutBucketInput): Future[PutBucketOutput] = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket",
      method = "PUT",
      requestUri = "/<bucket-name>",
      statusCodes = 201 +: // Bucket created
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutACL does Set ACL information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/put_acl.html
  def putBucketACL(input: PutBucketACLInput): Future[PutBucketACLOutput] = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket ACL",
      method = "PUT",
      requestUri = "/<bucket-name>?acl",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketACLOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutCORS does Set CORS information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/cors/put_cors.html
  def putBucketCORS(input: PutBucketCORSInput): Future[PutBucketCORSOutput] = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket CORS",
      method = "PUT",
      requestUri = "/<bucket-name>?cors",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketCORSOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutExternalMirror does Set external mirror of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/external_mirror/put_external_mirror.html
  def putBucketExternalMirror(input: PutBucketExternalMirrorInput)
    : Future[PutBucketExternalMirrorOutput] = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket External Mirror",
      method = "PUT",
      requestUri = "/<bucket-name>?mirror",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketExternalMirrorOutput](
      futureResponse,
      operation.statusCodes)
  }

  // PutPolicy does Set policy information of the bucket.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/bucket/policy/put_policy.html
  def putBucketPolicy(
      input: PutBucketPolicyInput): Future[PutBucketPolicyOutput] = {
    val operation = Operation(
      config = config,
      apiName = "PUT Bucket Policy",
      method = "PUT",
      requestUri = "/<bucket-name>?policy",
      statusCodes = 200 +: // OK
        Array[Int](),
      zone = this.zone,
      bucketName = this.bucketName
    )

    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToGenericOutput[PutBucketPolicyOutput](
      futureResponse,
      operation.statusCodes)
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
