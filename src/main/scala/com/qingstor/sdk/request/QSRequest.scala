package com.qingstor.sdk.request

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.ActorMaterializer
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.util.QSLogger

import scala.concurrent.Future
import scala.reflect.ClassTag

class QSRequest(_operation: Operation, _input: Input) {
  private val operation = _operation
  private val input = _input

  def build(): HttpRequest = {
    if (!check())
      QSLogger.fatal(
        "Fatal: Access Key ID or Secret Access Key can't be empty")
    val builder = RequestBuilder(operation, input)
    builder.build
  }

  def sign(request: HttpRequest): HttpRequest = {
    val accessKeyID = operation.config.access_key_id
    val secretAccessKey = operation.config.secret_access_key
    val authString =
      QSSigner.getHeadAuthorization(request, accessKeyID, secretAccessKey)
    request.addHeader(RawHeader("Authorization", authString))
  }

  def signQueries(request: HttpRequest, liveTime: Long): HttpRequest = {
    val expires = System.currentTimeMillis() + liveTime
    signQuery(request, expires)
  }

  def send[T <: QSHttpResponse: ClassTag](_request: HttpRequest = null)(
      implicit system: ActorSystem,
      mat: ActorMaterializer): Future[T] = {
    import system.dispatcher
    var request = _request
    if (request == null)
      request = sign(build())
    Http(system).singleRequest(request).map { response =>
      ResponseUnpacker(response, operation).unpackResponse[T]()
    }
  }

  private def check(): Boolean = {
    if (operation.config != null) {
      val id = operation.config.access_key_id
      val secret = operation.config.secret_access_key
      id != null && secret != null && id != "" && secret != ""
    } else {
      false
    }
  }

  private def signQuery(request: HttpRequest, expires: Long): HttpRequest = {
    val accessKeyID = operation.config.access_key_id
    val secretAccessKey = operation.config.secret_access_key
    val authQueries =
      QSSigner.getQueryAuthorization(request,
                                     accessKeyID,
                                     secretAccessKey,
                                     expires)
    val oriQueries = request.uri.query().toMap

    val uri = request.uri.withQuery(Uri.Query(oriQueries ++ authQueries))
    request.withUri(uri)
  }
}

object QSRequest {
  def apply(operation: Operation, input: Input): QSRequest =
    new QSRequest(operation, input)
  def apply(operation: Operation): QSRequest = new QSRequest(operation, null)
}
