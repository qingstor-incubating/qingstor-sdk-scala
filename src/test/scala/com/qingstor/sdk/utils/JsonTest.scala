package com.qingstor.sdk.utils

import org.scalatest.FunSuite
import spray.json._

class JsonTest extends FunSuite{
  test("Json decode to any class test") {
    val rawJSON: String =
      """
        |{
        | "foo": "bar",
        | "baz": 123,
        | "list": [1, 2, 3],
        | "obj": {"a": "b"}
        |}
      """.stripMargin
    case class Obj(a: String)
    case class RawJson(foo: String, baz: Int, list: List[Int], obj: Obj)
    object TestJsonProtocol extends DefaultJsonProtocol {
      implicit val objFormat = jsonFormat(Obj, "a")
      implicit val testFormat = jsonFormat(RawJson, "foo", "baz", "list", "obj")
    }
    import TestJsonProtocol._

    val json = Json.decode[RawJson](rawJSON)
    assert(json.foo == "bar")
    assert(json.baz == 123)
    assert(json.list.size == 3)
    assert(json.list.head == 1)
    assert(json.obj.a == "b")
  }

  test("Json decode to Map test") {
    val rawJSON: String =
      """
        |{
        | "foo": "bar",
        | "baz": 123,
        | "list": [1, 2, 3],
        | "obj": {"a": "b"}
        |}
      """.stripMargin
    val json = rawJSON.parseJson
    val map = Map("foo" -> "bar", "baz" -> 123, "list" -> List(1, 2, 3), "obj" -> Map("a" -> "b"))
    assert(Json.decode(json.asInstanceOf[JsObject]) == map)
  }

  test("Json decode to List test") {
    val raw = """["a", "b", "c"]""".stripMargin
    val json  = raw.parseJson
    val list = List("a", "b", "c")
    assert(Json.decode(json.asInstanceOf[JsArray]) == list)
  }

  test("Map encode to Json test") {
    val source = Map("foo" -> "bar", "baz" -> 123, "list" -> List(1, 2, 3), "map" -> Map("a" -> "b"))
    val json = Json.encode(source)
    val str =
      """
        |{"foo":"bar","baz":123,"list":[1,2,3],"map":{"a":"b"}}
      """.stripMargin
    assert(json.toString.trim == str.trim)
  }

  test("List encode to Json test") {
    val source = List("a", "b", "c")
    val json = Json.encode(source)
    val str =
      """
        |["a","b","c"]
      """.stripMargin
    assert(json.toString.trim == str.trim)
  }
}
