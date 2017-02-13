package com.qingstor.sdk.request

import java.io.File

import akka.http.scaladsl.model.{HttpMethod, HttpMethods, Uri}
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.request.QSRequest.{Input, Property}
import org.scalatest.FunSuite

import scala.collection.mutable

class RequestBuilderTest extends FunSuite{
  test("Request Builder test") {
    val property: Property = Property(
      config = new QSConfig(),
      zone = "pek3a",
      apiName = "Request Builder",
      method = "GET",
      bucketName = "mybucket",
      requestUri = "/?uploads")

    val input: Input = Input(
      params = Map("foo" -> "bar", "baz" -> "123"),
      headers = Map("Cache-Control" -> "no-cache"),
      body = new File("/Users/Chris/Downloads/mini_channel.jpg")
    )

    val request = new RequestBuilder(property, input).build()

    println(request.uri.path)

    val uri = Uri("https://mybucket.pek3a.qingstor.com:443/1st/2nd?").withQuery(Uri.Query("foo&sign=bVjfhBepfluw3wFTxsNVFYq0ycmoEaUZpHfc1g9Y3r4%3D"))
    println(uri)
    println(uri.query())

  }
}
