package com.qingstor.sdk.utils

import java.util

import com.google.gson.internal.LinkedTreeMap
import org.scalatest.FunSuite

class JSONTest extends FunSuite{
  test("JSON object decode test") {
    val rawJSON =
      """
        |{
        | "foo": "bar",
        | "baz": 123,
        | "list": [1, 2, 3],
        | "obj": {"a": "b"}
        |}
      """.stripMargin
    val json = JSON.DecodeJSONObject(rawJSON)
    assert(json.get("foo") == "bar")
    assert(json.get("baz") == 123)
    val list = json.get("list").asInstanceOf[util.ArrayList[Double]]
    assert(list.size() == 3)
    assert(list.get(0) == 1.0)
    assert(json.get("obj").asInstanceOf[LinkedTreeMap[String, String]].get("a") == "b")
  }

  test("JSON array decode test") {
    var rawJSON =
      """
        |[
        | {
        |   "foo": "bar"
        | },
        | {
        |   "baz": 123,
        |   "list": [1, 2, 3]
        | }
        |]
      """.stripMargin
    val json = JSON.DecodeJSONArray(rawJSON)
    assert(json.size() == 2)
    val foobar = json.get(0).asInstanceOf[LinkedTreeMap[String, String]]
    assert(foobar.get("foo") == "bar")
    val baz = json.get(1).asInstanceOf[LinkedTreeMap[String, Any]]
    assert(baz.get("baz") == 123)
    val list = baz.get("list").asInstanceOf[util.ArrayList[Double]]
    assert(list.size() == 3)
    assert(list.get(0) == 1.0)
  }
}
