package com.qingstor.sdk.utils

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

object YAML {

  // YAMLDecode decode the given yaml string and put into a map
  def YAMLDecode(text:String):java.util.Map[String, Any] = {
    val yaml = new Yaml(new Constructor(classOf[java.util.Map[String, Any]]))
    yaml.load(text).asInstanceOf[java.util.Map[String, Any]]
  }
}
