package com.qingstor.sdk.util

import scala.collection.JavaConversions._

object ConversionUtil {
  def jMapAsScalaMap[K, V](map: java.util.Map[K, V]): Map[K, V] = mapAsScalaMap(map).toMap
}
