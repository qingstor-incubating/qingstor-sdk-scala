package com.qingstor.sdk.utils

import com.google.gson.Gson

object JSON {

  // Decode the given json string of a json object
  def DecodeJSONObject(text:String):java.util.Map[String, Any] =
    new Gson().fromJson(text, classOf[java.util.Map[String, Any]])

  // Decode the given json string of a json array
  def DecodeJSONArray(text:String):java.util.ArrayList[Any] =
    new Gson().fromJson(text, classOf[java.util.ArrayList[Any]])
}
