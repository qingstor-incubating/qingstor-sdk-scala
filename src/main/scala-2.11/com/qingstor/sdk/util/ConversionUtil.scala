package com.qingstor.sdk.util

import scala.collection.JavaConversions._
import scala.collection.mutable

object ConversionUtil {
  def jMapAsScalaMap[K, V](map: java.util.Map[K, V]): mutable.Map[K, V] = mapAsScalaMap(map)
}
