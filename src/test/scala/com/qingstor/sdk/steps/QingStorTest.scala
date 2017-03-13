package com.qingstor.sdk.steps

import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.QingStor
import com.qingstor.sdk.service.QingStor.{ListBucketsInput, ListBucketsOutput}
import cucumber.api.scala.{EN, ScalaDsl}
import akka.actor.ActorSystem
import TestUtil.TestConfig
import org.scalatest.Matchers

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class QingStorTest extends ScalaDsl with EN with Matchers {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()
  private var testConfig: TestConfig = _
  private var config: QSConfig = _
  private var qingStor: QingStor = _
  private var listBucketsOutput: ListBucketsOutput = _

  Given("^need to use QingStor service$") { () =>
    config = TestUtil.getQSConfig
    testConfig = TestUtil.getTestConfig
    println("init: initConfig")
  }

  When("^initialize QingStor service$") { () =>
    qingStor = QingStor(config)
    println("init: initService")
  }

  Then("^the QingStor service is initialized$") { () =>
    assert(qingStor != null)
  }

  When("^list buckets$") { () =>
    val input = ListBucketsInput(location = Option(testConfig.zone))
    val futureOut = qingStor.listBuckets(input)
    listBucketsOutput = Await.result(futureOut, Duration.Inf)
  }

  Then("^list buckets status code is (\\d+)$") { (arg0: Int) =>
    assert(listBucketsOutput != null)
    assert(listBucketsOutput.statusCode.nonEmpty)
    listBucketsOutput should not be null
    listBucketsOutput.statusCode.getOrElse(0) should be (arg0)
  }
}
