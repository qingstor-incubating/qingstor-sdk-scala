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
    val msg = String.format("QingStor error: code %s, message %s, request_id %s, url %s", error.code, error.message, error.request_id, error.url)
    new QingStorException(msg)
  }

  def apply(msg: String): QingStorException = new QingStorException(msg)
}
