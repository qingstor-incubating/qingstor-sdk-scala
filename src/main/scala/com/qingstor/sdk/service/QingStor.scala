package com.qingstor.sdk.service

import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Types.BucketModel
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.QingStor._
import com.qingstor.sdk.exception.QingStorException
import scala.concurrent.{ExecutionContextExecutor, Future}
import com.qingstor.sdk.service.QSCodec.QSOutputCodec._

// QingStorService: QingStor provides low-cost and reliable online storage service with unlimited storage space, high read and write performance, high reliability and data safety, fine-grained access control, and easy to use API.
class QingStor(_config: QSConfig) {
  implicit val system = QSConstants.QingStorSystem
  implicit val materializer = ActorMaterializer()
  implicit val ece: ExecutionContextExecutor = system.dispatcher
  val config: QSConfig = _config

  // ListBuckets does Retrieve the bucket list.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/service/get.html
  def listBuckets(input: ListBucketsInput): Future[ListBucketsOutput] = {
    val request = listBucketsRequest(input)
    val operation = request.operation
    val futureResponse = request.send()
    ResponseUnpacker
      .unpackWithElements[ListBucketsOutput](futureResponse, operation)
      .map({
        case Left(errorMessage) => throw QingStorException(errorMessage)
        case Right(output) => output
      })
  }

  // ListBucketsRequest creates request and output object of ListBuckets.
  def listBucketsRequest(input: ListBucketsInput): QSRequest = {
    val operation = Operation(
      config = config,
      apiName = "Get Service",
      method = "GET",
      requestUri = "/",
      statusCodes = 200 +: // OK
        Array[Int]()
    )
    QSRequest(operation, input)
  }

}

object QingStor {
  def apply(config: QSConfig): QingStor = new QingStor(config)

  case class ListBucketsInput(
      // Limits results to buckets that in the location
      location: Option[String] = None
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Location")
    def getLocation: Option[String] =
      this.location

  }

  case class ListBucketsOutput(
      // Buckets information
      buckets: Option[List[BucketModel]] = None,
      // Bucket count
      count: Option[Int] = None
  ) extends Output

}
