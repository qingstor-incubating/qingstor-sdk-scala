package com.qingstor.sdk.request

import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.request.Models._
import org.scalatest.FunSuite

class QSRequestTest extends FunSuite{

  test("QSRequest test") {
    def onSuccess(resp: HttpResponse){}

    def onError(msg: ErrorMessage): Unit = {
      assert(msg.code == "invalid_access_key_id")
      assert(msg.message == "The access key id you provided does not exist.")
    }

    val config: QSConfig = new QSConfig("ACCESS_KEY_ID", "SECRET_ACCESS_KEY")
    val property = Property(config, "", "Some Action", "GET", "", "/")
    val input = Input(null, null)
    val r = new QSRequest(property, input)
    r.send(onSuccess, onError)
    assert(r.HTTPRequest.entity == HttpEntity.Empty)
    assert(r.HTTPRequest.uri.toString() == "https://qingstor.com/")
  }
}
