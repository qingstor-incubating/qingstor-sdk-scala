package com.qingstor.sdk.service

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Types.BucketModel
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.QingStor.ListBucketsOutput
import CustomJsonProtocol._
import spray.json.JsValue

import scala.concurrent.{ExecutionContextExecutor, Future}

class QingStor(private val _config: QSConfig)(
    implicit val system: ActorSystem,
    val mat: ActorMaterializer,
    val ec: ExecutionContextExecutor) {
  val config: QSConfig = _config

  def listBuckets(
      input: Input): Future[Either[ErrorMessage, ListBucketsOutput]] = {
    val operation = Operation(
      config = config,
      apiName = "GET Service",
      method = "GET",
      requestUri = "/",
      statusCodes = Array[Int](200)
    )
    val futureResponse = QSRequest(operation, input).send()
    ResponseUnpacker.unpackToOutputOrErrorMessage[ListBucketsOutput](futureResponse, operation.statusCodes)
  }
}

object QingStor {
  def apply(config: QSConfig)(implicit system: ActorSystem,
                              mat: ActorMaterializer,
                              ec: ExecutionContextExecutor): QingStor =
    new QingStor(config)

  case class ListBucketsInput(location: Option[String] = None) extends Input {
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Location")
    def getLocation: Option[String] = location
  }

  case class ListBucketsOutput(count: Int, buckets: List[BucketModel]) extends Output
}
