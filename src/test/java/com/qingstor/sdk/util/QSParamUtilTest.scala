package com.qingstor.sdk.util

import java.time.ZonedDateTime

import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.util.QSParamUtilTest.Test
import org.scalatest.FunSuite

class QSParamUtilTest extends FunSuite {
  test("getRequestParams Test") {
    val now = ZonedDateTime.now()
    val test = Test("foo", 123, true, false, Some(456), now)

    val map1 = ConversionUtil.jMapAsScalaMap(QSParamUtil.getRequestParams(test, "elements"))
      .asInstanceOf[Map[String, Any]]
    assert(map1 == Map[String, Any]("Foo" -> "foo", "Baz" -> true))

    val map2 = ConversionUtil.jMapAsScalaMap(QSParamUtil.getRequestParams(test, "params"))
    assert(map2 == Map[String, String]("Bar" -> "123", "T1" -> "false"))

    val map3 = ConversionUtil.jMapAsScalaMap(QSParamUtil.getRequestParams(test, "headers"))
    assert(map3 == Map[String, String]("T2" -> "456", "T3" -> now.toString))
  }

  test("getResponseParams Test") {
    val now = ZonedDateTime.now()
    val test = Test("foo", 123, true, false, Some(456), now)

    val map = ConversionUtil.jMapAsScalaMap(QSParamUtil.getResponseParams(test, "params"))
    assert(map == Map[String, String]("Bar" -> "getBar", "T1" -> "getT1"))
  }

  test("invokeMethod Test") {
    val now = ZonedDateTime.now()
    val test = Test("foo", 123, true, false, Some(456), now)

    val foo = QSParamUtil.invokeMethod(test, "getFoo", null)
    assert(foo == "foo")

    QSParamUtil.invokeMethod(test, "setFoo", Array("foo bar"))
    assert(test.foo == "foo bar")
  }
}

object QSParamUtilTest {
  case class Test(var foo: String, bar: Int, baz: Boolean, t1: Boolean, t2: Some[Int], t3: ZonedDateTime) {
    def setFoo(f: String): Unit = foo = f

    @ParamAnnotation(location = "elements", name = "Foo")
    def getFoo: String = foo

    @ParamAnnotation(location = "params", name = "Bar")
    def getBar: Int = bar

    @ParamAnnotation(location = "elements", name = "Baz")
    def getBaz: Boolean = baz

    @ParamAnnotation(location = "params", name = "T1")
    def getT1: Boolean = t1

    @ParamAnnotation(location = "headers", name = "T2")
    def getT2: Some[Int] = t2

    @ParamAnnotation(location = "headers", name = "T3")
    def getT3: ZonedDateTime = t3
  }
}
