package com.qingstor.sdk.config

import java.io.{File, FileNotFoundException, FileWriter}

import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.util.{QSLogger, YamlUtil}

import scala.beans.BeanProperty
import scala.io.Source

class QSConfig(
    private val _accessKeyID: String,
    private val _secretAccessKey: String,
    private val _host: String = "qingstor.com",
    private val _port: Int = 443,
    private val _protocol: String = "https",
    private val _connectionRetries: Int = 3,
    private val _logLevel: String = QSConstants.LogWarn
) {
  @BeanProperty var access_key_id: String = _accessKeyID
  @BeanProperty var secret_access_key: String = _secretAccessKey
  @BeanProperty var host: String = _host
  @BeanProperty var port: Int = _port
  @BeanProperty var protocol: String = _protocol
  @BeanProperty var connection_retries: Int = _connectionRetries
  @BeanProperty var log_level: String = _logLevel

  def this() = {
    this("", "")
  }
}

object QSConfig {
  private val DefaultConfigDir = "~/.qingstor/"
  private val DefaultConfigFile = "~/.qingstor/config.yaml"
  private val DefaultConfigFileContent =
    """
      |# QingStor services configuration
      |#access_key_id: 'ACCESS_KEY_ID'
      |#secret_access_key: 'SECRET_ACCESS_KEY'
      |host: 'qingstor.com'
      |port: 443
      |protocol: 'https'
      |connection_retries: 3
      |# Valid log levels are "debug", "info", "warn", "error", and "fatal".
      |log_level: 'warn'
    """.stripMargin

  def apply(
      accessKeyID: String, secretAccessKey: String, host: String = "qingstor.com",
      port: Int = 443, protocol: String = "https", connectionRetries: Int = 3,
      logLevel: String = QSConstants.LogWarn
  ): QSConfig = new QSConfig(accessKeyID, secretAccessKey, host, port, protocol, connectionRetries, logLevel)

  def apply(accessKeyID: String, secretAccessKey: String): QSConfig =
    new QSConfig(accessKeyID, secretAccessKey)

  def apply(): QSConfig = new QSConfig()

  // loadUserConfig loads user configuration in ~/.qingstor/config.yaml for Config.
  def loadUserConfig(): QSConfig = {
    try {
      loadConfigFromFile(DefaultConfigFile)
    } catch {
      case fnd: FileNotFoundException =>
        QSLogger.warn(
          "Installing default config file to \"" + convertToRealPath(
            DefaultConfigFile) + "\"")
        installDefaultUserConfig()
        loadUserConfig()
    }
  }

  // loadConfigFromFile loads configuration from a specified local path.
  @throws[FileNotFoundException]
  def loadConfigFromFile(filepath: String): QSConfig = {
    val file = new File(convertToRealPath(filepath))
    loadConfigFromContent(Source.fromFile(file).mkString)
  }

  // loadConfigFromContent loads configuration from given string.
  def loadConfigFromContent(content: String): QSConfig = {
    YamlUtil.YAMLDecode(content, QSConfig())
  }

  // installDefaultUserConfig install default configuration file to ~/.qingstor/
  private def installDefaultUserConfig() = {
    val configDir = new File(convertToRealPath(DefaultConfigDir))
    if (!configDir.exists())
      configDir.mkdirs()
    val configFile = new File(convertToRealPath(DefaultConfigFile))
    val writer = new FileWriter(configFile)
    writer.write(DefaultConfigFileContent)
    writer.close()
  }

  private def userHome = scala.util.Properties.userHome

  // convertToRealPath convert '~/xxx' to real path
  private def convertToRealPath(filepath: String): String =
    filepath.replaceFirst("~/", userHome + "/")
}
