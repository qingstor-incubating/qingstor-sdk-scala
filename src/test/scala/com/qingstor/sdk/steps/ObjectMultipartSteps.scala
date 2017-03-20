package com.qingstor.sdk.steps

import java.io.File

import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.exception.QingStorException
import com.qingstor.sdk.service.Object
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class ObjectMultipartSteps extends En {

  private def initBucket(): Unit = {
    ObjectMultipartSteps.config = TestUtil.getQSConfig
    ObjectMultipartSteps.testConfig = TestUtil.getTestConfig
    ObjectMultipartSteps.obj = Object(
      ObjectMultipartSteps.config,
      ObjectMultipartSteps.testConfig.bucket_name,
      ObjectMultipartSteps.testConfig.zone
    )
  }

  When("^initiate multipart upload with key \"(.*)\"$", { arg: String =>
    initBucket()
    val input = Object.InitiateMultipartUploadInput()
    val outputFuture = ObjectMultipartSteps.obj.initiateMultipartUpload(arg, input)
    ObjectMultipartSteps.initiateMultipartUploadOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^initiate multipart upload status code is (\\d+)$", { arg: Integer =>
    assert(ObjectMultipartSteps.initiateMultipartUploadOutput.statusCode.getOrElse(-1) == arg)
  })

  When("""^upload the first part with key "(.*)"$""", { arg: String =>
    TestUtil.createTmpFile(name = "sdk_bin_part_0", count = 5, bs = 1048576)
    val file = new File("/tmp/sdk_bin_part_0")
    val input = Object.UploadMultipartInput(
      partNumber = 0,
      uploadID = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse(""),
      body = file
    )
    val of = ObjectMultipartSteps.obj.uploadMultipart(arg, input)
    ObjectMultipartSteps.uploadMultipartOutput = Await.result(of, Duration.Inf)
    file.delete()
  })

  Then("""^upload the first part status code is (\d+)$""", { arg: Integer =>
    assert(ObjectMultipartSteps.uploadMultipartOutput.statusCode.getOrElse(-1) == arg)
  })

  When("""^upload the second part with key "(.*)"$""", { arg: String =>
    TestUtil.createTmpFile(name = "sdk_bin_part_1", count = 4, bs = 1048576)
    val file = new File("/tmp/sdk_bin_part_1")
    val input = Object.UploadMultipartInput(
      partNumber = 1,
      uploadID = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse(""),
      body = file
    )
    val of = ObjectMultipartSteps.obj.uploadMultipart(arg, input)
    ObjectMultipartSteps.uploadMultipartOutput = Await.result(of, Duration.Inf)
    file.delete()
  })

  Then("""^upload the second part status code is (\d+)$""", { arg: Integer =>
    assert(ObjectMultipartSteps.uploadMultipartOutput.statusCode.getOrElse(-1) == arg)
  })

  When("""^upload the third part with key "(.*)"$""", { arg: String =>
    TestUtil.createTmpFile(name = "sdk_bin_part_2", count = 3, bs = 1048576)
    val file = new File("/tmp/sdk_bin_part_2")
    val input = Object.UploadMultipartInput(
      partNumber = 2,
      uploadID = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse(""),
      body = file
    )
    val of = ObjectMultipartSteps.obj.uploadMultipart(arg, input)
    ObjectMultipartSteps.uploadMultipartOutput = Await.result(of, Duration.Inf)
    file.delete()
  })

  Then("""^upload the third part status code is (\d+)$""", { arg: Integer =>
    assert(ObjectMultipartSteps.uploadMultipartOutput.statusCode.getOrElse(-1) == arg)
  })

  When("""^list multipart with key "(.*)"$""", { arg: String =>
    val id = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse("")
    val input = Object.ListMultipartInput(uploadID = id)
    val of = ObjectMultipartSteps.obj.listMultipart(arg, input)
    ObjectMultipartSteps.listMultipartOutput = Await.result(of, Duration.Inf)
  })

  Then("""^list multipart status code is (\d+)$""", { arg: Integer =>
    assert(ObjectMultipartSteps.listMultipartOutput.statusCode.getOrElse(-1) == arg)
  })

  And("""^list multipart object parts count is (\d+)$""", { arg: Integer =>
    val count = ObjectMultipartSteps.listMultipartOutput.`count`.getOrElse(-1)
    assert(arg == count)
  })

  When("""^complete multipart upload with key "(.*)"$""", { arg: String =>
    val id = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse("")
    val input = Object.CompleteMultipartUploadInput(
      uploadID = id,
      objectParts = ObjectMultipartSteps.listMultipartOutput.`object_parts`
    )
    val of = ObjectMultipartSteps.obj.completeMultipartUpload(arg, input)
    ObjectMultipartSteps.completeMultipartUploadOutput = Await.result(of, Duration.Inf)
  })

  Then("""^complete multipart upload status code is (\d+)$""", { arg: Integer =>
    assert(ObjectMultipartSteps.completeMultipartUploadOutput.statusCode.getOrElse(-1) == arg)
  })

  When("""^abort multipart upload with key "(.*)"$""", { arg: String =>
    val id = ObjectMultipartSteps.initiateMultipartUploadOutput.`upload_id`.getOrElse("")
    val input = Object.AbortMultipartUploadInput(id)
    ObjectMultipartSteps.abortMultipartUploadOutputFuture =
      ObjectMultipartSteps.obj.abortMultipartUpload(arg, input)
  })

  Then("""^abort multipart upload status code is (\d+)$""", { arg: Integer =>
    try {
      Await.result(ObjectMultipartSteps.abortMultipartUploadOutputFuture, Duration.Inf)
    } catch {
      case ex: QingStorException =>
        val error = ex.errorMessage
        assert(error.statusCode.getOrElse(-1) == arg)
    }
  })

  When("""^delete the multipart object with key "(.*)"$""", { arg: String =>
    val input = Object.DeleteObjectInput()
    val of = ObjectMultipartSteps.obj.deleteObject(arg, input)
    ObjectMultipartSteps.deleteObjectOutput = Await.result(of, Duration.Inf)
  })

  Then("""^delete the multipart object status code is (\d+)$""", { arg: Integer =>
    assert(ObjectMultipartSteps.deleteObjectOutput.statusCode.getOrElse(-1) == arg)
  })
}

object ObjectMultipartSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var obj: Object = _

  private var initiateMultipartUploadOutput: Object.InitiateMultipartUploadOutput = _
  private var uploadMultipartOutput: Object.UploadMultipartOutput = _
  private var listMultipartOutput: Object.ListMultipartOutput = _
  private var completeMultipartUploadOutput: Object.CompleteMultipartUploadOutput = _
  private var abortMultipartUploadOutputFuture: Future[Object.AbortMultipartUploadOutput] = _
  private var deleteObjectOutput: Object.DeleteObjectOutput = _
}