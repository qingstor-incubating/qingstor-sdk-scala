package com.qingstor.sdk.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.model.QSModels._
import com.qingstor.sdk.request.{QSRequest, ResponseUnpacker}
import com.qingstor.sdk.service.Types.BucketModel
import com.qingstor.sdk.annotation.ParamAnnotation
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.QingStor._
import com.qingstor.sdk.service.QSJsonProtocol._
import scala.concurrent.{ExecutionContextExecutor, Future}

// QingStorService: QingStor provides low-cost and reliable online storage service with unlimited storage space, high read and write performance, high reliability and data safety, fine-grained access control, and easy to use API.
class QingStor(_config: QSConfig)(implicit val system: ActorSystem,
                                  val mat: ActorMaterializer,
                                  val ec: ExecutionContextExecutor) {
  val config: QSConfig = _config

  // ListBuckets does Retrieve the bucket list.
  // Documentation URL: https://docs.qingcloud.com/qingstor/api/service/get.html
  def listBuckets(input: ListBucketsInput)
    : Future[Either[ErrorMessage, ListBucketsOutput]] = {
    val operation = Operation(
      config = config,
      apiName = "Get Service",
      method = "GET",
      requestUri = "/",
      statusCodes = 200 +: // OK
        Array[Int]()
    )

    val futureResponse = QSRequest(operation, input).send()

    ResponseUnpacker.unpackToOutputOrErrorMessage[ListBucketsOutput](
      futureResponse,
      operation.statusCodes)

  }

}

object QingStor {
  def apply(config: QSConfig)(implicit system: ActorSystem,
                              mat: ActorMaterializer,
                              ec: ExecutionContextExecutor): QingStor =
    new QingStor(config)

  case class ListBucketsInput(
      // Limits results to buckets that in the location
      location: Option[String] = None
  ) extends Input {

    @ParamAnnotation(location = QSConstants.ParamsLocationHeader,
                     name = "Location")
    def getLocation = this.location

  }
  case class ListBucketsOutput(
      // Buckets information
      `buckets`: Option[List[BucketModel]] = None,
      // Bucket count
      `count`: Option[Int] = None
  ) extends Output

}
