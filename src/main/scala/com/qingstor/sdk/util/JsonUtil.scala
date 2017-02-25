package com.qingstor.sdk.util

import spray.json._
import com.qingstor.sdk.service.CustomJsonProtocol._

object JsonUtil {

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
