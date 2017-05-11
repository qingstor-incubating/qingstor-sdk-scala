package com.qingstor.sdk.steps

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.Bucket
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig
import io.circe.Json
import io.circe.parser._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class BucketExternalMirrorSteps extends En{

  private def initBucket(): Unit = {
    BucketExternalMirrorSteps.config = TestUtil.getQSConfig
    BucketExternalMirrorSteps.testConfig = TestUtil.getTestConfig
    BucketExternalMirrorSteps.bucket = Bucket(
      BucketExternalMirrorSteps.config,
      BucketExternalMirrorSteps.testConfig.bucket_name,
      BucketExternalMirrorSteps.testConfig.zone
    )
  }

  When("^put bucket external mirror:$", new A1[String] {
    override def accept(arg: String): Unit = {
      initBucket()
      val json = parse(arg) match {
        case Left(_) => Json.Null
        case Right(j) => j
      }
      val sourceSite = json.asObject.get.toMap("source_site").asString.getOrElse("")
      val input = Bucket.PutBucketExternalMirrorInput(sourceSite)
      val outputFuture = BucketExternalMirrorSteps.bucket.putExternalMirror(input)
      BucketExternalMirrorSteps.putBucketExternalMirrorOutput = Await.result(outputFuture,
        Duration.Inf)
    }
  })

  Then("^put bucket external mirror status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketExternalMirrorSteps.putBucketExternalMirrorOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  When("^get bucket external mirror$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.GetBucketExternalMirrorInput()
      val outputFuture = BucketExternalMirrorSteps.bucket.getExternalMirror(input)
      BucketExternalMirrorSteps.getBucketExternalMirrorOutput = Await.result(outputFuture,
        Duration.Inf)
    }
  })

  Then("^get bucket external mirror status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketExternalMirrorSteps.getBucketExternalMirrorOutput
        .statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  And("^get bucket external mirror should have source_site \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val sourceSite = BucketExternalMirrorSteps.getBucketExternalMirrorOutput
        .sourceSite.getOrElse("")
      assert(sourceSite == arg)
    }
  })

  When("^delete bucket external mirror$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.DeleteBucketExternalMirrorInput()
      val outputFuture = BucketExternalMirrorSteps.bucket.deleteExternalMirror(input)
      BucketExternalMirrorSteps.deleteBucketExternalMirrorOutput = Await.result(outputFuture,
        Duration.Inf)
    }
  })

  Then("^delete bucket external mirror status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketExternalMirrorSteps.deleteBucketExternalMirrorOutput
        .statusCode.getOrElse(-1)
      assert(status == arg)
    }
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
