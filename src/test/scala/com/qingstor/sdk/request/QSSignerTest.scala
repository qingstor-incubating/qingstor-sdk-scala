package com.qingstor.sdk.request

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import org.scalatest.FunSuite

class QSSignerTest extends FunSuite {

  test("Test getHeadAuthorization") {
    val url =
      "https://qingstor.com/?acl&upload_id=fde133b5f6d932cd9c79bac3c7318da1&part_number=0&other=abc"
    val headers = List(RawHeader("X-QS-Test-2", "Test 2"),
                       RawHeader("X-QS-Test-1", "Test 1"),
                       RawHeader("Date", "Mon, 01 Jan 0001 00:00:00 GMT"))
    val request = HttpRequest()
      .withUri(url)
      .withMethod(HttpMethods.GET)
      .withHeaders(headers)
    val signature = "QS ENV_ACCESS_KEY_ID:bvglZF9iMOv1RaCTxPYWxexmt1UN2m5WKngYnhDEp2c="
    val authorization = QSSigner.getHeadAuthorization(request, "ENV_ACCESS_KEY_ID", "ENV_SECRET_ACCESS_KEY")
    assert(authorization == signature)
  }

  test("Test getHeadAuthorization chinese") {
    val url = "https://zone.qingstor.com/bucket-name/中文"
    val headers = List(RawHeader("Date", "Mon, 01 Jan 0001 00:00:00 GMT"))
    val request = HttpRequest()
      .withUri(new java.net.URI(url).toASCIIString)
      .withMethod(HttpMethods.GET)
      .withHeaders(headers)
    val authorization = QSSigner.getHeadAuthorization(request, "ENV_ACCESS_KEY_ID", "ENV_SECRET_ACCESS_KEY")
    val signature = "QS ENV_ACCESS_KEY_ID:XsTXX50kzqBf92zLG1aIUIJmZ0hqIHoaHgkumwnV3fs="
    assert(authorization == signature)
  }

  test("Test getQueryAuthorization") {
    val url =
      "https://qingstor.com/?acl&upload_id=fde133b5f6d932cd9c79bac3c7318da1&part_number=0&other=abc"
    val headers = List(RawHeader("X-QS-Test-2", "Test 2"),
      RawHeader("X-QS-Test-1", "Test 1"),
      RawHeader("Date", "Mon, 01 Jan 0001 00:00:00 GMT"))
    val request = HttpRequest()
      .withUri(url)
      .withMethod(HttpMethods.GET)
      .withHeaders(headers)
    val auth = QSSigner.getQueryAuthorization(request, "ENV_ACCESS_KEY_ID", "ENV_SECRET_ACCESS_KEY", -62135596800L)
    val signature = "access_key_id=ENV_ACCESS_KEY_ID&expires=-62135596800&signature=gTdB%2FcmD6rjv8CbFRDfFbHc64q442rYNAp99Hm7fBl4%3D"
    assert(auth == signature)
  }
}
