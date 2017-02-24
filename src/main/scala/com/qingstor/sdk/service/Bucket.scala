package com.qingstor.sdk.service

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels.{Input, Output, Operation}
import com.qingstor.sdk.request.QSRequest
import com.qingstor.sdk.service.Bucket.DeleteMultipleObjectsOutput
import com.qingstor.sdk.service.Types.DeleteErrorModel

import CustomJsonProtocol._

class Bucket(_config: QSConfig, _bucketName: String, _zone: String) {
  val config: QSConfig = _config
  val bucketName: String = _bucketName
  val zone: String = _zone

//  def deleteMultipleObjects(input: Input): Either[Throwable, DeleteMultipleObjectsOutput] = {
//    val property = Operation(config, zone, "Delete Multiple Objects", "POST", bucketName, "/?delete")
//    QSRequest(property, input)
//      .send()
//      .unpack[DeleteMultipleObjectsOutput]()
//  }
}

object Bucket {
  def apply(config: QSConfig, bucketName: String, zone: String): Bucket = new Bucket(config, bucketName, zone)
  case class DeleteMultipleObjectsOutput(deleted: List[Map[String, String]],
                                         errors: List[DeleteErrorModel] = List.empty) extends Output
}
