package com.qingstor.sdk.request

import java.io.File
import java.nio.file.Paths
import javax.activation.MimetypesFileTypeMap

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constants.QSConstants
import com.qingstor.sdk.request.Models.{Input, Property}
import com.qingstor.sdk.utils.{Json, TimeUtil}

class RequestBuilder(_property: Property, _input: Input) {
  private val context = _property
  private val input = _input
  private val apiName = _property.apiName
  var parsedHeaders: Map[String, String] = Option(input.headers).getOrElse(Map.empty)
  var parsedBody: RequestEntity = HttpEntity.Empty
  var parsedBodyString: String = ""

  def build(): HttpRequest = {
    var request = HttpRequest()
      .withUri(parseUrl)
      .withMethod(parseHttpMethod)
      .withProtocol(HttpProtocols.`HTTP/1.1`)
      .withEntity(parseBody())

    setupHeaders(request)
  }

  private def parseHttpMethod = context.method match {
    case "GET" => HttpMethods.GET
    case "POST" => HttpMethods.POST
    case "HEAD" => HttpMethods.HEAD
    case "PUT" => HttpMethods.PUT
    case "DELETE" => HttpMethods.DELETE
  }

  private def parseHost(config: QSConfig, zone: String): String = {
    if (apiName.equals(QSConstants.APIGETServiceName) || zone == null || zone.isEmpty)
      config.host
    else
      zone + "." + config.host
  }

  private def parseBody(): RequestEntity = {
    if (input.elements != null && input.elements.nonEmpty) {
      parsedBodyString = Json.encode(input.elements).toString()
      parsedHeaders += ("Content-Type" -> "application/json")
      parsedBody =
        HttpEntity(ContentTypes.`application/json`, parsedBodyString)
    } else {
      if (input.body != null) {
        parsedBody = HttpEntity.fromPath(parseContentType(input.body),
          Paths.get(input.body.getAbsolutePath))
      }
    }
    parsedBody
  }

  private def parseUrl: String = {
    val config = context.config
    val bucket = if (context.bucketName.nonEmpty) "/" + context.bucketName else context.bucketName
    val zone = context.zone
    val path = context.requestUri
    val _queryString = Option(input.params).getOrElse(Map.empty).mkString("&").replace(" -> ", "=")
    val queryString = if (_queryString.nonEmpty) "?" + _queryString.substring(0, _queryString.lastIndexOf("&")) else ""
    new java.net.URI(config.protocol+"://"+parseHost(config, zone) + bucket + path + queryString).toASCIIString
  }

  private def parseContentType(file: File): ContentType = {
    new MimetypesFileTypeMap().getContentType(file) match {
      case "application/json" => ContentTypes.`application/json`
      case "text/csv" => ContentTypes.`text/csv(UTF-8)`
      case "text/html" => ContentTypes.`text/html(UTF-8)`
      case "text/plain" => ContentTypes.`text/plain(UTF-8)`
      case "text/xml" => ContentTypes.`text/xml(UTF-8)`
      case _ => ContentTypes.`application/octet-stream`
    }
  }

  private def setupHeaders(request: HttpRequest): HttpRequest = {
    if (request.getHeader("Content-Length").orElse(null) == null ||
        request.getHeader("Content-Length").get().value() == "") {
      val length: Long = input.body match {
        case null => parsedBodyString match {
          case null => 0
          case content: String => content.length
        }
        case body: File => body.length()
      }
      if (length > 0)
        parsedHeaders += ("Content-Length" -> length.toString)
    }
    if (request.getHeader("Date").orElse(null) == null ||
        request.getHeader("Date").get().value() == "") {
      val timeNow = TimeUtil.zonedDateTimeToString()
      parsedHeaders += ("Date" -> timeNow)
    }
    var headers = List[HttpHeader]()
    if (parsedHeaders.nonEmpty) {
      parsedHeaders.keys.foreach { key =>
        headers = headers ::: List(RawHeader(key, parsedHeaders(key)))
      }
    }
    request.withHeaders(headers)
  }
}
