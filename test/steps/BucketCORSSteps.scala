package steps

import java.util.NoSuchElementException

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.Bucket
import com.qingstor.sdk.service.Types.CORSRuleModel
import cucumber.api.java8.En
import steps.TestUtil.TestConfig
import spray.json._
import com.qingstor.sdk.service.QSJsonProtocol.cORSRuleModelFormat

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

class BucketCORSSteps extends En{
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private def initBucket(): Unit = {
    BucketCORSSteps.config = TestUtil.getQSConfig
    BucketCORSSteps.testConfig = TestUtil.getTestConfig
    BucketCORSSteps.bucket = Bucket(
      BucketCORSSteps.config,
      BucketCORSSteps.testConfig.bucket_name,
      BucketCORSSteps.testConfig.zone
    )
  }

  When("^put bucket CORS:$", { arg: String =>
    initBucket()
    val json = arg.parseJson
    val rules = json.asJsObject.fields("cors_rules").asInstanceOf[JsArray].elements
                  .toList.map(_.convertTo[CORSRuleModel])

    val input = Bucket.PutBucketCORSInput(rules)
    val outputFuture = BucketCORSSteps.bucket.putBucketCORS(input)
    BucketCORSSteps.putBucketCORSOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^put bucket CORS status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(BucketCORSSteps.putBucketCORSOutput.statusCode.getOrElse(-1))
    assert(status == arg)
  })

  When("^get bucket CORS$", { () =>
    val input = Bucket.GetBucketCORSInput()
    val outputFuture = BucketCORSSteps.bucket.getBucketCORS(input)
    BucketCORSSteps.getBucketCORSOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^get bucket CORS status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(BucketCORSSteps.getBucketCORSOutput.statusCode.getOrElse(-1))
    assert(status == arg)
  })

  And("get bucket CORS should have allowed origin \"(.*)\"$", { arg: String =>
    val hasAO = BucketCORSSteps.getBucketCORSOutput.`cors_rules`
      .map(_.exists(_.`allowed_origin` == arg))
    if (hasAO.isEmpty || !hasAO.get)
      throw new NoSuchElementException("""Allowed origin: "%s" not found in bucket CORS""".format(arg))
  })

  When("^delete bucket CORS$", { () =>
    val input = Bucket.DeleteBucketCORSInput()
    val outputFuture = BucketCORSSteps.bucket.deleteBucketCORS(input)
    BucketCORSSteps.deleteBucketCORSOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^delete bucket CORS status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(BucketCORSSteps.deleteBucketCORSOutput.statusCode.getOrElse(-1))
    assert(status == arg)
  })
}

object BucketCORSSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var bucket: Bucket = _

  private var putBucketCORSOutput: Bucket.PutBucketCORSOutput = _
  private var getBucketCORSOutput: Bucket.GetBucketCORSOutput = _
  private var deleteBucketCORSOutput: Bucket.DeleteBucketCORSOutput = _
}
