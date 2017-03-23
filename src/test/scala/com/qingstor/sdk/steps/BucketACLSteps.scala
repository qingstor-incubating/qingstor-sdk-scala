package com.qingstor.sdk.steps

import java.util.NoSuchElementException

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.Bucket
import com.qingstor.sdk.service.Types.ACLModel
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig
import spray.json._
import com.qingstor.sdk.service.QSJsonProtocol.aCLModelFormat

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
      val json = arg.parseJson
      val modelList = json.asJsObject.fields("acl").asInstanceOf[JsArray]
        .elements.toList.map(_.convertTo[ACLModel])
      val input = Bucket.PutBucketACLInput(modelList)
      val outputFuture = BucketACLSteps.bucket.putBucketACL(input)
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
      val outputFuture = BucketACLSteps.bucket.getBucketACL(input)
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
      val hasThisName = BucketACLSteps.getBucketACLOutput.`acl`.map{ modleList =>
        modleList.exists { model =>
          model.`grantee`.`name`.nonEmpty && model.`grantee`.`name`.get.equals(arg)
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
