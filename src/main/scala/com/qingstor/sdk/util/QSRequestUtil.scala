package com.qingstor.sdk.util

import java.io.File

import akka.http.scaladsl.model._

object QSRequestUtil {
  // get all the non-null and non-None params of the specified location from Input
  def getRequestParams(any: Any, location: String): Map[String, AnyRef] = {
    val jMap = QSParamUtil.getRequestParams(any, location)
    ConversionUtil.jMapAsScalaMap(jMap).toSeq.sortBy(_._1).toMap
  }

  // get all the needed params of the specified location from Output
  // return map contains the name of this param and its get method name
  def getResponseParams(any: Any, location: String): Map[String, String] = {
    val jMap = QSParamUtil.getResponseParams(any, location)
    ConversionUtil.jMapAsScalaMap[String, String](jMap).toMap
  }

  // invoke a method specified by @methodName of class any
  def invokeMethod(any: Any, methodName: String, params: Array[AnyRef]): Any = {
    QSParamUtil.invokeMethod(any, methodName, params)
  }

  // parse Content-Type of a file according to its extension
  def parseContentType(file: File): ContentType = {
    if (file == null)
      ContentTypes.NoContentType
    else
      ContentType(parseMediaType(file.getName), charset)
  }

  def parseMediaType(fileName: String): MediaType = {
    if (fileName == null)
      null
    else {
      val extension = parseFileExtension(fileName)
      MediaTypes.forExtension(extension)
    }
  }

  def parseFileExtension(fileName: String): String = {
    if (fileName == null || fileName == "")
      ""
    else {
      val extPos = fileName.lastIndexOf(".")
      if (extPos == -1)
        ""
      else
        fileName.substring(extPos + 1)
    }
  }

  private val charset = () => HttpCharsets.`UTF-8`
}
