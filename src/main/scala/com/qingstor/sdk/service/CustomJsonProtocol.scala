package com.qingstor.sdk.service

import java.time.ZonedDateTime

import com.qingstor.sdk.model.QSModels.ErrorMessage
import com.qingstor.sdk.service.Bucket.DeleteMultipleObjectsOutput
import com.qingstor.sdk.service.QingStor.ListBucketsOutput
import com.qingstor.sdk.service.Types.{BucketModel, DeleteErrorModel, ObjectModel}
import com.qingstor.sdk.util.JsonUtil
import spray.json._

object CustomJsonProtocol extends DefaultJsonProtocol {

  object ZonedDateTimeJson extends JsonFormat[ZonedDateTime] {
    override def write(obj: ZonedDateTime): JsValue = obj match {
      case time: ZonedDateTime => JsString(time.toString)
    }

    override def read(json: JsValue): ZonedDateTime =
      ZonedDateTime.parse(json.asInstanceOf[JsString].value)
  }

  implicit val objectModelFormat = jsonFormat1(ObjectModel)
  implicit val zonedDateTimeFormat = ZonedDateTimeJson
  implicit val bucketModelFormat: RootJsonFormat[BucketModel] = jsonFormat4(BucketModel)
  implicit val errorMessageFormat: RootJsonFormat[ErrorMessage] = jsonFormat4(ErrorMessage)
  implicit val deleteErrorModelFormat: RootJsonFormat[DeleteErrorModel] = jsonFormat3(DeleteErrorModel)
  implicit val listBucketsOutputFormat = jsonFormat2(ListBucketsOutput)

  object DeleteMultipleObjectsOutputFormat extends RootJsonFormat[DeleteMultipleObjectsOutput] {
    override def write(obj: DeleteMultipleObjectsOutput): JsValue = {
      val deleted = obj.deleted
      val errors = obj.errors
      JsObject(("deleted", deleted.toJson), ("errors", errors.toJson))
    }

    override def read(json: JsValue): DeleteMultipleObjectsOutput = {
      val obj = json.asJsObject.fields
      val deleted = obj.getOrElse("deleted", JsArray.empty).asInstanceOf[JsArray]
      val listDeleted = deleted.convertTo[List[ObjectModel]]
      val errors = obj.getOrElse("errors", JsArray.empty).asInstanceOf[JsArray]
      val listErrors = errors.convertTo[List[DeleteErrorModel]]
      DeleteMultipleObjectsOutput(listDeleted, listErrors)
    }
  }

  implicit val deleteMultipleOutputFormat = DeleteMultipleObjectsOutputFormat

  object AnyJsonFormat extends JsonFormat[Any] {
    override def write(x: Any): JsValue = x match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b => JsTrue
      case b: Boolean if !b => JsFalse
      case m: Map[String, Any] => JsonUtil.encode(m)
      case l: List[Any] => JsonUtil.encode(l)
      case b: BucketModel => b.toJson
      case d: DeleteErrorModel => d.toJson
      case o: ObjectModel => o.toJson
      case _ => JsNull
    }

    override def read(value: JsValue): Any = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case JsNull => null
      case l: JsArray => JsonUtil.decode(l)
      case o: JsObject => JsonUtil.decode(o)
    }
  }

  implicit val anyJsonFormat = AnyJsonFormat
}
