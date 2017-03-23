package com.qingstor.sdk.steps

import java.io.{BufferedInputStream, File}
import java.net.URL

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.qingstor.sdk.model.QSModels
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.request.QSRequest
import com.qingstor.sdk.service.Object
import cucumber.api.java8.StepdefBody._
import cucumber.api.java8.En
import com.qingstor.sdk.steps.TestUtil.TestConfig

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ObjectSteps extends En {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  private def initBucket(): Unit = {
    ObjectSteps.config = TestUtil.getQSConfig
    ObjectSteps.testConfig = TestUtil.getTestConfig
    ObjectSteps.obj = Object(
      ObjectSteps.config,
      ObjectSteps.testConfig.bucket_name,
      ObjectSteps.testConfig.zone
    )
  }
  
  When("^put object with key \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      initBucket()
      TestUtil.createTmpFile("sdk_bin", 1)
      val file = new File("/tmp/sdk_bin")
      val input = Object.PutObjectInput(
        contentLength = file.length().toInt,
        body = file
      )
      val outputFuture = ObjectSteps.obj.putObject(arg, input)
      ObjectSteps.putObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^put object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = ObjectSteps.putObjectOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  When("^copy object with key \"(.*)\"", new A1[String] {
    override def accept(arg: String): Unit = {
      val copyKey = "%s_copy".format(arg)
      val input = Object.PutObjectInput(
        contentLength = 0,
        xQSCopySource = Some("/%s/%s".format(ObjectSteps.testConfig.bucket_name, arg))
      )
      val outputFuture = ObjectSteps.obj.putObject(copyKey, input)
      ObjectSteps.putObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^copy object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = ObjectSteps.putObjectOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  When("^move object with key \"(.*)\"", new A1[String] {
    override def accept(arg: String): Unit = {
      val copyKey = arg + "_copy"
      val moveKey = arg + "_move"
      val input = Object.PutObjectInput(
        contentLength = 0,
        xQSMoveSource = Some("/%s/%s".format(ObjectSteps.testConfig.bucket_name, copyKey))
      )
      val outputFuture = ObjectSteps.obj.putObject(moveKey, input)
      ObjectSteps.putObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^move object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = ObjectSteps.putObjectOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  When("^get object with key \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val input = Object.GetObjectInput()
      val outputFuture = ObjectSteps.obj.getObject(arg, input)
      ObjectSteps.getObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^get object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val status = ObjectSteps.getObjectOutput.statusCode.getOrElse(-1)
      assert(status == arg)
    }
  })

  And("^get object content length is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val length = ObjectSteps.getObjectOutput.body.length
      assert( length * 1024 == arg)
    }
  })

  When("^get object \"(.*)\" with content type \"(.*)\"", new A2[String, String] {
    override def accept(arg1: String, arg2: String): Unit = {
      val input = Object.GetObjectInput(responseContentType = Some(arg2))
      ObjectSteps.getObjectRequest = ObjectSteps.obj.getObjectRequest(arg1, input)
      val outputFuture = ObjectSteps.getObjectRequest.send()
      ObjectSteps.getObjectResponse = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^get object content type is \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val entity = ObjectSteps.getObjectResponse.getEntity
      val contentType = entity.contentType.toString()
      assert(arg.contains(contentType))
    }
  })

  When("^get object \"(.*)\" with query signature$", new A1[String] {
    override def accept(arg: String): Unit = {
      val input = Object.GetObjectInput()
      val request = ObjectSteps.obj.getObjectRequest(arg, input)
      val uri = QSRequest.signQueries(request, 100000)
      val conn = new URL(uri.toString()).openConnection()
      conn.setConnectTimeout(5000)
      conn.setReadTimeout(5000)
      ObjectSteps.getObjectWithQueryOutput = new BufferedInputStream(conn.getInputStream)
    }
  })

  Then("^get object with query signature content length is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val available = ObjectSteps.getObjectWithQueryOutput.available()
      assert(arg == available * 1024)
      ObjectSteps.getObjectWithQueryOutput.close()
    }
  })

  When("^head object with key \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val outputFuture = ObjectSteps.obj.headObject(arg, Object.HeadObjectInput())
      ObjectSteps.headObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^head object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(arg == ObjectSteps.headObjectOutput.statusCode.getOrElse(-1))
    }
  })

  When("^options object \"(.*)\" with method \"(.*)\" and origin \"(.*)\"$", 
    new A3[String, String, String] {
    override def accept(arg1: String, arg2: String, arg3: String): Unit = {
      val input = Object.OptionsObjectInput(
        accessControlRequestMethod = arg2,
        origin = arg3
      )
      val outputFuture = ObjectSteps.obj.optionsObject(arg1, input)
      ObjectSteps.optionsObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^options object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      val statusCode = ObjectSteps.optionsObjectOutput.statusCode.getOrElse(-1)
      assert(arg == statusCode)
    }
  })

  When("^delete object with key \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val outputFuture = ObjectSteps.obj.deleteObject(arg, Object.DeleteObjectInput())
      ObjectSteps.deleteObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^delete object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectSteps.deleteObjectOutput.statusCode.getOrElse(-1) == arg)
    }
  })

  When("^delete the move object with key \"(.*)\"$", new A1[String] {
    override def accept(arg: String): Unit = {
      val moveKey = arg + "_move"
      val outputFuture = ObjectSteps.obj.deleteObject(moveKey, Object.DeleteObjectInput())
      ObjectSteps.deleteObjectOutput = Await.result(outputFuture, Duration.Inf)
    }
  })

  Then("^delete the move object status code is (\\d+)$", new A1[Integer] {
    override def accept(arg: Integer): Unit = {
      assert(ObjectSteps.deleteObjectOutput.statusCode.getOrElse(-1) == arg)
    }
  })
}

object ObjectSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var obj: Object = _

  private var putObjectOutput: Object.PutObjectOutput = _
  private var getObjectRequest: QSRequest = _
  private var getObjectResponse: QSModels.QSHttpResponse = _
  private var getObjectOutput: Object.GetObjectOutput = _
  private var getObjectWithQueryOutput: BufferedInputStream = _
  private var headObjectOutput: Object.HeadObjectOutput = _
  private var optionsObjectOutput: Object.OptionsObjectOutput = _
  private var deleteObjectOutput: Object.DeleteObjectOutput = _
}