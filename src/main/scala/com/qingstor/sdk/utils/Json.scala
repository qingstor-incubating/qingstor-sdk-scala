package com.qingstor.sdk.utils

import spray.json._
import DefaultJsonProtocol._

object Json {

  implicit object AnyJsonFormat extends JsonFormat[Any] {
    def write(x: Any) = x match {
      case n: Int => JsNumber(n)
      case s: String => JsString(s)
      case b: Boolean if b => JsTrue
      case b: Boolean if !b => JsFalse
      case m: Map[String, Any] => encode(m)
      case l: List[Any] => encode(l)
      case _ => JsNull
    }

    def read(value: JsValue) = value match {
      case JsNumber(n) => n.intValue()
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case JsNull => null
      case l: JsArray => decode(l)
      case o: JsObject => decode(o)
    }
  }

  // encode given Json string to AST Json
  def encode(content: String): JsValue = content.parseJson

  // encode given map to AST Json
  def encode(content: Map[String, Any]): JsValue = content.toJson

  def encode(content: List[Any]): JsValue = content.toJson

  def decode[T :JsonFormat](json: String): T = json.parseJson.convertTo[T]

  def decode(jsonObject: JsObject): Map[String, Any] = jsonObject.convertTo[Map[String, Any]]

  def decode(jsonArray: JsArray): List[Any] = jsonArray.convertTo[List[Any]]

  def formatJson(json: String): String = json.parseJson.prettyPrint
}
