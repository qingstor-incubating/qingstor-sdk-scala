package com.qingstor.sdk.util

import scala.collection.JavaConverters._
import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.{Charset, IllegalCharsetNameException}

object QSRequestUtil {
  def getRequestParams(any: Any, location: String): Map[String, AnyRef] = {
    val jMap = QSParamUtil.getRequestParams(any, location)
    mapAsScalaMap(jMap).toMap
  }

  @throws[IllegalCharsetNameException]
  def inputStreamFromString(string: String, charset: String = "UTF-8"): InputStream = {
    if (!Charset.isSupported(charset))
      throw new IllegalCharsetNameException("""Charset "%s" is not support""".format(charset))
    new ByteArrayInputStream(string.getBytes(charset))
  }

  def bytesToString(bytes: Array[Byte], charset: String = "UTF-8"): String = {
    if (!Charset.isSupported(charset))
      throw new IllegalCharsetNameException("""Charset "%s" is not support""".format(charset))
    new String(bytes, charset)
  }

  def getResponseParams(any: Any, location: String): Map[String, String] = {
    val jMap = QSParamUtil.getResponseParams(any, location)
    mapAsScalaMap[String, String](jMap).toMap
  }

  def invokeMethod(any: Any, methodName: String, params: Array[AnyRef]): Any = {
    QSParamUtil.invokeMethod(any, methodName, params)
  }
}
