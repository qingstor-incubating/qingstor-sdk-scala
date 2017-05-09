package com.qingstor.sdk.util

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SecurityUtil {
  def getMD5(string: String): Array[Byte] = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"))

  def getHmacSHA256(key: String, content: String): Array[Byte] = {
    val secret = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secret)
    mac.doFinal(content.getBytes("UTF-8"))
  }

  def encodeToBase64String(bytes: Array[Byte]): String = Base64.getEncoder.encodeToString(bytes)
}
