package steps

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.Bucket
import cucumber.api.java8.En
import steps.TestUtil.TestConfig
import spray.json._

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

class BucketExternalMirrorSteps extends En{
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private def initBucket(): Unit = {
    BucketExternalMirrorSteps.config = TestUtil.getQSConfig
    BucketExternalMirrorSteps.testConfig = TestUtil.getTestConfig
    BucketExternalMirrorSteps.bucket = Bucket(
      BucketExternalMirrorSteps.config,
      BucketExternalMirrorSteps.testConfig.bucket_name,
      BucketExternalMirrorSteps.testConfig.zone
    )
  }

  When("^put bucket external mirror:$", { arg: String =>
    initBucket()
    val json = arg.parseJson
    val sourceSite = json.asJsObject.fields("source_site").asInstanceOf[JsString].value
    val input = Bucket.PutBucketExternalMirrorInput(sourceSite)
    val outputFuture = BucketExternalMirrorSteps.bucket.putBucketExternalMirror(input)
    BucketExternalMirrorSteps.putBucketExternalMirrorOutput = Await.result(outputFuture,
      Duration.Inf)
  })

  Then("^put bucket external mirror status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(BucketExternalMirrorSteps.putBucketExternalMirrorOutput
      .statusCode.getOrElse(-1))
    assert(status == arg)
  })

  When("^get bucket external mirror$", { () =>
    val input = Bucket.GetBucketExternalMirrorInput()
    val outputFuture = BucketExternalMirrorSteps.bucket.getBucketExternalMirror(input)
    BucketExternalMirrorSteps.getBucketExternalMirrorOutput = Await.result(outputFuture,
      Duration.Inf)
  })

  Then("^get bucket external mirror status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(BucketExternalMirrorSteps.getBucketExternalMirrorOutput
      .statusCode.getOrElse(-1))
    assert(status == arg)
  })

  And("^get bucket external mirror should have source_site \"(.*)\"$", { arg: String =>
    val sourceSite = BucketExternalMirrorSteps.getBucketExternalMirrorOutput
      .`source_site`.getOrElse("")
    assert(sourceSite == arg)
  })

  When("^delete bucket external mirror$", { () =>
    val input = Bucket.DeleteBucketExternalMirrorInput()
    val outputFuture = BucketExternalMirrorSteps.bucket.deleteBucketExternalMirror(input)
    BucketExternalMirrorSteps.deleteBucketExternalMirrorOutput = Await.result(outputFuture,
      Duration.Inf)
  })

  Then("^delete bucket external mirror status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(BucketExternalMirrorSteps.deleteBucketExternalMirrorOutput
      .statusCode.getOrElse(-1))
    assert(status == arg)
  })
}

object BucketExternalMirrorSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var bucket: Bucket = _

  private var putBucketExternalMirrorOutput: Bucket.PutBucketExternalMirrorOutput = _
  private var getBucketExternalMirrorOutput: Bucket.GetBucketExternalMirrorOutput = _
  private var deleteBucketExternalMirrorOutput: Bucket.DeleteBucketExternalMirrorOutput = _
}
