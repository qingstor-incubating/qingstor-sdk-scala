package com.qingstor.sdk.request

trait ParamValidate {
  abstract def validateParam(): String
}
