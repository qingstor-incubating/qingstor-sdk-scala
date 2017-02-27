package com.qingstor.sdk.service

import java.time.ZonedDateTime

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Bucket._
import com.qingstor.sdk.service.Types._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.util.{JsonUtil, QSLogger, QSRequestUtil, SecurityUtil}
import CustomJsonProtocol._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}

class Bucket(_config: QSConfig, _bucketName: String, _zone: String)(
  implicit val system: ActorSystem,
  val mat: ActorMaterializer,
  val ec: ExecutionContextExecutor) {
  val config: QSConfig = _config
  val bucketName: String = _bucketName
  val zone: String = _zone

  def listObjects(input: Input): Future[Either[ErrorMessage, ListObjectsOutput]] = {
    val operation = Operation(
      config = this.config,
      apiName = "List Objects",
      method = "GET",
      requestUri = "/",
      statusCodes = Array[Int](200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[QSHttpResponse]()
    ResponseUnpacker.unpackToOutputOrErrorMessage[ListObjectsOutput](futureResponse, operation.statusCodes)
  }

  def deleteMultipleObjects(input: Input)
  : Future[Either[ErrorMessage, DeleteMultipleObjectsOutput]] = {
    val operation = Operation(
      config = this.config,
      apiName = "Delete Multiple Objects",
      method = "POST",
      requestUri = "/?delete",
      statusCodes = Array[Int](200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[QSHttpResponse]()
    ResponseUnpacker.unpackToOutputOrErrorMessage[DeleteMultipleObjectsOutput](futureResponse,
      operation.statusCodes)
  }

  def headBucket(): Future[Int] = {
    val operation = Operation(
      config = this.config,
      apiName = "HEAD Bucket",
      method = "HEAD",
      requestUri = "/",
      statusCodes = Array[Int](200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation).send[QSHttpResponse]()
    futureResponse.map( _.getStatusCode )
  }

  def getBucketStatistics(input: Input): Future[Either[ErrorMessage, GetBucketStatisticsOutput]] = {
    val operation = Operation(
      config = this.config,
      apiName = "GET Bucket Statistics",
      method = "GET",
      requestUri = "/?stats",
      statusCodes = Array[Int](200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[QSHttpResponse]()
    ResponseUnpacker.unpackToOutputOrErrorMessage[GetBucketStatisticsOutput](futureResponse,
      operation.statusCodes)
  }

  def getBucketACL(input: Input): Future[Either[ErrorMessage, GetBucketACLOuput]] = {
    val operation = Operation(
      config = this.config,
      apiName = "GET Bucket ACL",
      method = "GET",
      requestUri = "/?acl",
      statusCodes = Array[Int](200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[QSHttpResponse]()
    ResponseUnpacker.unpackToOutputOrErrorMessage[GetBucketACLOuput](futureResponse,
      operation.statusCodes)
  }

  def getBucketCORS(input: Input): Future[Either[ErrorMessage, GetBucketCORSOutput]] = {
    val operation = Operation(
      config = this.config,
      apiName = "GET Bucket CORS",
      method = "GET",
      requestUri = "/?cors",
      statusCodes = Array[Int](200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[QSHttpResponse]()
    ResponseUnpacker.unpackToOutputOrErrorMessage[GetBucketCORSOutput](futureResponse,
      operation.statusCodes)
  }

  def putBucketCORS(input: Input): Future[Either[ErrorMessage, Int]] = {
    val operation = Operation(
      config = this.config,
      apiName = "PUT Bucket CORS",
      method = "PUT",
      requestUri = "/?cors",
      statusCodes = Array[Int](200),
      bucketName = this.bucketName,
      zone = this.zone
    )
    val futureResponse = QSRequest(operation, input).send[QSHttpResponse]()
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, operation.statusCodes))
        Future { Right(response.getStatusCode) }
      else {
        ResponseUnpacker.unpackToErrorMessage(response).map(Left(_))
      }
    }
  }
}

object Bucket {
  def apply(config: QSConfig, bucketName: String, zone: String)(
    implicit system: ActorSystem,
    mat: ActorMaterializer,
    ec: ExecutionContextExecutor): Bucket =
    new Bucket(config, bucketName, zone)

  case class ListObjectsInput(prefix: String = null,
                              delimiter: Char = 0,
                              marker: String = null,
                              limit: Int = 200 ) extends Input{
    require(limit <= 1000, "limit can't larger than 1000")
    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "prefix")
    def getPrefix: String = this.prefix

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "delimiter")
    def getDelimiter: Char = this.delimiter

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "marker")
    def getMarker: String = this.marker

    @ParamAnnotation(location = QSConstants.ParamsLocationParam, name = "limit")
    def getLimit: Int = this.limit
  }
  case class ListObjectsOutput(name: String, keys: List[ObjectModel], prefix: String,
                               owner: OwnerModel, delimiter: Char, limit: Int,
                               marker: String, next_marker: String, common_prefixes: List[String]) extends Output

  case class DeleteMultipleObjectsInput(contentMD5: String,
                                        quiet: Boolean = false,
                                        objects: List[ObjectKeyModel]) extends Input {
    require(contentMD5 != null && contentMD5 != "", "ContentMD5 can't be empty")
    require(objects != null && objects.nonEmpty, "Objects to be deleted can't be empty")

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-MD5")
    def getContentMD5: String = this.contentMD5

    @ParamAnnotation(location = QSConstants.ParamsLocationElement, name = "quiet")
    def getQuiet: Boolean = this.quiet

    @ParamAnnotation(location = QSConstants.ParamsLocationElement, name = "objects")
    def getObjects: List[ObjectKeyModel] = this.objects

    def setContentMD5(md5: String): DeleteMultipleObjectsInput = {
      DeleteMultipleObjectsInput(md5, this.getQuiet, this.getObjects)
    }
  }
  case class DeleteMultipleObjectsOutput(deleted: List[ObjectKeyModel] = List.empty,
                                         errors: List[DeleteErrorModel] =
                                         List.empty) extends Output

  def getContentMD5OfDeleteMultipleObjectsInput(input: DeleteMultipleObjectsInput): String = {
    val elements = QSRequestUtil.getRequestParams(input, QSConstants.ParamsLocationElement)
    val content = JsonUtil.encode(elements).compactPrint
    SecurityUtil.encodeToBase64String(SecurityUtil.getMD5(content))
  }

  case class GetBucketStatisticsInput() extends Input
  case class GetBucketStatisticsOutput(count: Int, size: Int, location: String, created: ZonedDateTime, status: String) extends Output

  case class GetBucketACLInput() extends Input
  case class GetBucketACLOuput(owner: OwnerModel, acl: List[ACLModel]) extends Output

  case class GetBucketCORSInput() extends Input
  case class GetBucketCORSOutput(cors_rules: List[CORSRulesModel]) extends Output

  case class PutBucketCORSInput(cORSRules: List[CORSRulesModel]) extends Input {
    @ParamAnnotation(location = QSConstants.ParamsLocationElement, name = "cors_rules")
    def getCORSRules: List[CORSRulesModel] = this.cORSRules
  }
}
