package com.qingstor.sdk.util

import com.qingstor.sdk.constant.QSConstants
import com.qingstor.sdk.service.Types.KeyModel
import com.qingstor.sdk.service.QSCodec.QSTypesCodec.encodeKeyModel
import io.circe._
import io.circe.syntax._

object QSServiceUtil {

  // Claculate md5 for operation DeleteMultipleObjects
  // return base64 encrypted md5 string
  def calMD5(quiet: Option[Boolean] = None, objects: List[KeyModel]): String = {
    val json = JsonObject.fromMap(
      Map("objects" -> objects.sortBy(_.key).asJson, "quiet" -> quiet.asJson)
    ).asJson.pretty(QSConstants.printer)
    SecurityUtil.encodeToBase64String(SecurityUtil.getMD5(json))
  }
}
