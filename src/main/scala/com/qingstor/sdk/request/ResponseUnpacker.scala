package com.qingstor.sdk.request

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels.{Operation, QSHttpResponse}
import com.qingstor.sdk.util.{ClassUtil, QSRequestUtil}

import scala.reflect.ClassTag

class ResponseUnpacker(private val _response: HttpResponse,
                       private val _operation: Operation) {
  private val response = _response
  private val operation = _operation

  def unpackResponse[T <: QSHttpResponse :ClassTag](): T = {
    val clazz = ClassUtil.ClassBuilder[T]
    setupStatusCodeAndRequestID(clazz)
    setupHeaders(clazz)
    setupEntity(clazz)
    clazz
  }

  private def setupStatusCodeAndRequestID(obj: Any) = {
    val statusCode = response.status.intValue()
    QSRequestUtil.invokeMethod(obj,
      "setStatusCode",
      Array(int2Integer(statusCode)))
    val emptyHeader = RawHeader(QSConstants.`X-QS-Request-ID`, "")
    val optionHeaders =
      response.headers.find(_.name() == QSConstants.`X-QS-Request-ID`)
    val requestID = optionHeaders.getOrElse(emptyHeader).value()
    QSRequestUtil.invokeMethod(obj, "setRequestID", Array(requestID))
  }

  private def setupHeaders(obj: Any) = {
    if (isRightStatusCode(response)) {
      val headersNameAndGetMethodname =
        QSRequestUtil.getResponseParams(obj, "headers")
      for ((headerName, methodName) <- headersNameAndGetMethodname) {
        val header = response.headers.find(h => h.name().equals(headerName))
        if (header.isDefined) {
          val setMethodName = methodName.replaceFirst("get", "set")
          QSRequestUtil.invokeMethod(obj,
            setMethodName,
            Array(header.get.value()))
        }
      }
    }
  }

  private def setupEntity(obj: Any) = {
    QSRequestUtil.invokeMethod(obj, "setEntity", Array(response.entity))
  }

  private def isRightStatusCode(response: HttpResponse): Boolean = {
    val rightStatus = operation.statusCodes match {
      case null => Array(200)
      case Array.emptyIntArray => Array(200)
      case _ => operation.statusCodes
    }
    rightStatus.contains(response.status.intValue())
  }
}

object ResponseUnpacker {
  def apply(response: HttpResponse, operation: Operation) =
    new ResponseUnpacker(response, operation)
}
