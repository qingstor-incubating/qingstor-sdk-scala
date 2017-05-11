package com.qingstor.sdk.request

import java.io.File

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.util.ByteString
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels.{Input, Operation}
import com.qingstor.sdk.util.{QSLogger, QSRequestUtil, TimeUtil}

class RequestBuilder(op: Operation, in: Input) {
  private val operation = op
  private val input = in
  private val apiName = op.apiName
  private var givenContentType: Either[List[ErrorInfo], ContentType] = _
  val parsedParams: Map[String, String] = parseParams()
  val parsedHeaders: Map[String, String] = parseHeaders()
  val parsedBody: RequestEntity = parseBody()

  def build: HttpRequest =
    HttpRequest()
      .withUri(parseUri)
      .withMethod(parseHttpMethod)
      .withProtocol(HttpProtocols.`HTTP/1.1`)
      .withHeaders(buildHeader())
      .withEntity(parsedBody)

  private def parseHttpMethod = operation.method.toUpperCase match {
    case "GET" => HttpMethods.GET
    case "POST" => HttpMethods.POST
    case "HEAD" => HttpMethods.HEAD
    case "PUT" => HttpMethods.PUT
    case "DELETE" => HttpMethods.DELETE
    case "OPTIONS" => HttpMethods.OPTIONS
    case "CONNECT" => HttpMethods.CONNECT
    case "PATCH" => HttpMethods.PATCH
    case "TRACE" => HttpMethods.TRACE
    case str: String =>
      QSLogger.warn("No such method %s, use GET instead".format(str))
      HttpMethods.GET
  }

  private def parseParams(): Map[String, String] =
    QSRequestUtil
      .getRequestParams(input, QSConstants.ParamsLocationParam)
      .asInstanceOf[Map[String, String]]

  private def parseHost(config: QSConfig, zone: String): String =
    if (apiName.equals(QSConstants.APIGETService) || zone == null || zone.isEmpty)
      config.host
    else
      zone + "." + config.host

  private def parseUri: Uri = {
    val scheme = operation.config.protocol
    val host = parseHost(operation.config, operation.zone)
    val zone = operation.zone
    val port =operation.config.port
    val objectKey = operation.objectKey.replace("%", "%25")
    val requestURI =
      operation.requestUri
        .replace(QSConstants.BucketNamePlaceHolder, operation.bucketName)
        .replace(QSConstants.ObjectKeyPlaceHolder, Uri.Path(objectKey).toString())
    val queries =
      if (parsedParams.isEmpty) ""
      else if (requestURI.contains("?")) "&" + Uri.Query(parsedParams)
      else "?" + Uri.Query(parsedParams)

    Uri(s"$scheme://$host:$port$requestURI$queries")
  }

  private def parseHeaders(): Map[String, String] = {
    var headers = Map.empty[String, String]
    if (input != null)
      headers = QSRequestUtil.getRequestParams(input, QSConstants.ParamsLocationHeader)
        .asInstanceOf[Map[String, String]]

    headers.foreach { entry =>
      val key = entry._1
      val value =
        if (key.equals("Date")) entry._2
        else Uri.Path(entry._2.replace("%", "%25")).toString()

      headers += (key -> value)
    }
    if (headers.getOrElse("Date", "").isEmpty) {
      val now = TimeUtil.zonedDateTimeToString()
      headers += ("Date" -> now)
    }
    givenContentType = ContentType.parse(headers.getOrElse("Content-Type", ""))
    headers.filterNot(entry => entry._1.equals("Content-Type"))
  }

  private def parseBody(): RequestEntity = {
    if (input != null) {
      val elements = QSRequestUtil.getRequestParams(input, QSConstants.ParamsLocationElement)
      if (elements.nonEmpty) {
        val bytes = elements.getOrElse(QSConstants.ParamsLocationElement, "").asInstanceOf[String].getBytes
        HttpEntity(RequestBuilder.eitherGetOrElse(givenContentType, ContentTypes.`application/json`), bytes)
      } else {
        val body: Map[String, AnyRef] = QSRequestUtil.getRequestParams(input, QSConstants.ParamsLocationBody)
        if (body.isEmpty)
          HttpEntity(RequestBuilder.eitherGetOrElse(givenContentType, ContentTypes.NoContentType), ByteString.empty)
        else {
          val Body = body.getOrElse("Body", "")
          Body match {
            case bodyString: String =>
              HttpEntity(RequestBuilder.eitherGetOrElse(givenContentType,
                ContentTypes.`text/plain(UTF-8)`), bodyString.getBytes)
            case file: File =>
              HttpEntity.fromPath(RequestBuilder.eitherGetOrElse(givenContentType,
                QSRequestUtil.parseContentType(file)), file.toPath)
            case bytes: Array[Byte] =>
              HttpEntity(bytes)
          }
        }
      }
    } else {
      HttpEntity(RequestBuilder.eitherGetOrElse(givenContentType, ContentTypes.NoContentType), ByteString.empty)
    }
  }

  private def buildHeader(): List[HttpHeader] = {
    var listHeaders = List[HttpHeader]()
    if (parsedHeaders.nonEmpty) {
      for ((key, value) <- parsedHeaders) {
        if (!key.equals("Content-Length"))
          listHeaders = RawHeader(key, value) :: listHeaders
      }
    }
    listHeaders
  }
}

object RequestBuilder {
  def apply(operation: Operation, input: Input): RequestBuilder = new RequestBuilder(operation, input)

  def eitherGetOrElse[B](either: Either[_, B], or: B): B = either match {
    case Right(r) => r
    case Left(_) => or
  }
}
