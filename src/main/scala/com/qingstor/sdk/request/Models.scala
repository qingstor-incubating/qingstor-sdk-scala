package com.qingstor.sdk.request

import java.io.File

import com.qingstor.sdk.config.QSConfig

object Models {
  case class Input(
      params: Map[String, String],
      headers: Map[String, String],
      elements: Map[String, Any] = null,
      body: File = null
  )

  abstract class Output

  case class ErrorMessage(
      code: String,
      message: String,
      request_id: String,
      url: String
  )

  case class Property(
      config: QSConfig,
      zone: String,
      apiName: String,
      method: String,
      bucketName: String,
      requestUri: String
  )
}
