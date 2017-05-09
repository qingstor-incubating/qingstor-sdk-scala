package com.qingstor.sdk.steps

import java.io.File

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.exception.QingStorException
import com.qingstor.sdk.service.Bucket
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class ObjectMultipartSteps extends En {

  private def initBucket(): Unit = {
    ObjectMultipartSteps.config = TestUtil.getQSConfig
    ObjectMultipartSteps.testConfig = TestUtil.getTestConfig
    ObjectMultipartSteps.bucket = Bucket(
      ObjectMultipartSteps.config,
      ObjectMultipartSteps.testConfig.bucket_name,
      ObjectMultipartSteps.testConfig.zone
    )
  }

  When("^initiate multipart upload with key \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      initBucket()
      val input = Bucket.InitiateMultipartUploadInput()
      val outputFuture = ObjectMultipartSteps.bucket.initiateMultipartUpload(arg, input)
      ObjectMultipartSteps.initiateMultipartUploadOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^initiate multipart upload status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectMultipartSteps.initiateMultipartUploadOutput.statusCode.getOrElse(-1) == arg)
    }
  })

  When("""^upload the first part with key "(.*)"$""", new A1[String] {
    override def accept(arg: String): Unit = {
      TestUtil.createTmpFile(name = "sdk_bin_part_0", count = 5, bs = 1048576)
      val file = new File("/tmp/sdk_bin_part_0")
      val input = Bucket.UploadMultipartInput(
        partNumber = 0,
        uploadID = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse(""),
        body = file
      )
      val of = ObjectMultipartSteps.bucket.uploadMultipart(arg, input)
      ObjectMultipartSteps.uploadMultipartOutput = Await.result(of, Duration.Inf)
      file.delete()
    }
  })

  Then("""^upload the first part status code is (\d+)$""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectMultipartSteps.uploadMultipartOutput.statusCode.getOrElse(-1) == arg)
    }
  })

  When("""^upload the second part with key "(.*)"$""", new A1[String] {
    override def accept(arg: String): Unit = {
      TestUtil.createTmpFile(name = "sdk_bin_part_1", count = 4, bs = 1048576)
      val file = new File("/tmp/sdk_bin_part_1")
      val input = Bucket.UploadMultipartInput(
        partNumber = 1,
        uploadID = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse(""),
        body = file
      )
      val of = ObjectMultipartSteps.bucket.uploadMultipart(arg, input)
      ObjectMultipartSteps.uploadMultipartOutput = Await.result(of, Duration.Inf)
      file.delete()
    }
  })

  Then("""^upload the second part status code is (\d+)$""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectMultipartSteps.uploadMultipartOutput.statusCode.getOrElse(-1) == arg)
    }
  })

  When("""^upload the third part with key "(.*)"$""", new A1[String] {
    override def accept(arg: String): Unit = {
      TestUtil.createTmpFile(name = "sdk_bin_part_2", count = 3, bs = 1048576)
      val file = new File("/tmp/sdk_bin_part_2")
      val input = Bucket.UploadMultipartInput(
        partNumber = 2,
        uploadID = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse(""),
        body = file
      )
      val of = ObjectMultipartSteps.bucket.uploadMultipart(arg, input)
      ObjectMultipartSteps.uploadMultipartOutput = Await.result(of, Duration.Inf)
      file.delete()
    }
  })

  Then("""^upload the third part status code is (\d+)$""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectMultipartSteps.uploadMultipartOutput.statusCode.getOrElse(-1) == arg)
    }
  })

  When("""^list multipart with key "(.*)"$""", new A1[String] {
    override def accept(arg: String): Unit = {
      val id = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse("")
      val input = Bucket.ListMultipartInput(uploadID = id)
      val of = ObjectMultipartSteps.bucket.listMultipart(arg, input)
      ObjectMultipartSteps.listMultipartOutput = Await.result(of, Duration.Inf)
    }
  })

  Then("""^list multipart status code is (\d+)$""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectMultipartSteps.listMultipartOutput.statusCode.getOrElse(-1) == arg)
    }
  })

  And("""^list multipart object parts count is (\d+)$""", new A1[Integer]{
    override def accept(arg: Integer): Unit = {
      val count = ObjectMultipartSteps.listMultipartOutput.`count`.getOrElse(-1)
      assert(arg == count)
    }
  })

  When("""^complete multipart upload with key "(.*)"$""", new A1[String] {
    override def accept(arg: String): Unit = {
      val id = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse("")
      val input = Bucket.CompleteMultipartUploadInput(
        uploadID = id,
        objectParts = ObjectMultipartSteps.listMultipartOutput.`object_parts`
      )
      val of = ObjectMultipartSteps.bucket.completeMultipartUpload(arg, input)
      ObjectMultipartSteps.completeMultipartUploadOutput = Await.result(of, Duration.Inf)
    }
  })

  Then("""^complete multipart upload status code is (\d+)$""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectMultipartSteps.completeMultipartUploadOutput.statusCode.getOrElse(-1) == arg)
    }
  })

  When("""^abort multipart upload with key "(.*)"$""", new A1[String] {
    override def accept(arg: String): Unit = {
      val id = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse("")
      val input = Bucket.AbortMultipartUploadInput(id)
      ObjectMultipartSteps.abortMultipartUploadOutputFuture =
        ObjectMultipartSteps.bucket.abortMultipartUpload(arg, input)
    }
  })

  Then("""^abort multipart upload status code is (\d+)$""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      try {
        Await.result(ObjectMultipartSteps.abortMultipartUploadOutputFuture, Duration.Inf)
      } catch {
        case ex: QingStorException =>
          val error = ex.errorMessage
          assert(error.statusCode.getOrElse(-1) == arg)
      }
    }
  })

  When("""^delete the multipart object with key "(.*)"$""", new A1[String] {
    override def accept(arg: String): Unit = {
      val input = Bucket.DeleteObjectInput()
      val of = ObjectMultipartSteps.bucket.deleteObject(arg, input)
      ObjectMultipartSteps.deleteObjectOutput = Await.result(of, Duration.Inf)
    }
  })

  Then("""^delete the multipart object status code is (\d+)$""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectMultipartSteps.deleteObjectOutput.statusCode.getOrElse(-1) == arg)
    }
  })
}

object ObjectMultipartSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var bucket: Bucket = _

  private var initiateMultipartUploadOutput: Bucket.InitiateMultipartUploadOutput = _
  private var uploadMultipartOutput: Bucket.UploadMultipartOutput = _
  private var listMultipartOutput: Bucket.ListMultipartOutput = _
  private var completeMultipartUploadOutput: Bucket.CompleteMultipartUploadOutput = _
  private var abortMultipartUploadOutputFuture: Future[Bucket.AbortMultipartUploadOutput] = _
  private var deleteObjectOutput: Bucket.DeleteObjectOutput = _
}