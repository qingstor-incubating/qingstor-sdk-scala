package com.qingstor.sdk.request

import java.io.{BufferedInputStream, File, FileInputStream}

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels.{Operation, QSHttpResponse}
import com.qingstor.sdk.request.ResponseUnpackerTest.{TestResponse, TestResponseWithBytes}
import org.scalatest.FunSuite

class ResponseUnpackerTest extends FunSuite{
  val config = QSConfig()
  val operation = Operation(
    config = config,
    apiName = "Test API",
    method = "GET",
    requestUri = "/",
    statusCodes = Array(200)
  )
  val headers = List(
    RawHeader("Date", "Sun, 16 Aug 2015 09:05:00 GMT"),
    RawHeader("X-QS-Request-ID", "aa08cf7a43f611e5886952542e6ce14b"),
    RawHeader("Content-Type", "image/jpeg")
  )

  test("Test Response Unpacker") {
    val entity = HttpEntity(ContentTypes.`application/json`, """{"foo": "bar"}""")
    val response: HttpResponse = HttpResponse(
      headers = headers,
      entity = entity
    )
    val unpacker = ResponseUnpacker(response, operation)
    val testResp = unpacker.unpackResponse[TestResponse]()
    assert(testResp.getDate.equals("Sun, 16 Aug 2015 09:05:00 GMT"))
    assert(testResp.getStatusCode == 200)
    assert(testResp.getRequestID.equals("aa08cf7a43f611e5886952542e6ce14b"))
    assert(testResp.getEntity.equals(entity))
  }

  test("Test Response Unpacker With Bytes") {
    val file = new File("/Users/Chris/test.jpg")
    val bis = new BufferedInputStream(new FileInputStream(file))
    val bytes = new Array[Byte](file.length().toInt)
    bis.read(bytes)
    bis.close()

    val entity = HttpEntity(ContentTypes.`application/octet-stream`, bytes)
    val response: HttpResponse = HttpResponse(
      headers = headers,
      entity = entity
    )

    val testResponse = ResponseUnpacker(response, operation).unpackResponse[TestResponseWithBytes]()
    assert(testResponse.getContentType.equals("image/jpeg"))
    assert(testResponse.getEntity.contentLengthOption.get == file.length())
  }
}

object ResponseUnpackerTest {
  class TestResponse extends QSHttpResponse {
    private var date: String = _

    def setDate(date: String): Unit = this.date = date

    @ParamAnnotation(location = "headers", name = "Date")
    def getDate: String = this.date
  }

  class TestResponseWithBytes extends QSHttpResponse {
    private var contentType: String = _

    def setContentType(t: String): Unit = this.contentType = t

    @ParamAnnotation(location = "headers", name = "Content-Type")
    def getContentType: String = this.contentType
  }
}