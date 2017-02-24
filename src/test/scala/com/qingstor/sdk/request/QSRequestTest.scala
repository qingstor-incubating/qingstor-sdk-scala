package com.qingstor.sdk.request

import java.lang.reflect.Field

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling._
import akka.stream.ActorMaterializer
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.exception.QingStorException
import com.qingstor.sdk.model.QSModels._
import org.scalatest.FunSuite
import com.qingstor.sdk.service.CustomJsonProtocol._
import spray.json.{JsNull, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class QSRequestTest extends FunSuite{
  case class TestOutput() extends Output

  class TestResponse extends QSHttpResponse {
    private var date: String = _
//    private var statusCode: Int = _
//    private var requestID: String = _
//    private var elements: JsValue = JsNull
//
//    def setStatusCode(statusCode: Int): Unit = this.statusCode = statusCode
//    def setRequestID(requestID: String): Unit = this.requestID = requestID
//    def setElements(value: JsValue): Unit = this.elements = value
//
//    @ParamAnnotation(location = "StatusCode")
//    def getStatusCode: Int = this.statusCode
//
//    @ParamAnnotation(location = "requestID")
//    def getRequestID: String = this.requestID
//
//    @ParamAnnotation(location = "elements")
//    def getElements: JsValue = this.elements

    def setDate(date: String): Unit = this.date = date

    @ParamAnnotation(location = "headers", name = "Date")
    def getDate: String = this.date
  }

  test("QSRequest test") {
    val config: QSConfig = new QSConfig("ACCESS_KEY_ID", "SECRET_ACCESS_KEY")
//    val property = Operation(config, "", "Some Action", "GET", "", "/")
//    val input = Input(null, null)
//    val request = new QSRequest(property, input)
//    val result = request.send().unpack[ListBucketOutput]()
//    assert(result.isLeft)
//    assert(result.left.get.isInstanceOf[QingStorException])
//    assert(request.HTTPRequest.entity == HttpEntity.Empty)
//    assert(request.HTTPRequest.uri.toString() == "https://qingstor.com/")
    val response: QSHttpResponse = new TestResponse()
    val methods = response.getClass.getMethods
    val fields = response.getClass.getDeclaredFields
    for (method <- methods) {
      println(method.getName)
    }
    println("###########")
    for (field <- fields) {
      println(field.getName)
    }
  }
}
