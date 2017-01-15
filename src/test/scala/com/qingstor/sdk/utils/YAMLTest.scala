package com.qingstor.sdk.utils

import org.scalatest.FunSuite

class YAMLTest extends FunSuite{
  test("YAML string decode test") {
    val yamlString =
      """
        |foo: 'bar'
        |baz: 443
      """.stripMargin

    val yaml = YAML.YAMLDecode(yamlString)
    assert(yaml.get("foo") == "bar")
    assert(yaml.get("baz") == 443)
  }
}
