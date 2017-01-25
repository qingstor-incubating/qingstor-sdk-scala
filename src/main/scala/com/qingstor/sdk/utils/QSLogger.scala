package com.qingstor.sdk.utils

import java.util.logging.{Logger, Level}

import com.qingstor.sdk.constants.QSConstants

object QSLogger {
  private val logger = Logger.getLogger(this.getClass.getName)
  private var level = "warn"
  logger.setLevel(Level.WARNING)

  def getLevel =  this.level

  def setLevel(level: String) = {
    level match {
      case "debug" => logger.setLevel(Level.ALL)
      case "info" => logger.setLevel(Level.INFO)
      case "warn" => logger.setLevel(Level.WARNING)
      case "error" => logger.setLevel(Level.SEVERE)
      case "fatal" => logger.setLevel(Level.SEVERE)
      case _ => fatal("Log level invalid: " + level)
    }
    this.level = level
  }

  def debug(msg: String) = {
    logger.log(Level.ALL, msg)
  }

  def info(msg: String) = {
    logger.log(Level.INFO, msg)
  }

  def warn(msg: String) = {
    logger.log(Level.WARNING, msg)
  }

  def error(msg: String) = {
    logger.log(Level.SEVERE, msg)
  }

  def fatal(msg: String) = {
    logger.log(Level.SEVERE, msg)
    scala.sys.exit(-1)
  }
}
