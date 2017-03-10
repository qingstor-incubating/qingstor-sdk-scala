package com.qingstor.sdk.request

import java.io.File
import java.net.URI

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.util.ByteString
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels.{Input, Operation}
import com.qingstor.sdk.util.{JsonUtil, QSLogger, QSRequestUtil, TimeUtil}

class RequestBuilder(op: Operation, in: Input) {
  private val operation = op
  private val input = in
  private val apiName = op.apiName
  private var givenContentType: Either[List[ErrorInfo], ContentType] = _
  val parsedParams: Map[String, String] = parseParams()
  val parsedHeaders: Map[String, String] = parseHeaders()
  val parsedBody: RequestEntity = parseBody()

  def build: HttpRequest = {
    HttpRequest()
      .withUri(parseUri)
      .withMethod(parseHttpMethod)
      .withProtocol(HttpProtocols.`HTTP/1.1`)
      .withHeaders(buildHeader())
      .withEntity(parsedBody)
  }

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

  private def parseParams(): Map[String, String] = {
    QSRequestUtil
      .getRequestParams(input, QSConstants.ParamsLocationParam)
      .asInstanceOf[Map[String, String]]
  }

  private def parseHost(config: QSConfig, zone: String): String = {
    if (apiName.equals(QSConstants.APIGETService) || zone == null || zone.isEmpty)
      config.host
    else
      zone + "." + config.host
  }

  private def parseUri: Uri = {
    val config = operation.config
    val zone = operation.zone
    val requestURI = operation.requestUri.replace(QSConstants.BucketNamePlaceHolder, operation.bucketName)
                                          .replace(QSConstants.ObjectKeyPlaceHolder, operation.objectKey)
    val path = new URI(requestURI).toASCIIString
    val queries = new URI(
      if (parsedParams.isEmpty) ""
      else if (path.contains("?")) "&" + Uri.Query(parsedParams)
      else "?" + Uri.Query(parsedParams)
    ).toASCIIString
    Uri("%s://%s:%d%s%s".format(config.protocol, parseHost(config, zone), config.port, path, queries))
  }

  private def parseHeaders(): Map[String, String] = {
    var headers = Map.empty[String, String]
    if (input != null) {
      headers = QSRequestUtil
        .getRequestParams(input, QSConstants.ParamsLocationHeader)
        .asInstanceOf[Map[String, String]]
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
      val elements =
        QSRequestUtil.getRequestParams(input, QSConstants.ParamsLocationElement)
      if (elements.nonEmpty) {
        val bytes = JsonUtil.encode(elements).compactPrint.getBytes
        HttpEntity(givenContentType.getOrElse(ContentTypes.`application/json`), bytes)
      } else {
        val body: Map[String, AnyRef] = QSRequestUtil
          .getRequestParams(input, QSConstants.ParamsLocationBody)
        if (body.isEmpty)
          HttpEntity(givenContentType.getOrElse(ContentTypes.NoContentType), ByteString.empty)
        else if (body.contains("Body")) {
          val Body = body.getOrElse("Body", "")
          Body match {
            case bodyString: String =>
              HttpEntity(givenContentType.getOrElse(ContentTypes.`text/plain(UTF-8)`), bodyString.getBytes)
            case file: File =>
              HttpEntity.fromPath(givenContentType.getOrElse(QSRequestUtil.parseContentType(file)), file.toPath)
            case bytes: Array[Byte] =>
              HttpEntity(bytes)
          }
        } else {
          val bytes = JsonUtil.encode(body).compactPrint.getBytes
          HttpEntity(givenContentType.getOrElse(ContentTypes.`application/json`), bytes)
        }
      }
    } else {
      HttpEntity(givenContentType.getOrElse(ContentTypes.NoContentType), ByteString.empty)
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
  def apply(operation: Operation, input: Input): RequestBuilder =
    new RequestBuilder(operation, input)
}
