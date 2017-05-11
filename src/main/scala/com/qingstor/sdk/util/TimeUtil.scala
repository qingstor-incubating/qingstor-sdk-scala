package com.qingstor.sdk.util

import java.time.format.DateTimeFormatter
import java.time._

private[sdk] object TimeUtil {
  def zonedDateTimeToString(time: ZonedDateTime = ZonedDateTime.now()): String =
    DateTimeFormatter.RFC_1123_DATE_TIME
      .withZone(ZoneOffset.UTC)
      .format(time)

  def stringToZonedDateTime(time: String, zoneId: ZoneId = ZoneOffset.UTC): ZonedDateTime = {
    val temp = DateTimeFormatter.RFC_1123_DATE_TIME
      .withZone(zoneId)
      .parse(time)
    ZonedDateTime.from(temp)
  }

  def stringToUnixInt(time: String): Long = stringToZonedDateTime(time).toInstant.toEpochMilli
}
