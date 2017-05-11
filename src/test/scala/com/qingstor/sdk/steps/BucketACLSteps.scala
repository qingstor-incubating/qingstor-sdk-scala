package com.qingstor.sdk.steps

import java.util.NoSuchElementException

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.Bucket
import com.qingstor.sdk.service.Types.ACLModel
import com.qingstor.sdk.service.QSCodec.QSTypesCodec.decodeACLModel
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig
import io.circe.parser._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class BucketACLSteps extends En{

  private def initBucket(): Unit = {
    BucketACLSteps.config = TestUtil.getQSConfig
    BucketACLSteps.testConfig = TestUtil.getTestConfig
    BucketACLSteps.bucket = Bucket(
      BucketACLSteps.config,
      BucketACLSteps.testConfig.bucket_name,
      BucketACLSteps.testConfig.zone
    )
  }

  When("^put bucket ACL:$", new A1[String] {
    override def accept(arg: String): Unit = {
      initBucket()
      val maps = parse(arg).right.flatMap(_.as[Map[String, List[ACLModel]]]) match {
        case Left(failure) => throw failure
        case Right(m) => m
      }
      val input = Bucket.PutBucketACLInput(maps("acl"))
      val outputFuture = BucketACLSteps.bucket.putACL(input)
      BucketACLSteps.putBucketACLOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^put bucket ACL status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketACLSteps.putBucketACLOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  When("^get bucket ACL$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.GetBucketACLInput()
      val outputFuture = BucketACLSteps.bucket.getACL(input)
      BucketACLSteps.getBucketACLOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^get bucket ACL status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketACLSteps.getBucketACLOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  And("^get bucket ACL should have grantee name \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val hasThisName = BucketACLSteps.getBucketACLOutput.aCL.map{ modleList =>
        modleList.exists { model =>
          model.grantee.name.nonEmpty && model.grantee.name.get.equals(arg)
        }
      }
      if (hasThisName.isEmpty || !hasThisName.get)
        throw new NoSuchElementException("""Grantee name: "%s" not found in bucket ACLs""".format(arg))
    }
  })
}

object BucketACLSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var bucket: Bucket = _

  private var putBucketACLOutput: Bucket.PutBucketACLOutput = _
  private var getBucketACLOutput: Bucket.GetBucketACLOutput = _
}
