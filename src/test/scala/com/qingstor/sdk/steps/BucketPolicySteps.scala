package com.qingstor.sdk.steps

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.Bucket
import com.qingstor.sdk.service.Types.StatementModel
import com.qingstor.sdk.service.QSCodec.QSTypesCodec.decodeStatementModel
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import io.circe.parser._

class BucketPolicySteps extends En {

  private def initBucket(): Unit = {
    BucketPolicySteps.config = TestUtil.getQSConfig
    BucketPolicySteps.testConfig = TestUtil.getTestConfig
    BucketPolicySteps.bucket = Bucket(
      BucketPolicySteps.config,
      BucketPolicySteps.testConfig.bucket_name,
      BucketPolicySteps.testConfig.zone
    )
  }

  When("^put bucket policy:$", new A1[String] {
    override def accept(arg: String): Unit = {
      initBucket()
      val map = parse(arg).right.flatMap(_.as[Map[String, List[StatementModel]]]) match {
        case Left(failure) => throw failure
        case Right(m) => m
      }
      val tmp = map("statement")
      var statements: List[StatementModel] = null
      if (tmp.length == 1) {
        statements = List(StatementModel(
          action = tmp.head.action,
          effect = tmp.head.effect,
          iD = tmp.head.iD,
          condition = tmp.head.condition,
          user = tmp.head.user,
          resource = Some(List(s"${BucketPolicySteps.bucket.bucketName}/*"))
        ))
      }
      val input = Bucket.PutBucketPolicyInput(statements)
      val outputFuture = BucketPolicySteps.bucket.putPolicy(input)
      BucketPolicySteps.putBucketPolicyOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^put bucket policy status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketPolicySteps.putBucketPolicyOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  When("^get bucket policy$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.GetBucketPolicyInput()
      val outputFuture = BucketPolicySteps.bucket.getPolicy(input)
      BucketPolicySteps.getBucketPolicyOutput = Await.result(outputFuture,
        Duration.Inf)
    }
  })

  Then("^get bucket policy status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketPolicySteps.getBucketPolicyOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  And("^get bucket policy should have Referer \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val hasReferer = BucketPolicySteps.getBucketPolicyOutput.`statement`.map{ statements =>
        statements.exists{ statement =>
          statement.condition.get.stringLike.get.referer.get.contains(arg)
        }
      }
      if (!hasReferer.get)
        throw new NoSuchElementException(
          """Referer: "%s" can not found in bucket Referer""".format(arg))
    }
  })

  When("^delete bucket policy$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.DeleteBucketPolicyInput()
      val outputFuture = BucketPolicySteps.bucket.deletePolicy(input)
      BucketPolicySteps.deleteBucketPolicyOutput = Await.result(outputFuture,
        Duration.Inf)
    }
  })

  Then("^delete bucket policy status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketPolicySteps.deleteBucketPolicyOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })
}

object BucketPolicySteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var bucket: Bucket = _

  private var putBucketPolicyOutput: Bucket.PutBucketPolicyOutput = _
  private var getBucketPolicyOutput: Bucket.GetBucketPolicyOutput = _
  private var deleteBucketPolicyOutput: Bucket.DeleteBucketPolicyOutput = _
}