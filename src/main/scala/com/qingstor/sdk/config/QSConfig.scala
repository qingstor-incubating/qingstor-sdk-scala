package com.qingstor.sdk.config

import java.io._

import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.util.QSLogger
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

import scala.beans.BeanProperty

class QSConfig {
  @BeanProperty var access_key_id: String = ""
  @BeanProperty var secret_access_key: String = ""
  @BeanProperty var host: String = "qingstor.com"
  @BeanProperty var port: Int = 443
  @BeanProperty var protocol: String = "https"
  @BeanProperty var connection_retries: Int = 3
  @BeanProperty var log_level: String = QSConstants.LogWarn
}

object QSConfig {
  private val dumpOption = new DumperOptions()
  dumpOption.setPrettyFlow(true)
  private val yaml = new Yaml(new Constructor(classOf[QSConfig]), new Representer(), dumpOption)
  private val defaultConfigFile = "~/.qingstor/config.yaml"

  def apply(accessKeyID: String, secretAccessKey: String,
            host: String = "qingstor.com", port: Int = 443,
            protocol: String = "https", connectionRetries: Int = 3,
            logLevel: String = QSConstants.LogWarn): QSConfig = {
    val config = new QSConfig()
    config.setAccess_key_id(accessKeyID)
    config.setSecret_access_key(secretAccessKey)
    config.setHost(host)
    config.setPort(port)
    config.setProtocol(protocol)
    config.setConnection_retries(connectionRetries)
    config.setLog_level(logLevel)
    config
  }


  def apply(accessKeyID: String, secretAccessKey: String): QSConfig = {
    val config = new QSConfig()
    config.setAccess_key_id(accessKeyID)
    config.setSecret_access_key(secretAccessKey)
    config
  }

  def apply(): QSConfig = new QSConfig()

  // loadUserConfig loads user configuration in ~/.qingstor/config.yaml for Config.
  def loadUserConfig(): QSConfig = {
    try {
      loadConfigFromFile(defaultConfigFile)
    } catch {
      case fnd: FileNotFoundException =>
        QSLogger.warn("Installing default config file to %s".format(convertToRealPath(defaultConfigFile)))
        installDefaultUserConfig()
        QSConfig()
    }
  }

  // loadConfigFromFile loads configuration from a specified local path.
  @throws[FileNotFoundException]
  def loadConfigFromFile(filepath: String): QSConfig = {
    val ins = new FileInputStream(new File(convertToRealPath(filepath)))
    yaml.loadAs(ins, classOf[QSConfig])
  }

  // loadConfigFromContent loads configuration from given string.
  def loadConfigFromContent(content: String): QSConfig = {
    yaml.loadAs(content, classOf[QSConfig])
  }

  // installDefaultUserConfig install default configuration file to ~/.qingstor/
  private def installDefaultUserConfig() = {
    val configFile = new File(convertToRealPath(defaultConfigFile))
    val configDir = new File(configFile.getParent)
    if (!configDir.exists())
      configDir.mkdirs()
    val writer = new BufferedWriter(new FileWriter(configFile))
    val defaultContent = yaml.dumpAsMap(QSConfig())
    writer.write(defaultContent)
    writer.close()
  }

  private val userHome = scala.util.Properties.userHome

  // convertToRealPath convert '~/xxx' to real path
  private def convertToRealPath(filepath: String): String =
    filepath.replaceFirst("~/", userHome + "/")
}
