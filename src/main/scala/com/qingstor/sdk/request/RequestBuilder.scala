package com.qingstor.sdk.request

import java.io._

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels.{Input, Operation}
import com.qingstor.sdk.util.{JsonUtil, QSLogger, QSRequestUtil, TimeUtil}

class RequestBuilder(op: Operation, in: Input) {
  private val operation = op
  private val input = in
  private val apiName = op.apiName
  private var bodyBytes = Array[Byte]()
  val parsedParams: Map[String, String] = parseParams()
  val parsedBody: RequestEntity = parseBody()
  val parsedHeaders: Map[String, String] = parseHeaders()

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
      QSLogger.error("No such method %s, use GET instead".format(str))
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
    val bucket =
      if (operation.bucketName.nonEmpty) "/" + operation.bucketName
      else operation.bucketName
    val zone = operation.zone
    val path = operation.requestUri
    val queries =
      if (parsedParams.isEmpty) ""
      if (path.contains("?")) "&" + Uri.Query(parsedParams)
      else "?" + Uri.Query(parsedParams)
//    Uri().withScheme(config.protocol)
//      .withHost(parseHost(config, zone))
//      .withPort(config.port)
//      .withPath(Uri.Path(bucket + path))
//      .withQuery(Uri.Query(parsedParams))
    "%s://%s:%d%s%s".format(config.protocol, parseHost(config, zone), config.port, bucket + path, queries)
  }

  private def parseHeaders(): Map[String, String] = {
    var headers = QSRequestUtil
      .getRequestParams(input, QSConstants.ParamsLocationHeader)
      .asInstanceOf[Map[String, String]]
    if (headers.getOrElse("Content-Length", "").isEmpty) {
      val length = parsedBody.contentLengthOption.getOrElse(0)
      if (length != 0)
        headers += ("Content-Length" -> length.toString)
    }
    if (headers.getOrElse("Date", "").isEmpty) {
      val now = TimeUtil.zonedDateTimeToString()
      headers += ("Date" -> now)
    }
    if (headers.getOrElse("Content-Type", "").isEmpty)
      if (parsedBody.contentType.equals(ContentTypes.`application/json`))
        headers += ("Content-Type" -> "application/json")
    headers
  }

  private def parseBody(): RequestEntity = {
    val elements =
      QSRequestUtil.getRequestParams(input, QSConstants.ParamsLocationElement)
    if (elements.nonEmpty) {
      bodyBytes = JsonUtil.encode(elements).compactPrint.getBytes
      HttpEntity(ContentTypes.`application/json`, bodyBytes)
    } else {
      val body: Map[String, AnyRef] = QSRequestUtil
        .getRequestParams(input, QSConstants.ParamsLocationBody)
      if (body.isEmpty)
        HttpEntity.Empty
      else if (body.contains("Body")) {
        val Body = body.getOrElse("Body", "")
        Body match {
          case bodyString: String =>
            bodyBytes = bodyString.getBytes
            HttpEntity(ContentTypes.`text/plain(UTF-8)`, bodyBytes)
          case ins: InputStream =>
            val bis = new BufferedInputStream(ins)
            val bytes = new Array[Byte](bis.available())
            bis.read(bytes)
            bodyBytes = bytes
            HttpEntity(ContentTypes.`application/octet-stream`, bytes)
        }
      } else {
        bodyBytes = JsonUtil.encode(body).compactPrint.getBytes
        HttpEntity(ContentTypes.`application/json`, bodyBytes)
      }
    }
  }

  private def buildHeader(): List[HttpHeader] = {
    var listHeaders = List[HttpHeader]()
    if (parsedHeaders.nonEmpty) {
      for ((key, value) <- parsedHeaders) {
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
