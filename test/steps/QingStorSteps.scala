package steps

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import cucumber.api.java8.En
import com.qingstor.sdk.service.QingStor
import com.qingstor.sdk.service.QingStor._
import TestUtil.TestConfig

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

class QingStorSteps extends En {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  When("^initialize QingStor service$", () => {
    QingStorSteps.config = TestUtil.getQSConfig
    QingStorSteps.testConfig = TestUtil.getTestConfig
    QingStorSteps.qingStor = QingStor(QingStorSteps.config)
  })

  Then("^the QingStor service is initialized$", () => {
    if (QingStorSteps.qingStor == null)
      throw new NullPointerException("QingStor service is not initialized")
  })

  When("^list buckets$", () => {
    val input = ListBucketsInput(Option(QingStorSteps.testConfig.zone))
    QingStorSteps.listBucketsOutput = Await.result(QingStorSteps.qingStor.listBuckets(input), Duration.Inf)
  })

  Then("^list buckets status code is (\\d+)$", (status: Integer) => {
    assert(int2Integer(QingStorSteps.listBucketsOutput.statusCode.getOrElse(0)) == status)
  })
}

object QingStorSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var qingStor: QingStor = _
  private var listBucketsOutput: ListBucketsOutput = _
}
