package com.qingstor.sdk.service

import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.QSRequest
import com.qingstor.sdk.service.Types.BucketModel
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import spray.json.{JsString, JsValue, JsonFormat}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future

class QingStor(private val _config: QSConfig)(implicit val system: ActorSystem, val mat: ActorMaterializer) {
  val config: QSConfig = _config
//
//  object ZonedDateTimeJson extends JsonFormat[ZonedDateTime] {
//    override def write(obj: ZonedDateTime): JsValue = obj match {
//      case time: ZonedDateTime => JsString(time.toString)
//    }
//
//    override def read(json: JsValue): ZonedDateTime = json match {
//      case JsString(str) => ZonedDateTime.parse(str)
//    }
//  }
//
//  private implicit val zonedDateTimeFormat = ZonedDateTimeJson
//  private implicit val bucketModelFormat = jsonFormat4(BucketModel)

  def listBuckets(input: Input): Future[QSHttpResponse] = {
    val operation = Operation(
      config = config,
      apiName = "GET Service",
      method = "GET",
      requestUri = "/",
      statusCodes = Array[Int](200)
    )
    QSRequest(operation, input).send[QSHttpResponse]()

  }
}

object QingStor {
  def apply(config: QSConfig)(implicit system: ActorSystem, mat: ActorMaterializer): QingStor = new QingStor(config)

  case class ListBucketsInput(location: String = null) extends Input {
    @ParamAnnotation(location = QSConstants.ParamsLocationHeader, name = "Location")
    def getLocation: String = location
  }

  case class ListBucketsOutput(count: Int, buckets: List[BucketModel])
}
