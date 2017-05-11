package com.qingstor.sdk.steps

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.service.Bucket
import com.qingstor.sdk.service.Bucket.DeleteMultipleObjectsInput
import com.qingstor.sdk.service.Types.KeyModel
import com.qingstor.sdk.service.QSCodec.QSTypesCodec.decodeKeyModel
import com.qingstor.sdk.util.QSServiceUtil
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig
import io.circe._
import io.circe.parser._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class BucketSteps extends En {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  // Scenario: need to use bucket
  When("^initialize the bucket$", new A0 {
    override def accept(): Unit = {
      BucketSteps.config = TestUtil.getQSConfig
      BucketSteps.testConfig = TestUtil.getTestConfig
      BucketSteps.bucket = Bucket(BucketSteps.config, BucketSteps.newBucketName, BucketSteps.testConfig.zone)
    }
  })

  Then("^the bucket is initialized$", new A0 {
    override def accept(): Unit = {
      if (BucketSteps.bucket == null) {
        throw new NullPointerException("Bucket is not initialized")
      }
    }
  })

  // Scenario: create the bucket
  When("^put bucket$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.PutBucketInput()
      val outputFuture = BucketSteps.bucket.put(input)
      BucketSteps.putBucketOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^put bucket status code is (\\d+)$", new A1[Integer] {
    override def accept(status: Integer): Unit = {
      val outputStatus = BucketSteps.putBucketOutput.statusCode.getOrElse(0)
      assert(outputStatus == status)
    }
  })

  // Scenario: create the same bucket again
  When("^put same bucket again$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.PutBucketInput()
      val outputFuture = BucketSteps.bucket.putRequest(input).send()
      BucketSteps.putBucketAgainResp = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^put same bucket again status code is (\\d+)$", new A1[Integer] {
    override def accept(status: Integer): Unit = {
      val outputStatus = BucketSteps.putBucketAgainResp.status.intValue()
      assert(outputStatus == status)
    }
  })

  // Scenario: list objects in the bucket
  When("^list objects$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.ListObjectsInput()
      val outputFuture = BucketSteps.bucket.listObjects(input)
      BucketSteps.listObjectsOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^list objects status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketSteps.listObjectsOutput.statusCode.getOrElse(0)
      assert(status == arg)
    }
  })

  And("^list objects keys count is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val keys = BucketSteps.listObjectsOutput.`keys`
      val count = int2Integer(keys.map(_.length).getOrElse(-1))
      assert(count == arg)
    }
  })

  // Scenario: head the bucket
  When("^head bucket$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.HeadBucketInput()
      val outputFuture = BucketSteps.bucket.head(input)
      BucketSteps.headBucketOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^head bucket status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketSteps.headBucketOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  // Scenario: delete multiple objects in the bucket
  When("^delete multiple objects:$", new A1[String] {
    override def accept(args: String): Unit = {
      val rawJson: String = args
      val json = parse(rawJson) match {
        case Left(_) => Json.Null
        case Right(j) => j
      }
      val jsonObject = json.asObject.get
      val quiet = jsonObject.toMap("quiet").asBoolean.getOrElse(false)
      val keys = jsonObject.toMap("objects").asArray.getOrElse(Vector.empty[Json]).toList.map(_.as[KeyModel] match {
        case Left(failure) => throw failure
        case Right(keyModel) => keyModel
      })
      val contentMD5 = QSServiceUtil.calMD5(Option(quiet), keys)
      val input = DeleteMultipleObjectsInput(
        contentMD5 = contentMD5,
        quiet = Option(quiet),
        objects = keys
      )
      val outputFuture = BucketSteps.bucket.deleteMultipleObjects(input)
      BucketSteps.deleteMultipleObjectsOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^delete multiple objects code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketSteps.deleteMultipleObjectsOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  // Scenario: get statistics of the bucket
  When("^get bucket statistics$", new A0 {
    override def accept(): Unit = {
      val input = Bucket.GetBucketStatisticsInput()
      val outputFuture = BucketSteps.bucket.getStatistics(input)
      BucketSteps.getBucketStatisticsOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^get bucket statistics status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = BucketSteps.getBucketStatisticsOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  And("^get bucket statistics status is \"(.*)\"$", new A1[String] {
    override def accept(args: String): Unit = {
      val status = BucketSteps.getBucketStatisticsOutput.`status`.getOrElse("")
      assert(status == args)
    }
  })

  // Scenario: delete the bucket
  When("^delete bucket$", new A0 {
    override def accept(): Unit = {
//      val input = Bucket.DeleteBucketInput()
//      val outputFuture = BucketSteps.bucket.deleteBucket(input)
//      BucketSteps.deleteBucketOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^delete bucket status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
//      val status = BucketSteps.deleteBucketOutput.statusCode.getOrElse(-1)
//      assert(status == arg)
    }
  })

  // Scenario: list multipart uploads
  Given("""^an object created by initiate multipart upload$""", new A0 {
    override def accept(): Unit = {
      val input = Bucket.InitiateMultipartUploadInput()
      val of = BucketSteps.bucket.initiateMultipartUpload(BucketSteps.objectKey, input)
      BucketSteps.initiateMultipartUploadOutput = Await.result(of, Duration.Inf)
    }
  })

  When("""^list multipart uploads$""", new A0 {
    override def accept(): Unit = {
      val input = Bucket.ListMultipartUploadsInput()
      val of = BucketSteps.bucket.listMultipartUploads(input)
      BucketSteps.listMultipartUploadsOutput = Await.result(of, Duration.Inf)
    }
  })

  Then("""^list multipart uploads count is (\d+)""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(BucketSteps.listMultipartUploadsOutput.`uploads`.getOrElse(List.empty).length == arg)
    }
  })

  When("""^list multipart uploads with prefix$""", new A0 {
    override def accept(): Unit = {
      val input = Bucket.ListMultipartUploadsInput(prefix = Some(BucketSteps.prefix))
      val of = BucketSteps.bucket.listMultipartUploads(input)
      BucketSteps.listMultipartUploadsOutput = Await.result(of, Duration.Inf)
    }
  })

  Then("""^list multipart uploads with prefix count is (\d+)""", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val input = Bucket.AbortMultipartUploadInput(
        BucketSteps.initiateMultipartUploadOutput.uploadID.getOrElse(""))
      val of = BucketSteps.bucket.abortMultipartUpload(BucketSteps.objectKey, input)
      Await.result(of, Duration.Inf)
      assert(BucketSteps.listMultipartUploadsOutput.`uploads`.getOrElse(List.empty).length == arg)
    }
  })
}

object BucketSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var bucket: Bucket = _
  private val newBucketName: String = "test" + System.currentTimeMillis()
  private val prefix: String = "list"

  private var putBucketOutput: Bucket.PutBucketOutput = _
  private var putBucketAgainResp: HttpResponse = _
  private var listObjectsOutput: Bucket.ListObjectsOutput = _
  private var headBucketOutput: Bucket.HeadBucketOutput = _
//  private var deleteBucketOutput: Bucket.DeleteBucketOutput = _
  private var getBucketStatisticsOutput: Bucket.GetBucketStatisticsOutput = _
  private var deleteMultipleObjectsOutput: Bucket.DeleteMultipleObjectsOutput = _

  private val objectKey = "list_multipart_uploads_object_key"
  private var initiateMultipartUploadOutput: Bucket.InitiateMultipartUploadOutput = _
  private var listMultipartUploadsOutput: Bucket.ListMultipartUploadsOutput = _
}
