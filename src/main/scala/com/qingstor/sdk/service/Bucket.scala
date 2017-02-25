package com.qingstor.sdk.service

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Bucket.DeleteMultipleObjectsOutput
import com.qingstor.sdk.service.Types.{DeleteErrorModel, ObjectModel}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.util.{JsonUtil, QSRequestUtil, SecurityUtil}
import spray.json.JsValue
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
    futureResponse.flatMap { response =>
      val futureJson = Unmarshal(response.getEntity).to[JsValue]
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode,
        operation.statusCodes))
        futureJson.map{ json =>
          Right(json.convertTo[DeleteMultipleObjectsOutput])
        }
      else
        futureJson.map(json => Left(json.convertTo[ErrorMessage]))
    }
  }
}

object Bucket {
  def apply(config: QSConfig, bucketName: String, zone: String)(
    implicit system: ActorSystem,
    mat: ActorMaterializer,
    ec: ExecutionContextExecutor): Bucket =
    new Bucket(config, bucketName, zone)

  case class DeleteMultipleObjectsInput(contentMD5: String,
                                        quiet: Boolean = false,
                                        objects: List[ObjectModel]) extends Input {
    require(contentMD5 != null && contentMD5 != "", "ContentMD5 can't be empty")
    require(objects != null && objects.nonEmpty, "Objects to be deleted can't be empty")

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Content-MD5")
    def getContentMD5: String = this.contentMD5

    @ParamAnnotation(location = QSConstants.ParamsLocationElement, name = "quiet")
    def getQuiet: Boolean = this.quiet

    @ParamAnnotation(location = QSConstants.ParamsLocationElement, name = "objects")
    def getObjects: List[ObjectModel] = this.objects

    def setContentMD5(md5: String): DeleteMultipleObjectsInput = {
      DeleteMultipleObjectsInput(md5, this.getQuiet, this.getObjects)
    }
  }

  case class DeleteMultipleObjectsOutput(deleted: List[ObjectModel] = List.empty,
                                         errors: List[DeleteErrorModel] =
                                         List.empty) extends Output
  case class QuietDeleteMultipleObjectsOuput(errors: List[DeleteErrorModel] = List.empty)

  def getContentMD5OfDeleteMultipleObjectsInput(input: DeleteMultipleObjectsInput): String = {
    val elements = QSRequestUtil.getRequestParams(input, QSConstants.ParamsLocationElement)
    val content = JsonUtil.encode(elements).compactPrint
    SecurityUtil.encodeToBase64String(SecurityUtil.getMD5(content))
  }
}
