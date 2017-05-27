package com.qingstor.sdk.exception

import com.qingstor.sdk.model.QSModels.ErrorMessage

class QingStorException(error: ErrorMessage) extends Throwable {

  def errorMessage: ErrorMessage = error

  override def getCause: Throwable = super.getCause

  override def printStackTrace(): Unit = super.printStackTrace()

  override def getMessage: String = {
    val errorMessage = "request_id: " + error.requestID + (
        error.statusCode match {
          case Some(code) => ", status_code: " + String.valueOf(code)
          case None => ""
        }
      ) + (
        error.code match {
          case Some(code) => ", code: " + code
          case None => ""
        }
      ) + (
        error.message match {
          case Some(message) => ", message: " + message
          case None => ""
        }
      ) + (
        error.url match {
          case Some(url) => ", url: " + url
          case None => ""
        }
      )
    "QingStor exception:\n\t%s".format(errorMessage)
  }
}
