package com.qingstor.sdk.util

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.reflect.ClassTag

object YamlUtil {

  // YAMLDecode decode the given yaml string and put into a map
  def YAMLDecode[T: ClassTag](text:String, struct: T): T = {
    val yaml = new Yaml(new Constructor(struct.getClass))
    yaml.load(text).asInstanceOf[T]
  }
}
