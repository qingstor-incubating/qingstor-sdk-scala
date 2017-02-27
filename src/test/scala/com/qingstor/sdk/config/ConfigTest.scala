package com.qingstor.sdk.config

import com.qingstor.sdk.constant.QSConstants
import org.scalatest.FunSuite

class ConfigTest extends FunSuite{

  test("Config test") {
    val config = new QSConfig("AccessKeyID", "SecretAccessKey", "qingstor.com", 80, "http", 10, QSConstants.LogWarn)
    assert(config.access_key_id == "AccessKeyID")
    assert(config.secret_access_key == "SecretAccessKey")
    assert(config.host == "qingstor.com")
    assert(config.port == 80)
    assert(config.protocol == "http")
    assert(config.connection_retries == 10)
    assert(config.log_level == QSConstants.LogWarn)
  }

  test("Default config test") {
    val config = new QSConfig()
    assert(config.access_key_id == "")
    assert(config.secret_access_key == "")
    assert(config.host == "qingstor.com")
    assert(config.port == 443)
    assert(config.protocol == "https")
    assert(config.connection_retries == 3)
    assert(config.log_level == QSConstants.LogWarn)
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
        |#access_key_id: "ACCESS_KEY_ID"
        |#secret_access_key: "SECRET_ACCESS_KEY"
        |host: "api.qingstor.com"
        |port: 443
        |protocol: 'https'
        |connection_retries: 5
        |# Valid log levels are "debug", "info", "warn", "error", and "fatal".
        |log_level: 'error'
      """.stripMargin
    val config = QSConfig.loadConfigFromContent(content)
    assert(config.host == "api.qingstor.com")
    assert(config.port == 443)
    assert(config.protocol == "https")
    assert(config.connection_retries == 5)
    assert(config.log_level == QSConstants.LogError)
  }
}
