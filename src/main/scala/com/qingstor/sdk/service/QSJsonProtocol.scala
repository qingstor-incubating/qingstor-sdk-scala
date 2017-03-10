package com.qingstor.sdk.service

import java.time.ZonedDateTime

import com.qingstor.sdk.model.QSModels.ErrorMessage
import com.qingstor.sdk.service.Bucket._
import com.qingstor.sdk.service.Object._
import com.qingstor.sdk.service.QingStor._
import com.qingstor.sdk.service.Types._
import com.qingstor.sdk.util.JsonUtil
import spray.json._

object QSJsonProtocol extends DefaultJsonProtocol {

  object ZonedDateTimeJson extends JsonFormat[ZonedDateTime] {
    override def write(obj: ZonedDateTime): JsValue =
      JsString.apply(Symbol(obj.toString))

    override def read(json: JsValue): ZonedDateTime =
      ZonedDateTime.parse(json.asInstanceOf[JsString].value)
  }
  implicit val zonedDateTimeFormat = ZonedDateTimeJson
  object GranteeModelFormat extends RootJsonFormat[GranteeModel] {
    override def read(json: JsValue): GranteeModel = {
      val obj = json.asJsObject.fields
      val `type` =
        obj.getOrElse("type", JsString("")).asInstanceOf[JsString].value
      val id = obj.get("id").flatMap[String] {
        case str: JsString => Some(str.value)
        case _ => None
      }
      val name = obj.get("name").flatMap[String] {
        case str: JsString => Some(str.value)
        case _ => None
      }
      GranteeModel(`type` = `type`, id = id, name = name)
    }

    override def write(obj: GranteeModel): JsValue = {
      val maps = Map(
        "type" -> JsString(obj.`type`),
        "id" -> obj.id.toJson,
        "name" -> obj.name.toJson
      ).filter(t => !t._2.equals(JsNull))
      JsObject(maps)
    }
  }
  implicit val granteeModelFormat = GranteeModelFormat
  implicit val aCLModelFormat: RootJsonFormat[ACLModel] =
    jsonFormat2(ACLModel)
  implicit val bucketModelFormat: RootJsonFormat[BucketModel] =
    jsonFormat4(BucketModel)
  implicit val iPAddressModelFormat: RootJsonFormat[IPAddressModel] =
    jsonFormat1(IPAddressModel)
  implicit val isNullModelFormat: RootJsonFormat[IsNullModel] =
    jsonFormat1(IsNullModel)
  implicit val notIPAddressModelFormat: RootJsonFormat[NotIPAddressModel] =
    jsonFormat1(NotIPAddressModel)
  implicit val stringLikeModelFormat: RootJsonFormat[StringLikeModel] =
    jsonFormat1(StringLikeModel)
  implicit val stringNotLikeModelFormat: RootJsonFormat[StringNotLikeModel] =
    jsonFormat1(StringNotLikeModel)
  implicit val conditionModelFormat: RootJsonFormat[ConditionModel] =
    jsonFormat5(ConditionModel)
  implicit val cORSRuleModelFormat: RootJsonFormat[CORSRuleModel] =
    jsonFormat5(CORSRuleModel)
  implicit val keyModelFormat: RootJsonFormat[KeyModel] =
    jsonFormat6(KeyModel)
  implicit val keyDeleteErrorModelFormat: RootJsonFormat[KeyDeleteErrorModel] =
    jsonFormat3(KeyDeleteErrorModel)
  implicit val objectPartModelFormat: RootJsonFormat[ObjectPartModel] =
    jsonFormat4(ObjectPartModel)
  implicit val ownerModelFormat: RootJsonFormat[OwnerModel] =
    jsonFormat2(OwnerModel)
  implicit val statementModelFormat: RootJsonFormat[StatementModel] =
    jsonFormat6(StatementModel)

  object ErrorMessageFormat extends RootJsonFormat[ErrorMessage] {
    override def read(json: JsValue): ErrorMessage = {
      val obj = json.asJsObject.fields
      val statusCode = obj.get("status_code").flatMap {
        case num: JsNumber => Some(num.value.intValue())
        case _ => None
      }
      val requestID =
        obj.getOrElse("request_id", "").asInstanceOf[JsString].value
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
  implicit val listBucketsOutputFormat: RootJsonFormat[ListBucketsOutput] =
    jsonFormat2(ListBucketsOutput)
  implicit val deleteMultipleObjectsOutputFormat
    : RootJsonFormat[DeleteMultipleObjectsOutput] =
    jsonFormat2(DeleteMultipleObjectsOutput)
  implicit val getBucketACLOutputFormat: RootJsonFormat[GetBucketACLOutput] =
    jsonFormat2(GetBucketACLOutput)
  implicit val getBucketCORSOutputFormat: RootJsonFormat[GetBucketCORSOutput] =
    jsonFormat1(GetBucketCORSOutput)
  implicit val getBucketExternalMirrorOutputFormat
    : RootJsonFormat[GetBucketExternalMirrorOutput] =
    jsonFormat1(GetBucketExternalMirrorOutput)
  implicit val getBucketPolicyOutputFormat
    : RootJsonFormat[GetBucketPolicyOutput] =
    jsonFormat1(GetBucketPolicyOutput)
  implicit val getBucketStatisticsOutputFormat
    : RootJsonFormat[GetBucketStatisticsOutput] =
    jsonFormat7(GetBucketStatisticsOutput)
  implicit val listObjectsOutputFormat: RootJsonFormat[ListObjectsOutput] =
    jsonFormat9(ListObjectsOutput)
  implicit val listMultipartOutputFormat: RootJsonFormat[ListMultipartOutput] =
    jsonFormat2(ListMultipartOutput)

  object AnyJsonFormat extends JsonFormat[Any] {
    override def write(obj: Any): JsValue = obj match {
      case int: Int => JsNumber(int)
      case long: Long => JsNumber(long)
      case str: String => JsString(str)
      case bool: Boolean => JsBoolean(bool)
      case map: Map[_, _] if map.keySet.forall(_.isInstanceOf[String]) =>
        JsonUtil.encode(map.asInstanceOf[Map[String, Any]])
      case list: List[Any] => JsonUtil.encode(list)
      case m: ACLModel => m.toJson
      case m: BucketModel => m.toJson
      case m: ConditionModel => m.toJson
      case m: CORSRuleModel => m.toJson
      case m: GranteeModel => m.toJson
      case m: IPAddressModel => m.toJson
      case m: IsNullModel => m.toJson
      case m: KeyModel => m.toJson
      case m: KeyDeleteErrorModel => m.toJson
      case m: NotIPAddressModel => m.toJson
      case m: ObjectPartModel => m.toJson
      case m: OwnerModel => m.toJson
      case m: StatementModel => m.toJson
      case m: StringLikeModel => m.toJson
      case m: StringNotLikeModel => m.toJson
      case _ => serializationError("Can't serialize such type")
    }

    override def read(json: JsValue): Any = json match {
      case JsNumber(num) => num.longValue()
      case JsString(str) => str
      case JsBoolean(bool) => bool
      case JsNull => null
      case array: JsArray => JsonUtil.decode(array)
      case obj: JsObject => JsonUtil.decode(obj)
      case _ => deserializationError("Can't deserialize such type")
    }
  }
  implicit val anyJsonFormat = AnyJsonFormat
}
