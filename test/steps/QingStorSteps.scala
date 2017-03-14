package steps

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import cucumber.api.java8.En
import com.qingstor.sdk.service.QingStor
import com.qingstor.sdk.service.QingStor._
import TestUtil.TestConfig
import cucumber.runtime.java.picocontainer.PicoFactory

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

class QingStorTest(dep: QingStorDependency) extends En {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  private val qingStorDependency = dep

//  private var config: QSConfig = _
//  private var testConfig: TestConfig = _
//  private var qingStor: QingStor = _
//  private var listBucketsOutput: ListBucketsOutput = _

  Given("^need to use QingStor service$", () => {
    qingStorDependency.config = TestUtil.getQSConfig
    qingStorDependency.testConfig = TestUtil.getTestConfig
    assert(qingStorDependency.config != null)
    assert(qingStorDependency.testConfig != null)
  })

  When("^initialize QingStor service$", () => {
    qingStorDependency.qingStor = QingStor(qingStorDependency.config)
  })

  Then("^the QingStor service is initialized$", () => {
    assert(qingStorDependency.qingStor != null)
  })

  When("^list buckets$", () => {
    val input = ListBucketsInput(Option(qingStorDependency.testConfig.zone))
    qingStorDependency.listBucketsOutput = Await.result(qingStorDependency.qingStor.listBuckets(input), Duration.Inf)
    assert(qingStorDependency.listBucketsOutput != null)
  })

  Then("^list buckets status code is (\\d+)$", (status: Int) => {
    assert(qingStorDependency.listBucketsOutput.statusCode.getOrElse(0) == status)
  })

}
