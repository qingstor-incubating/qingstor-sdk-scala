package com.qingstor.sdk.service

import java.time.ZonedDateTime

import com.qingstor.sdk.model.QSModels.ErrorMessage
import com.qingstor.sdk.service.Bucket._
import com.qingstor.sdk.service.Object.{InitiateMultipartUploadOutput, ListMultipartOutput}
import com.qingstor.sdk.service.QingStor.ListBucketsOutput
import com.qingstor.sdk.service.Types._
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
  implicit val objectKeyModelFormat = jsonFormat1(ObjectKeyModel)
  implicit val objectModelFormat = jsonFormat7(ObjectModel)
  implicit val zonedDateTimeFormat = ZonedDateTimeJson
  implicit val bucketModelFormat: RootJsonFormat[BucketModel] = jsonFormat4(BucketModel)
  implicit val deleteErrorModelFormat: RootJsonFormat[DeleteErrorModel] = jsonFormat3(DeleteErrorModel)
  implicit val ownerModelFormat = jsonFormat2(OwnerModel)
  object GranteeModelFormat extends RootJsonFormat[GranteeModel] {
    override def read(json: JsValue): GranteeModel = {
      val obj = json.asJsObject.fields
      val `type` = obj.getOrElse("type", JsString("")).asInstanceOf[JsString].value
      val id = obj.get("id").flatMap[String] {
        case str: JsString => Some(str.value)
        case _ => None
      }
      val name = obj.get("name").flatMap[String] {
        case str: JsString => Some(str.value)
        case _ => None
      }
      GranteeModel(`type`, id, name)
    }

    override def write(obj: GranteeModel): JsValue = {
      val maps = Map (
        "type" -> JsString(obj.`type`),
        "id" -> obj.id.toJson,
        "name" -> obj.name.toJson
      ).filter(t => !t._2.equals(JsNull))
      JsObject(maps)
    }
  }
  implicit val granteeModelFormat = GranteeModelFormat
  implicit val aclModelFormat = jsonFormat2(ACLModel)
  object CORSRulesModelFormat extends RootJsonFormat[CORSRulesModel] {
    override def read(json: JsValue): CORSRulesModel = {
      val obj = json.asJsObject.fields
      val allowedOrigin = obj.getOrElse("allowed_origin", JsString("")).asInstanceOf[JsString].value
      val _allowedMethods = obj.get("allowed_methods")
      val allowedMethods = obj.getOrElse("allowed_methods", JsArray.empty).asInstanceOf[JsArray].convertTo[List[String]]
      val allowedHeaders = obj.get("allowed_headers").flatMap[List[String]] {
        case array: JsArray => Some(array.convertTo[List[String]])
        case _ => None
      }
      val maxAgeSeconds = obj.get("max_age_seconds").flatMap[Int] {
        case num: JsNumber => Some(num.value.intValue())
        case _ => None
      }
      val exposeHeaders = obj.get("expose_headers").flatMap[List[String]] {
        case array: JsArray => Some(array.convertTo[List[String]])
        case _ => None
      }
      CORSRulesModel(
        allowed_origin = allowedOrigin,
        allowed_methods = allowedMethods.mapConserve(_.toUpperCase),
        allowed_headers = allowedHeaders,
        max_age_seconds = maxAgeSeconds,
        expose_headers = exposeHeaders
      )
    }

    override def write(obj: CORSRulesModel): JsValue = {
      val allowedOrigin = JsString(obj.allowed_origin)
      val allowedMethods = obj.allowed_methods.toJson
      val allowedHeaders = obj.allowed_headers.toJson
      val maxAgeSeconds = obj.max_age_seconds.toJson
      val exposeHeaders = obj.expose_headers.toJson
      val maps = Map(
        "allowed_origin" -> allowedOrigin,
        "allowed_methods" -> allowedMethods,
        "allowed_headers" -> allowedHeaders,
        "max_age_seconds" -> maxAgeSeconds,
        "expose_headers" -> exposeHeaders
      ).filter(m => !m._2.equals(JsNull))
      JsObject(maps)
    }
  }
  implicit val corsRulesModelFormat = CORSRulesModelFormat
  object PartModelFormat extends RootJsonFormat[PartModel] {
    override def read(json: JsValue): PartModel = {
      val map = json.asJsObject.fields
      val partNum = map("part_number").asInstanceOf[JsNumber].value.intValue()
      val size = map.get("size").flatMap[Long] {
        case num: JsNumber => Some(num.value.longValue())
        case _ => None
      }
      val created = map.get("created").flatMap[ZonedDateTime] {
        case str: JsString => Some(str.convertTo[ZonedDateTime])
        case _ => None
      }
      val etag = map.get("etag").flatMap[String] {
        case str: JsString => Some(str.value)
        case _ => None
      }
      PartModel(
        part_number = partNum,
        size = size,
        created = created,
        etag = etag
      )
    }

    override def write(obj: PartModel): JsValue = {
      val map = Map(
        "part_number" -> JsNumber(obj.part_number),
        "size" -> obj.size.toJson,
        "created" -> obj.created.toJson,
        "etag" -> obj.etag.toJson
      ).filter(entry => entry._2 != JsNull)
      JsObject(map)
    }
  }
  implicit val partModelFormat = PartModelFormat

  object ErrorMessageFormat extends RootJsonFormat[ErrorMessage] {
    override def read(json: JsValue): ErrorMessage = {
      val obj = json.asJsObject.fields
      val statusCode = obj.get("status_code").flatMap {
        case num: JsNumber => Some(num.value.intValue())
        case _ => None
      }
      val requestID = obj.getOrElse("request_id", "").asInstanceOf[JsString].value
      val code = obj.get("code").flatMap {
        case str: JsString => Some(str.value)
        case _ => None
      }
      val message = obj.get("message").flatMap {
        case str: JsString => Some(str.value)
        case _ => None
      }
      val url = obj.get("url").flatMap {
        case str: JsString => Some(str.value)
        case _ => None
      }
      ErrorMessage(
        requestID = requestID,
        statusCode = statusCode,
        code = code,
        message = message,
        url = url
      )
    }

    override def write(obj: ErrorMessage): JsValue = {
      val map = Map(
        "status_code" -> obj.statusCode.toJson,
        "request_id" -> obj.requestID.toJson,
        "code" -> obj.code.toJson,
        "message" -> obj.message.toJson,
        "url" -> obj.url.toJson
      ).filter(t => !t._2.equals(JsNull))
      JsObject(map)
    }
  }
  implicit val errorMessageFormat = ErrorMessageFormat
  implicit val listBucketsOutputFormat = jsonFormat2(ListBucketsOutput)
  implicit val getBucketStatisticsOutputFormat = jsonFormat5(GetBucketStatisticsOutput)
  implicit val getBucketACLOuputFormat = jsonFormat2(GetBucketACLOuput)
  implicit val getBucketCORSOutputFormat = jsonFormat1(GetBucketCORSOutput)
  implicit val listObjectsOutputFormat = jsonFormat9(ListObjectsOutput)
  implicit val initiateMultipartUploadOutputFormat = jsonFormat3(InitiateMultipartUploadOutput)
  implicit val listMultipartOutputFormat = jsonFormat2(ListMultipartOutput)
  object DeleteMultipleObjectsOutputFormat extends RootJsonFormat[DeleteMultipleObjectsOutput] {
    override def write(obj: DeleteMultipleObjectsOutput): JsValue = {
      val deleted = obj.deleted
      val errors = obj.errors
      JsObject(("deleted", deleted.toJson), ("errors", errors.toJson))
    }

    override def read(json: JsValue): DeleteMultipleObjectsOutput = {
      val obj = json.asJsObject.fields
      val deleted = obj.getOrElse("deleted", JsArray.empty).asInstanceOf[JsArray]
      val listDeleted = deleted.convertTo[List[ObjectKeyModel]]
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
      case b: Boolean => JsBoolean(b)
      case m: Map[String, Any] => JsonUtil.encode(m)
      case l: List[Any] => JsonUtil.encode(l)
      case m: ObjectKeyModel => m.toJson
      case m: BucketModel => m.toJson
      case m: DeleteErrorModel => m.toJson
      case m: OwnerModel => m.toJson
      case m: GranteeModel => m.toJson
      case m: ACLModel => m.toJson
      case m: CORSRulesModel => m.toJson
      case m: PartModel => m.toJson
      case _ => serializationError("Can't serialize such type")
    }

    override def read(value: JsValue): Any = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case JsNull => null
      case l: JsArray => JsonUtil.decode(l)
      case o: JsObject => JsonUtil.decode(o)
      case _ => deserializationError("Can't deserialize such type")
    }
  }
  implicit val anyJsonFormat = AnyJsonFormat
}
