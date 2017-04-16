package com.qingstor.sdk.util

import org.scalatest.FunSuite

class ConversionUtilTest extends FunSuite{
  test("jMapAsScalaMap Test") {
    val jMap = new java.util.HashMap[String, String]()
    jMap.put("foo", "bar")
    val converted = ConversionUtil.jMapAsScalaMap(jMap)
    assert(converted.isInstanceOf[scala.collection.mutable.Map[String, String]])
  }
}
