package steps

import java.io.FileInputStream

import com.qingstor.sdk.config.QSConfig
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

object TestUtil {
  case class TestConfig(zone: String, bucket_name: String)

  def getQSConfig: QSConfig = {
    QSConfig.loadConfigFromFile("test/config.yaml")
  }

  def getTestConfig: TestConfig = {
    val ins = new FileInputStream("test/test_config.yaml")
    new Yaml(new Constructor(classOf[TestConfig])).loadAs(ins, classOf[TestConfig])
  }
}
