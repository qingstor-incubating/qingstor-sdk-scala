package com.qingstor.sdk.service

import java.time.ZonedDateTime

object Types {
  case class BucketModel(name: String, location: String, url: String, created: ZonedDateTime)
  case class DeleteErrorModel(key: String, code: String, message: String)
  case class ObjectModel(key: String)
}
