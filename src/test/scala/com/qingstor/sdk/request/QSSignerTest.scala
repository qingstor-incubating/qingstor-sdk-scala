package com.qingstor.sdk.request

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import org.scalatest.{FlatSpec, FunSuite}

class QSSignerTest extends FlatSpec {

  it should """get authorization "su3v/vHFfEwkamaUDpdJhbvUEsBLoNlCU2/x2QA1TUY=" """ in {
    val url =
      "https://bucket-name.zone.qingstor.com/?acl&upload_id=fde133b5f6d932cd9c79bac3c7318da1&part_number=0&other=abc"
    val headers = List(RawHeader("X-QS-Test-2", "Test 2"),
                       RawHeader("X-QS-Test-1", "Test 1"),
                       RawHeader("Date", "Mon, 01 Jan 0001 00:00:00 GMT"))
    val request = HttpRequest()
      .withUri(url)
      .withMethod(HttpMethods.GET)
      .withHeaders(headers)
    val authorization = QSSigner.getHeadAuthorization(request, "ENV_ACCESS_KEY_ID")
    assert(authorization == "QS ENV_ACCESS_KEY_ID:su3v/vHFfEwkamaUDpdJhbvUEsBLoNlCU2/x2QA1TUY=")
  }

  it should """get authorization "uJt2ADWkt+mpCMBGkTNYaZycV6iWuE19X/U0E0F5akM=" while there is chinese """ in {
    val url = Uri()
      .withScheme("https")
      .withHost("bucket-name.zone.qingstor.com")
      .withPath(Uri.Path("/中文"))
    val headers = List(RawHeader("Date", "Mon, 01 Jan 0001 00:00:00 GMT"))
    val request = HttpRequest()
      .withUri(url)
      .withMethod(HttpMethods.GET)
      .withHeaders(headers)
    val authorization = QSSigner.getHeadAuthorization(request, "ENV_ACCESS_KEY_ID")
    assert(authorization == "QS ENV_ACCESS_KEY_ID:uJt2ADWkt+mpCMBGkTNYaZycV6iWuE19X/U0E0F5akM=")
  }

  it should """return a map of access_key_id, expires, signature""" in {
    val url =
      "https://bucket-name.zone.qingstor.com/?acl&upload_id=fde133b5f6d932cd9c79bac3c7318da1&part_number=0&other=abc"
    val headers = List(RawHeader("X-QS-Test-2", "Test 2"),
      RawHeader("X-QS-Test-1", "Test 1"),
      RawHeader("Date", "Mon, 01 Jan 0001 00:00:00 GMT"))
    val request = HttpRequest()
      .withUri(url)
      .withMethod(HttpMethods.GET)
      .withHeaders(headers)
    val auth = QSSigner.getQueryAuthorization(request, "ENV_ACCESS_KEY_ID", 1479107162)
    assert(auth == "access_key_id=ENV_ACCESS_KEY_IDexpires=1479107162signature=bVjfhBepfluw3wFTxsNVFYq0ycmoEaUZpHfc1g9Y3r4%3D")
  }
}
