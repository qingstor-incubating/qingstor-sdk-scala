package com.qingstor.sdk.utils

import java.util.logging.{Logger, Level}

import com.qingstor.sdk.constants.QSConstants

object QSLogger {
  def setLoggerHandler(loggerName: String): Logger = {
    val logger = Logger.getLogger(loggerName)

    QSConstants.LoggerLevel match {
      case QSConstants.LoggerFatal => logger.setLevel(Level.SEVERE)
      case QSConstants.LoggerError => logger.setLevel(Level.SEVERE)
      case QSConstants.LoggerWarn => logger.setLevel(Level.WARNING)
      case QSConstants.LoggerDebug => logger.setLevel(Level.ALL)
      case QSConstants.LoggerInfo => logger.setLevel(Level.INFO)
      case _ => logger.setLevel(Level.WARNING)
    }
    logger
  }
}
