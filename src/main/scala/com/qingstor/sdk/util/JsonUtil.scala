package com.qingstor.sdk.util

import spray.json._
import com.qingstor.sdk.service.Types.QSJsonProtocol._

object JsonUtil {

  // encode given Json string to AST Json
  def encode(content: String): JsValue = {
    if (content != null && content.nonEmpty )
      content.parseJson
    else
      null
  }

  // encode given Map to AST Json
  def encode(content: Map[String, Any]): JsValue = {
    if (content != null && content.nonEmpty)
      content.toJson
    else
      null
  }

  // encode given List to AST Json
  def encode(content: List[Any]): JsValue = {
    if (content != null && content.nonEmpty)
      content.toJson
    else
      null
  }

  // decode given json string to type T
  def decode[T :JsonFormat](json: String): T = json.parseJson.convertTo[T]

  // decode AST json to Map
  def decode(jsonObject: JsObject): Map[String, Any] = jsonObject.convertTo[Map[String, Any]]

  // decode json array to List
  def decode(jsonArray: JsArray): List[Any] = jsonArray.convertTo[List[Any]]
}
