package com.qingstor.sdk.request

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.stream.ActorMaterializer
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.exception.QingStorException
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.util.QSRequestUtil
import spray.json.{JsValue, JsonFormat}
import com.qingstor.sdk.service.QSJsonProtocol._
import scala.reflect._
import scala.concurrent.{ExecutionContextExecutor, Future}

class ResponseUnpacker(_response: HttpResponse, _operation: Operation) {
  private val response = _response
  private val operation = _operation

  def unpackResponse(): QSHttpResponse = {
    val clazz = new QSHttpResponse()
    setupStatusCode(clazz)
    setupHeaders(clazz)
    setupEntity(clazz)
    clazz
  }

  private def setupStatusCode(obj: Any) = {
    val statusCode = response.status.intValue()
    QSRequestUtil.invokeMethod(obj,
      "setStatusCode",
      Array(int2Integer(statusCode)))
  }

  private def setupHeaders(obj: Any) = {
    if (ResponseUnpacker.isRightStatusCode(response.status.intValue(), operation.statusCodes)) {
      val headersNameAndGetMethodname =
        QSRequestUtil.getResponseParams(obj, QSConstants.ParamsLocationHeader)
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
}

object ResponseUnpacker {
  def apply(response: HttpResponse, operation: Operation) =
    new ResponseUnpacker(response, operation)

  def isRightStatusCode(code: Int, rightCodes: Array[Int] = Array[Int](200)): Boolean = {
    rightCodes.contains(code)
  }

  def unpack[T <: Output :JsonFormat :ClassTag](futureResponse: Future[QSHttpResponse],
                                      rightStatusCodes: Array[Int])
    (implicit system: ActorSystem, mat: ActorMaterializer,
      ec: ExecutionContextExecutor): Future[T]= {
    futureResponse.flatMap { response =>
      if (ResponseUnpacker.isRightStatusCode(response.getStatusCode, rightStatusCodes)) {
        val clazz = classTag[T].runtimeClass
        if (clazz.getName.equals(classOf[Output].getName)){
          val output = new Output()
          output.statusCode = Option(response.getStatusCode)
          output.requestID = Option(response.getRequestID)
          Future(output.asInstanceOf[T])
        } else {
          ResponseUnpacker.unpackToOutput[T](response).map { out =>
            out.statusCode = Option(response.getStatusCode)
            out.requestID = Option(response.getRequestID)
            out
          }
        }
      } else {
        ResponseUnpacker.unpackToErrorMessage(response).map{ error =>
          error.statusCode = Option(response.getStatusCode)
          throw QingStorException(error)
        }
      }
    }
  }

  def unpackToOutput[T <: Output :JsonFormat](response: QSHttpResponse)
                                             (implicit system: ActorSystem,
                                              mat: ActorMaterializer,
                                              ec: ExecutionContextExecutor): Future[T] = {
    Unmarshal(response.getEntity).to[JsValue].map(_.convertTo[T])
  }

  def unpackToErrorMessage(response: QSHttpResponse)
                          (implicit system: ActorSystem,
                           mat: ActorMaterializer,
                           ec: ExecutionContextExecutor): Future[ErrorMessage] = {
    response.getEntity.contentType match {
      case ContentTypes.`application/json` =>
        val errorFuture = Unmarshal(response.getEntity).to[JsValue]
        errorFuture.map(_.convertTo[ErrorMessage])
      case _ =>
        Future {ErrorMessage(
          requestID = response.getRequestID,
          statusCode = Some(response.getStatusCode)
        )}
    }
  }
}
