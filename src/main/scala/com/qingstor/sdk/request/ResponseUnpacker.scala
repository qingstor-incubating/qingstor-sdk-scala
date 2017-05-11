package com.qingstor.sdk.request

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import akka.http.scaladsl.unmarshalling.Unmarshaller._
import akka.stream.ActorMaterializer
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.exception.QingStorException
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.util.QSRequestUtil
import io.circe._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.reflect._

object ResponseUnpacker {
  def unpackWithoutElements[T <: Output : ClassTag](responseFuture: Future[HttpResponse], operation: Operation)
  (implicit s: ActorSystem, m: ActorMaterializer, e: ExecutionContextExecutor)
  : Future[Either[ErrorMessage, T]] = responseFuture.flatMap { response =>
    if (isRightStatusCode(response.status.intValue(), operation.statusCodes)) {
      Unmarshal(response.entity).to[Array[Byte]].map { bytes =>
        val output = classTag[T].runtimeClass.newInstance().asInstanceOf[T]
        val noBody = QSRequestUtil.getResponseParams(output, QSConstants.ParamsLocationBody).isEmpty

        setupStatusCodeAndRequestID(output, response)
        setupHeaders(output, response)
        if (!noBody)
          QSRequestUtil.invokeMethod(output, "setBody", Array(bytes))
        Right(output)
      }
    } else {
      unpackToErrorMessage(response).map(Left(_))
    }
  }

  def unpackWithElements[T <: Output](responseFuture: Future[HttpResponse], operation: Operation)
  (implicit d: Decoder[T], s: ActorSystem, m: ActorMaterializer, e: ExecutionContextExecutor)
  : Future[Either[ErrorMessage, T]] = responseFuture.flatMap { response =>
    if (isRightStatusCode(response.status.intValue(), operation.statusCodes)) {
      Unmarshal(response.entity).to[Json].map { json =>
        val output = json.as[T] match {
          case Left(failure) =>
            val requestID = parseRequestID(response).getOrElse("Can't parse request_id")
            val error = ErrorMessage(requestID = requestID, message = Some(failure.message))
            throw QingStorException(error)
          case Right(t) => t
        }
        setupStatusCodeAndRequestID(output, response)
        setupHeaders(output, response)
        Right(output)
      }
    } else {
      unpackToErrorMessage(response).map(Left(_))
    }
  }

  private def setupStatusCodeAndRequestID[T <: Output](output: T, response: HttpResponse) = {
    output.statusCode = Option(response.status.intValue())
    val optionRequestID = parseRequestID(response)
    output.requestID = optionRequestID
  }

  private def setupHeaders[T <: Output](output: T, response: HttpResponse) = {
    val headerNameMap = QSRequestUtil.getResponseParams(output, QSConstants.ParamsLocationHeader)
    for ((headerName, method) <- headerNameMap) {
      val header = response.headers.find(_.name() == headerName)
      if (header.isDefined) {
        QSRequestUtil.invokeMethod(output, method, Array(header.get.value()))
      }
    }
  }

  private def isRightStatusCode(code: Int, rightCodes: Array[Int] = Array[Int](200)): Boolean =
    rightCodes.contains(code)

  private def unpackToErrorMessage(response: HttpResponse)
  (implicit system: ActorSystem, mat: ActorMaterializer, ec: ExecutionContextExecutor)
  : Future[ErrorMessage] =
    response.entity.contentType match {
      case ContentTypes.`application/json` =>
        val errorFuture = Unmarshal(response.entity).to[Json]
        errorFuture.map(_.as[ErrorMessage] match {
          case Left(failure) =>
            val requestID = parseRequestID(response).getOrElse("Can't parse request_id")
            val error = ErrorMessage(requestID = requestID, message = Some(failure.message),
              statusCode = Some(response.status.intValue()))
            throw QingStorException(error)
          case Right(errorMessage) =>
            errorMessage.statusCode = Some(response.status.intValue())
            errorMessage
        })
      case _ => Future(ErrorMessage(requestID = parseRequestID(response).get,
        statusCode = Some(response.status.intValue())))
    }

  private def parseRequestID(response: HttpResponse): Option[String] =
    response.headers.find(_.name() == "request_id").map(_.value())
}
