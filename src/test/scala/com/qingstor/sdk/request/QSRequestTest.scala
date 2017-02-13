package com.qingstor.sdk.request

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.headers.RawHeader
import com.qingstor.sdk.config.QSConfig
import org.scalatest.FunSuite

class QSRequestTest extends FunSuite{
  test("QSRequest test") {
    val system = ActorSystem("QSRequest")
    val config: QSConfig = new QSConfig("CJAFYFUDKBYQROKTYHDT", "tvOlKg0Ba9mUt5p61VCioffM61o6ypx79ofFRXNH")
    val property = QSRequest.Property(config, "pek3a", "GET Bucket", "GET", "mybuket", "/")
    val input = QSRequest.Input(null, null)
    val accessKeyID = property.config.getAccessKeyID
    var request = new RequestBuilder(property, input).build()
    val authString = QSSigner.getHeadAuthorization(request, accessKeyID)
    request = request.addHeader(RawHeader("Authorization", authString))
    println("request:\n" + request)

    val qsRequest = system.actorOf(QSRequest.props(property, input), "qsRequest")
    qsRequest!request
  }
}
