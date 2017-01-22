package com.qingstor.sdk.utils

import org.scalatest.FunSuite
import java.util.logging.Level

import com.qingstor.sdk.constants.QSConstants

class QSLoggerTest extends FunSuite{
  test("Default log level should be \"warn\"") {
    val logger = QSLogger.setLoggerHandler(getClass.getName)
    assert(logger.getLevel == Level.WARNING)
  }

  test("Log level should be \"debug\"") {
    QSConstants.LoggerLevel = QSConstants.LoggerError
    val logger = QSLogger.setLoggerHandler(getClass.getName)
    assert(logger.getLevel == Level.SEVERE)
  }
}
