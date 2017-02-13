package com.qingstor.sdk.request

import java.io.File

import akka.actor.{Actor, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.request.QSRequest.{Input, Output, Property}

class QSRequest(c: Property, i: Input) extends Actor{
  import context.dispatcher
  import akka.pattern.pipe

  private val property = c
  private val input = i
  var HTTPRequest: HttpRequest = _
  var HTTPResponse: HttpResponse = _

  final implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)

  private def check(): Boolean = {
    val id = property.config.accessKeyID
    val secret = property.config.secretAccessKey
    id != null && secret != null && id != "" && secret != ""
  }

  override def preStart(): Unit = {
    super.preStart()
    println("preStart")
//    check()
//    build()
//    sign()
//    http.singleRequest(HTTPRequest).pipeTo(self)
  }

  def signQueries(liveTime: Long): Unit = {
    val expires = System.currentTimeMillis() + liveTime
    signQuery(expires)
  }

  override def receive: Receive = {
    case _ => println("recieve" + _)
    case request: HttpRequest =>
      http.singleRequest(request).pipeTo(self)
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      println(headers)
      println(entity)
    case HttpResponse(code, _, entity, _) =>
      println("error code: " + code)
      println(entity)
  }

  private def build() = {
    HTTPRequest = new RequestBuilder(property, input).build()
  }

  private def sign() = {
    val accessKeyID = property.config.getAccessKeyID
    val authString = QSSigner.getHeadAuthorization(HTTPRequest, accessKeyID)
    HTTPRequest.addHeader(RawHeader("Authorization", authString))
  }

  private def signQuery(expires: Long) = {
    val accessKeyID = property.config.getAccessKeyID
    val authQueries =
      QSSigner.getQueryAuthorization(HTTPRequest, accessKeyID, expires)
    val queries =
      if (HTTPRequest.uri.query().nonEmpty)
        HTTPRequest.uri.query() + "&" + authQueries
      else authQueries
    val uri = HTTPRequest.uri.withQuery(Uri.Query(queries))
    HTTPRequest = HTTPRequest.withUri(uri)
  }
}

object QSRequest {

  case class Input(
      params: Map[String, String],
      headers: Map[String, String],
      elements: Map[String, Any] = null,
      body: File = null
  )

  abstract class Output

  case class Property(
      config: QSConfig,
      zone: String,
      apiName: String,
      method: String,
      bucketName: String,
      requestUri: String
  )

  def props(c: Property, i: Input): Props = Props(classOf[QSRequest], c, i)
}
