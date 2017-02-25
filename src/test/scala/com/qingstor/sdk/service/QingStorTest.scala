package com.qingstor.sdk.service

import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.QingStor.ListBucketsInput
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class QingStorTest extends FunSuite {
  val PWD: String = sys.env("PWD")
  val config: QSConfig = QSConfig.loadConfigFromFile(PWD+"/src/test/resources/config.yml")
  implicit val system = QSConstants.QSActorSystem
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()

  test("QingStorTest") {
    val input = ListBucketsInput(location = "pek3a")
    val qingStor = QingStor(config)
    val futureOutput = qingStor.listBuckets(input)
    futureOutput onComplete {
      case Success(out) =>
        out match {
          case Left(errorMessage) => println(errorMessage)
          case Right(output) => println(output)
        }
      case Failure(exception) =>
        exception.printStackTrace()
    }
    Await.ready(futureOutput, Duration.Inf)
  }
}
