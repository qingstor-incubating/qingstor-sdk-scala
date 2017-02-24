package com.qingstor.sdk.util

import org.scalatest.FunSuite

import scala.beans.BeanProperty

class YamlUtilTest extends FunSuite {
  @BeanProperty var foo: String = _
  @BeanProperty var baz: Int = _
  var bar: String = _
  test("YAML string decode test") {
    val yamlString =
      """
        |foo: 'bar'
        |baz: 443
      """.stripMargin

    val yaml = YamlUtil.YAMLDecode(yamlString, this)
    assert(yaml.getFoo == "bar")
    assert(yaml.getBaz == 443)
  }

  test("Yaml test miss an entry") {
    val yamlString =
      """
        |#foo: 'bar'
        |baz: 443
      """.stripMargin
    val yaml = YamlUtil.YAMLDecode(yamlString, this)
    assert(yaml.getBaz == 443)
  }
}
