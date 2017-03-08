package com.qingstor.sdk.util

import java.io.File

import akka.http.scaladsl.model.ContentTypes
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.util.QSRequestUtilTest.{TestGetRequestParam, TestGetResponseParams}
import org.scalatest.FunSuite

class QSRequestUtilTest extends FunSuite{
  test("Test getRequestParams") {
    val maps = QSRequestUtil.getRequestParams(TestGetRequestParam("abc", true), "headers")
    val shouldResult = Map[String, AnyRef]("Foo" -> "abc", "Bar" -> "true")
    assert(maps == shouldResult)
    assertThrows[NoSuchElementException](maps("Baz"))
  }

  test("Test getResponseParams") {
    val headers = QSRequestUtil.getResponseParams(new TestGetResponseParams(), "headers")
    val elements = QSRequestUtil.getResponseParams(new TestGetResponseParams(), "elements")

    assert(headers == Map("Foo" -> "getFoo"))
    assert(elements == Map("Bar" -> "getBar"))
  }

  test("Test invokeMethod") {
    val t = new TestGetResponseParams()
    QSRequestUtil.invokeMethod(t, "setBar", Array("abc"))
    assert(t.getBar == "abc")
  }

  test("Test parseContentType") {
    val jpgFile = new File("/tmp/test.jpg")
    assert(QSRequestUtil.parseContentType(jpgFile).toString() == "image/jpeg")

    val octFile = new File("/tmp/test")
    assert(QSRequestUtil.parseContentType(octFile) == ContentTypes.`application/octet-stream`)
  }
}

object QSRequestUtilTest {
  case class TestGetRequestParam(foo: String, bar: Boolean = false, baz: Option[Int] = None) {
    @ParamAnnotation(location = "headers", name = "Foo")
    def getFoo: String = this.foo

    @ParamAnnotation(location = "headers", name = "Bar")
    def getBar: Boolean = this.bar

    @ParamAnnotation(location = "headers", name = "Baz")
    def getBaz: Option[Int] = this.baz
  }

  class TestGetResponseParams {
    private var foo: Int = _
    private var bar: String = _

    def setFoo(n: Int): Unit = this.foo = n
    def setBar(str: String): Unit = this.bar = str

    @ParamAnnotation(location = "headers", name = "Foo")
    def getFoo: Int = this.foo

    @ParamAnnotation(location = "elements", name = "Bar")
    def getBar: String = this.bar
  }
}
