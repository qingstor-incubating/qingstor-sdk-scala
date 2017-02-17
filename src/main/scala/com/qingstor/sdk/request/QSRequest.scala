package com.qingstor.sdk.request

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import com.qingstor.sdk.request.Models._
import com.qingstor.sdk.utils.{Json, QSLogger}
import spray.json.DefaultJsonProtocol._
import spray.json.JsValue

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class QSRequest(_property: Property, _input: Input) {
  private val property = _property
  private val input = _input
  var HTTPRequest: HttpRequest = _
  var HTTPResponse: HttpResponse = _

  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()
  private implicit val errorMessage = jsonFormat4(ErrorMessage)
  private implicit val um: Unmarshaller[HttpEntity, JsValue] = {
    Unmarshaller.byteStringUnmarshaller.mapWithCharset { (data, charset) =>
      Json.encode(data.decodeString(charset.value))
    }
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  def send(onSuccess: HttpResponse => Unit,
           onError: ErrorMessage => Unit): Unit = {
    if (!check())
      QSLogger.fatal("Access Key ID or Secret Access Key can't be empty")
    build()
    sign()
    val responseFuture = Http().singleRequest(HTTPRequest)
    responseFuture onComplete {
      case Failure(fail) =>
        QSLogger.fatal("Send request failed: " + fail.getMessage)
      case Success(response) =>
        response match {
          case resp @ HttpResponse(status, _, _, _) =>
            if (status.isFailure())
              onError(
                Unmarshal(resp.entity)
                  .to[JsValue]
                  .value
                  .get
                  .get
                  .convertTo[ErrorMessage])
            else if (status.isSuccess()) onSuccess(resp)
        }
    }
    Await.result(responseFuture, 5.second)
  }

  def signQueries(liveTime: Long): HttpRequest = {
    val expires = System.currentTimeMillis() + liveTime
    signQuery(expires)
    HTTPRequest
  }

  private def check(): Boolean = {
    val id = property.config.accessKeyID
    val secret = property.config.secretAccessKey
    id != null && secret != null && id != "" && secret != ""
  }

  private def build() = {
    HTTPRequest = new RequestBuilder(property, input).build()
  }

  private def sign() = {
    val accessKeyID = property.config.getAccessKeyID
    val secretAccessKey = property.config.getSecretAccessKey
    val authString =
      QSSigner.getHeadAuthorization(HTTPRequest, accessKeyID, secretAccessKey)
    HTTPRequest = HTTPRequest.addHeader(RawHeader("Authorization", authString))
  }

  private def signQuery(expires: Long) = {
    val accessKeyID = property.config.getAccessKeyID
    val secretAccessKey = property.config.getSecretAccessKey
    val authQueries =
      QSSigner.getQueryAuthorization(HTTPRequest,
                                     accessKeyID,
                                     secretAccessKey,
                                     expires)
    val queries =
      if (HTTPRequest.uri.query().nonEmpty)
        HTTPRequest.uri.query() + "&" + authQueries
      else authQueries
    val uri = HTTPRequest.uri.withQuery(Uri.Query(queries))
    HTTPRequest = HTTPRequest.withUri(uri)
  }
}
