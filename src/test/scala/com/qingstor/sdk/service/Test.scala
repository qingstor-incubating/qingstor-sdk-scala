package com.qingstor.sdk.util

import java.time.ZonedDateTime

import akka.http.scaladsl.model.Uri
import com.qingstor.sdk.service.Types.CORSRulesModel
import org.scalatest.FunSuite
import com.qingstor.sdk.service.CustomJsonProtocol._
import spray.json._
import scala.language.implicitConversions

/**
  * Created by Chris on 24/02/2017.
  */
class Test extends FunSuite {
  test("Test Uri") {
    val uri = Uri("http://httpbin.org/?delete").withQuery(Uri.Query(Map("foo" -> "bar")))
    println(uri)
    println(Uri.Query(Map("foo" -> "234+1")))
    val json = JsObject(("a", JsTrue))
//    val array = json.fields.getOrElse("a", JsArray.empty).asInstanceOf[JsArray]
    println(json.compactPrint)
    println(ZonedDateTime.parse("2015-07-22T02:23:04.000Z"))

    val cors = CORSRulesModel(
      allowed_origin = "http://www.baidu.com",
      allowed_methods = List("GET")
    )
    println(Option[String](null).toJson.equals(JsNull))
    val cORSRulesModel = CORSRulesModel(
      allowed_origin = "https://www.baidu.com",
      allowed_methods = List("GET"),
      allowed_headers = Some(List("Date"))
    )
    println(cORSRulesModel.toJson)
    implicit def int2String(i: java.lang.Integer): java.lang.String = java.lang.String.valueOf(i)
    val map = Map("char" -> String.valueOf(0.toChar), "int" -> "200")
    println(Uri.Query(map))
  }
}
