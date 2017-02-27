package com.qingstor.sdk.exception

import com.qingstor.sdk.model.QSModels.ErrorMessage

class QingStorException(private val msg: String) extends Throwable{
  private val message = msg

  override def getCause: Throwable = super.getCause

  override def printStackTrace(): Unit = super.printStackTrace()

  override def getMessage: String = message

}

object QingStorException {
  def apply(error: ErrorMessage): QingStorException = {
    val errorMessage = "request_id " + error.requestID + (
      error.statusCode match {
        case Some(code) => ", status_code " + String.valueOf(code)
        case None => ""
      }
    ) + (
      error.code match {
        case Some(code) => ", code " + code
        case None => ""
      }
    ) + (
      error.message match {
        case Some(message) => ", message " + message
        case None => ""
      }
    ) + (
      error.url match {
        case Some(url) => ", url " + url
        case None => ""
      }
    )
    val msg = "QingStor exception: %s".format(errorMessage)
    new QingStorException(msg)
  }

  def apply(msg: String): QingStorException = new QingStorException(msg)
}
