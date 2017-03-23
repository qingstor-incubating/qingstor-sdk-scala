package com.qingstor.sdk.steps

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.QingStor
import com.qingstor.sdk.service.QingStor._
import com.qingstor.sdk.steps.TestUtil.TestConfig
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class QingStorSteps extends En {

  When("^initialize QingStor service$", new A0 {
    override def accept(): Unit = {
      QingStorSteps.config = TestUtil.getQSConfig
      QingStorSteps.testConfig = TestUtil.getTestConfig
      QingStorSteps.qingStor = QingStor(QingStorSteps.config)
    }
  })

  Then("^the QingStor service is initialized$", new A0 {
    override def accept(): Unit = {
      if (QingStorSteps.qingStor == null)
        throw new NullPointerException("QingStor service is not initialized")
    }
  })

  When("^list buckets$", new A0 {
    override def accept(): Unit = {
      val input = ListBucketsInput(Option(QingStorSteps.testConfig.zone))
      QingStorSteps.listBucketsOutput = Await.result(QingStorSteps.qingStor.listBuckets(input), Duration.Inf)
    }
  })

  Then("^list buckets status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(QingStorSteps.listBucketsOutput.statusCode.getOrElse(-1) == arg)
    }
  })
}

object QingStorSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var qingStor: QingStor = _
  private var listBucketsOutput: ListBucketsOutput = _
}
