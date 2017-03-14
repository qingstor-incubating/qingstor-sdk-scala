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

  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var qingStor: QingStor = _
  private var listBucketsOutput: ListBucketsOutput = _

  When("^initialize QingStor service$", () => {
    config = TestUtil.getQSConfig
    testConfig = TestUtil.getTestConfig
    qingStor = QingStor(config)
  })

  Then("^the QingStor service is initialized$", () => {
    if (qingStor == null)
      throw new NullPointerException("QingStor service is not initialized")
  })

  When("^list buckets$", () => {
    config = TestUtil.getQSConfig
    testConfig = TestUtil.getTestConfig
    qingStor = QingStor(config)
    val input = ListBucketsInput(Option(testConfig.zone))
    listBucketsOutput = Await.result(qingStor.listBuckets(input), Duration.Inf)
  })

  Then("^list buckets status code is (\\d+)$", (status: Integer) => {
    assert(int2Integer(listBucketsOutput.statusCode.getOrElse(0)) == status)
  })

}
