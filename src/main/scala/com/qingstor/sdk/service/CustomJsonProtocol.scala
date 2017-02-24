package com.qingstor.sdk.service

import java.time.ZonedDateTime

import com.qingstor.sdk.model.QSModels.ErrorMessage
import com.qingstor.sdk.service.Bucket.DeleteMultipleObjectsOutput
import com.qingstor.sdk.service.Types.{BucketModel, DeleteErrorModel}
import spray.json._

object CustomJsonProtocol extends DefaultJsonProtocol{
  object ZonedDateTimeJson extends JsonFormat[ZonedDateTime] {
    override def write(obj: ZonedDateTime): JsValue = obj match {
      case time: ZonedDateTime => JsString(time.toString)
    }

    override def read(json: JsValue): ZonedDateTime = json match {
      case JsString(str) => ZonedDateTime.parse(str)
    }
  }

  implicit val zonedDateTimeFormat = ZonedDateTimeJson
  implicit val bucketModelFormat: RootJsonFormat[BucketModel] = jsonFormat4(BucketModel)
  implicit val errorMessageFormat: RootJsonFormat[ErrorMessage] = jsonFormat4(ErrorMessage)
  implicit val deleteErrorModelFormat: RootJsonFormat[DeleteErrorModel] = jsonFormat3(DeleteErrorModel)
  implicit val deleteMultipleOutputFormat: RootJsonFormat[DeleteMultipleObjectsOutput] = jsonFormat2(DeleteMultipleObjectsOutput)
}
