package com.qingstor.sdk.util

import java.security.MessageDigest
import java.util.Base64

object SecurityUtil {
  def getMD5(string: String): Array[Byte] = {
    MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"))
  }

  def encodeToBase64String(bytes: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(bytes)
  }
}
