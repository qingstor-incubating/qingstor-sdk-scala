package com.qingstor.sdk.util

import spray.json._
import spray.json.DefaultJsonProtocol._

object JsonUtil {

  object AnyJsonFormat extends JsonFormat[Any] {
    override def write(x: Any): JsValue = x match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b => JsTrue
      case b: Boolean if !b => JsFalse
      case m: Map[String, Any] => JsonUtil.encode(m)
      case l: List[Any] => JsonUtil.encode(l)
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

  // encode given Json string to AST Json
  def encode(content: String): JsValue = {
    if (content != null && content.nonEmpty )
      content.parseJson
    else
      null
  }

  // encode given map to AST Json
  def encode(content: Map[String, Any]): JsValue = {
    if (content != null && content.nonEmpty)
      content.toJson
    else
      null
  }

  def encode(content: List[Any]): JsValue = {
    if (content != null && content.nonEmpty)
      content.toJson
    else
      null
  }

  def decode[T :JsonFormat](json: String): T = json.parseJson.convertTo[T]

  def decode(jsonObject: JsObject): Map[String, Any] = jsonObject.convertTo[Map[String, Any]]

  def decode(jsonArray: JsArray): List[Any] = jsonArray.convertTo[List[Any]]

  def formatJson(json: String): String = json.parseJson.prettyPrint
}
