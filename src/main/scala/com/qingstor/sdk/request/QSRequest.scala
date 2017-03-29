package com.qingstor.sdk.request

import akka.actor.ActorSystem
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.ActorMaterializer
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.util.QSLogger
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

class QSRequest(_operation: Operation, _input: Input) {
  private val input = _input
  val operation: Operation = _operation
  val HTTPRequest: HttpRequest = build()

  def send()(implicit system: ActorSystem,
             mat: ActorMaterializer): Future[QSHttpResponse] = {
    import system.dispatcher
    val config = ConfigFactory.parseString(s"""
         |akka.http {
         |  host-connection-pool {
         |    client {
         |      connecting-timeout = "5s"
         |      connection-timeout = "5s"
         |      user-agent-header = "${QSConstants.UserAgent}"
         |    }
         |    max-retries = ${operation.config.connection_retries}
         |  }
         |}
      """.stripMargin).withFallback(ConfigFactory.defaultReference())
    Http(system).singleRequest(
      request = sign(HTTPRequest),
      settings = ConnectionPoolSettings(config)
    ).map(ResponseUnpacker(_, operation).unpackResponse())
  }

  private def build(): HttpRequest = {
    if (!check())
      QSLogger.fatal(
        "Fatal: Access Key ID or Secret Access Key can't be empty")
    val builder = RequestBuilder(operation, input)
    builder.build
  }

  private def sign(request: HttpRequest = HTTPRequest): HttpRequest = {
    val accessKeyID = operation.config.access_key_id
    val secretAccessKey = operation.config.secret_access_key
    val authString =
      QSSigner.getHeadAuthorization(request, accessKeyID, secretAccessKey)
    request.addHeader(RawHeader("Authorization", authString))
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
}

object QSRequest {
  def apply(operation: Operation, input: Input): QSRequest =
    new QSRequest(operation, input)
  def apply(operation: Operation): QSRequest = new QSRequest(operation, null)

  def signQueries(request: QSRequest, liveTime: Long): Uri = {
    val expires = System.currentTimeMillis() + liveTime
    val accessKeyID = request.operation.config.access_key_id
    val secretAccessKey = request.operation.config.secret_access_key
    val authQueries =
      QSSigner.getQueryAuthorization(request.HTTPRequest,
                                     accessKeyID,
                                     secretAccessKey,
                                     expires)
    val oriQueries = request.HTTPRequest.uri.query().toMap
    request.HTTPRequest.uri.withQuery(Uri.Query(oriQueries ++ authQueries))
  }
}
