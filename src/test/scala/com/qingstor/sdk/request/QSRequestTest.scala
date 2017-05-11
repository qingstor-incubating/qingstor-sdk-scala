package com.qingstor.sdk.request

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.model.QSModels.{Input, Operation}
import io.circe.Json
import io.circe.syntax._
import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class QSRequestTest extends FunSuite {
  case class TestInput() extends Input
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  test("Test UserAgent") {
    val config = QSConfig(
      "ACCESS_KEY_ID",
      "SECRET_ACCESS_KEY",
      host = "httpbin.org",
      port = 80,
      protocol = "http"
    )
    val operation = Operation(config, "Test API", "GET", "/user-agent", Array.emptyIntArray)
    val respFuture = QSRequest(operation, TestInput()).send()
    val entity = Await.result(respFuture, Duration.Inf).entity
    val json = Await.result(Unmarshal(entity).to[Json], Duration.Inf)
    assert(json.asObject.get.toMap("user-agent") == QSConstants.UserAgent.asJson)
  }
}
