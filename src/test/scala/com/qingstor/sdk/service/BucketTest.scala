package com.qingstor.sdk.service

import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.Bucket.DeleteMultipleObjectsInput
import com.qingstor.sdk.service.Types.ObjectModel
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class BucketTest extends FunSuite{
  val PWD: String = sys.env("PWD")
  val config: QSConfig = QSConfig.loadConfigFromFile(PWD+"/src/test/resources/config.yml")
  implicit val system = QSConstants.QSActorSystem
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()

  test("Test DeleteMultipleObjects") {
    val bucket = Bucket(config, "mybuket", "pek3a")
    val _input = DeleteMultipleObjectsInput (
      objects = List(ObjectModel(key = "demo.go")),
      contentMD5 = "<>"
    )
    val md5 = Bucket.getContentMD5OfDeleteMultipleObjectsInput(_input)
    val input = _input.setContentMD5(md5)
    val futureOutput = bucket.deleteMultipleObjects(input)
    futureOutput onComplete {
      case Success(out) =>
        out match {
          case Left(errorMessage) => println(errorMessage)
          case Right(output) => println(output)
        }
      case Failure(exception) => exception.printStackTrace()
    }
    Await.ready(futureOutput, Duration.Inf)
  }
}
