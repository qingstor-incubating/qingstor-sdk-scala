package com.qingstor.sdk.util

import scala.reflect._

object ClassUtil {
  def ClassBuilder[T: ClassTag]: T = {
    classTag[T].runtimeClass.newInstance().asInstanceOf[T]
  }
}
