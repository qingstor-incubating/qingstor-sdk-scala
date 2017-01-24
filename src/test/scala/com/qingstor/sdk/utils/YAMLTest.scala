package com.qingstor.sdk.utils

import org.scalatest.FunSuite

import scala.beans.BeanProperty

class YAMLTest() extends FunSuite {
  @BeanProperty var foo: String = _
  @BeanProperty var baz: Int = _
  var bar: String = _
  test("YAML string decode test") {
    val yamlString =
      """
        |foo: 'bar'
        |baz: 443
      """.stripMargin

    val yaml = YAML.YAMLDecode(yamlString, this)
    assert(yaml.getFoo == "bar")
    assert(yaml.getBaz == 443)
  }
}
