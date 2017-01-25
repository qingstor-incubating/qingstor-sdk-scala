package com.qingstor.sdk.config

import com.qingstor.sdk.constants.QSConstants
import org.scalatest.FunSuite

class ConfigTest extends FunSuite{

  test("Config test") {
    val config = new QSConfig("AccessKeyID", "SecretAccessKey", "qingstor.com", 80, "http", 10, QSConstants.LogWarn)
    assert(config.accessKeyID == "AccessKeyID")
    assert(config.secretAccessKey == "SecretAccessKey")
    assert(config.host == "qingstor.com")
    assert(config.port == 80)
    assert(config.protocol == "http")
    assert(config.connectionRetries == 10)
    assert(config.logLevel == QSConstants.LogWarn)
  }

  test("Default config test") {
    val config = new QSConfig()
    assert(config.accessKeyID == "")
    assert(config.secretAccessKey == "")
    assert(config.host == "qingstor.com")
    assert(config.port == 443)
    assert(config.protocol == "https")
    assert(config.connectionRetries == 3)
    assert(config.logLevel == QSConstants.LogWarn)
  }

  test("loadUserConfig") {
    val config = QSConfig.loadUserConfig()
    assert(config.host == "qingstor.com")
    assert(config.protocol == "https")
    assert(config.port == 443)
  }

  test("loadConfigFromContent") {
    val content: String =
      """
        |# QingStor services configuration
        |
        |#accessKeyID: "ACCESS_KEY_ID"
        |#secretAccessKey: "SECRET_ACCESS_KEY"
        |
        |host: "api.qingstor.com"
        |port: 443
        |protocol: 'https'
        |connectionRetries: 5
        |
        |# Valid log levels are "debug", "info", "warn", "error", and "fatal".
        |logLevel: 'error'
      """.stripMargin
    val config = QSConfig.loadConfigFromContent(content)
    assert(config.host == "api.qingstor.com")
    assert(config.port == 443)
    assert(config.protocol == "https")
    assert(config.connectionRetries == 5)
    assert(config.logLevel == QSConstants.LogError)
  }
}
