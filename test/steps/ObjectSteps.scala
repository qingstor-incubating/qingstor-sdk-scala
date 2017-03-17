package steps

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentType, ContentTypes}
import akka.stream.ActorMaterializer
import com.qingstor.sdk.config.QSConfig
import com.qingstor.sdk.exception.QingStorException
import com.qingstor.sdk.request.QSRequest
import com.qingstor.sdk.service.Bucket
import com.qingstor.sdk.service.Object
import com.qingstor.sdk.util.QSRequestUtil
import cucumber.api.java8.En
import steps.TestUtil.TestConfig
import spray.json._

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

class ObjectSteps extends En {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private def initBucket(): Unit = {
    ObjectSteps.config = TestUtil.getQSConfig
    ObjectSteps.testConfig = TestUtil.getTestConfig
    ObjectSteps.obj = Object(
      ObjectSteps.config,
      ObjectSteps.testConfig.bucket_name,
      ObjectSteps.testConfig.zone
    )
  }
  
  When("^put object with key \"(.*)\"$", { arg: String =>
    initBucket()
    TestUtil.createTmpFile("sdk_bin", 1)
    val file = new File("/tmp/sdk_bin")
    val input = Object.PutObjectInput(
      contentLength = file.length().toInt,
      body = file
    )
    ObjectSteps.objectKey = arg
    val outputFuture = ObjectSteps.obj.putObject(arg, input)
    ObjectSteps.putObjectOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^put object status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(ObjectSteps.putObjectOutput.statusCode.getOrElse(-1))
    assert(status == arg)
  })

  When("^copy object with key \"(.*)\"", { arg: String =>
    val input = Object.PutObjectInput(
      contentLength = 0,
      xQSCopySource = Some("/%s/%s".format(ObjectSteps.testConfig.bucket_name,
        ObjectSteps.objectKey))
    )
    val outputFuture = ObjectSteps.obj.putObject(arg, input)
    ObjectSteps.putObjectOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^copy object status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(ObjectSteps.putObjectOutput.statusCode.getOrElse(-1))
    assert(status == arg)
  })

  When("^move object with key \"(.*)\"", { arg: String =>
    val copyKey = ObjectSteps.objectKey + "_copy"
    val input = Object.PutObjectInput(
      contentLength = 0,
      xQSMoveSource = Some("/%s/%s".format(ObjectSteps.testConfig.bucket_name, copyKey))
    )
    val outputFuture = ObjectSteps.obj.putObject(arg, input)
    ObjectSteps.putObjectOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^move object status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(ObjectSteps.putObjectOutput.statusCode.getOrElse(-1))
    assert(status == arg)
  })

  When("^get object$", { () =>
    val input = Object.GetObjectInput()
    val outputFuture = ObjectSteps.obj.getObject(ObjectSteps.objectKey, input)
    ObjectSteps.getObjectOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^get object status code is (\\d+)$", { arg: Integer =>
    val status = int2Integer(ObjectSteps.putObjectOutput.statusCode.getOrElse(-1))
    assert(status == arg)
  })

  And("^get object content length is (\\d+)$", { arg: Integer =>
    val length = ObjectSteps.getObjectOutput.body.length
    assert( length * 1024 == arg)
  })

  When("^get object with content type \"(.*)\"", { arg: String =>
    val input = Object.GetObjectInput(responseContentType = Some(arg))
    val outputFuture = ObjectSteps.obj.getObject(ObjectSteps.objectKey, input)
    ObjectSteps.getObjectOutput = Await.result(outputFuture, Duration.Inf)
  })

  Then("^get object status code is \"(.*)\"$", { arg: String =>
    val contentType = ContentType.parse(arg).getOrElse(ContentTypes.NoContentType).toString()
    assert(contentType == arg)
  })
}

object ObjectSteps {
  private var config: QSConfig = _
  private var testConfig: TestConfig = _
  private var obj: Object = _
  private var objectKey: String = _

  private var putObjectOutput: Object.PutObjectOutput = _
  private var getObjectOutput: Object.GetObjectOutput = _
}