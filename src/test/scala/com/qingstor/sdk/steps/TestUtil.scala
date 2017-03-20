package com.qingstor.sdk.steps

import java.io.FileInputStream

import com.qingstor.sdk.config.QSConfig
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.beans.BeanProperty
import scala.language.postfixOps
import scala.sys.process._

object TestUtil {
  val testResourceRoot: String = "src/test/resources"
  class TestConfig {
    @BeanProperty var zone: String = _
    @BeanProperty var bucket_name: String = _
  }

  def getQSConfig: QSConfig = {
    QSConfig.loadConfigFromFile(s"$testResourceRoot/config.yaml")
  }

  def getTestConfig: TestConfig = {
    val ins = new FileInputStream(s"$testResourceRoot/test_config.yaml")
    new Yaml(new Constructor(classOf[TestConfig])).loadAs(ins, classOf[TestConfig])
  }

  def createTmpFile(name: String, count: Int, bs: Int = 1024): Unit = {
    val result = "dd if=/dev/zero of=/tmp/%s bs=%d count=%d".format(name, bs, count) ! ;
    if (result != 0)
      throw new Exception("Create file: %s failed".format(name))
  }
}
