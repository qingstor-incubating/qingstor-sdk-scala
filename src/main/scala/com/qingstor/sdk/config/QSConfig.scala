package com.qingstor.sdk.config

import java.io.{File, FileNotFoundException, FileWriter}

import com.qingstor.sdk.constants.QSConstants
import com.qingstor.sdk.utils.{QSLogger, YAML}

import scala.beans.BeanProperty
import scala.io.Source

class QSConfig {
  @BeanProperty var accessKeyID: String = ""
  @BeanProperty var secretAccessKey: String = ""
  @BeanProperty var host: String = "qingstor.com"
  @BeanProperty var port: Int = 443
  @BeanProperty var protocol: String = "https"
  @BeanProperty var connectionRetries: Int = 3
  @BeanProperty var logLevel: String = QSConstants.LogWarn

  // TODO: HTTP Connection

  def this(id: String, secret: String) {
    this()
    this.accessKeyID = id
    this.secretAccessKey = secret
  }

  def this(id: String, secret: String, host: String, port: Int, protocol: String, connRetries: Int, logLevel: String) {
    this()
    this.accessKeyID = id
    this.secretAccessKey = secret
    this.host = host
    this.port = port
    this.protocol = protocol
    this.connectionRetries = connRetries
    this.logLevel = logLevel
  }
}

object QSConfig {
  private val DefaultConfigDir = "~/.qingstor/"
  private val DefaultConfigFile = "~/.qingstor/config.yaml"
  private val DefaultConfigFileContent =
    """
      |# QingStor services configuration
      |
      |#accessKeyID: 'ACCESS_KEY_ID'
      |#secretAccessKey: 'SECRET_ACCESS_KEY'
      |
      |host: 'qingstor.com'
      |port: 443
      |protocol: 'https'
      |connectionRetries: 3
      |
      |# Valid log levels are "debug", "info", "warn", "error", and "fatal".
      |logLevel: 'warn'
    """.stripMargin

  // loadUserConfig loads user configuration in ~/.qingstor/config.yaml for Config.
  def loadUserConfig(): QSConfig = {
    try{
      loadConfigFromFile(DefaultConfigFile)
    } catch {
      case fnd: FileNotFoundException =>
        QSLogger.warn("Installing default config file to \"" + convertToRealPath(DefaultConfigFile) +"\"" )
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
    YAML.YAMLDecode(content, new QSConfig())
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
  private def convertToRealPath(filepath: String): String = filepath.replaceFirst("~/", userHome+"/")
}
