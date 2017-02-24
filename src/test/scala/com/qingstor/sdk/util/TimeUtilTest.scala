package com.qingstor.sdk.util

import java.time._

import org.scalatest.FunSuite

class TimeUtilTest extends FunSuite{
  test("ZonedDateTime to String test") {
    val time = ZonedDateTime.of(2017, 2, 17, 10, 0, 0, 0, ZoneId.of("Asia/Shanghai"))
    val timeString = "Fri, 17 Feb 2017 02:00:00 GMT"
    assert(timeString == TimeUtil.zonedDateTimeToString(time))
  }

  test("String to ZonedDateTime test") {
    val timeString = "Fri, 17 Feb 2017 10:00:00 GMT"
    val time = ZonedDateTime.of(2017, 2, 17, 10, 0, 0, 0, ZoneOffset.UTC)
    assert(time == TimeUtil.stringToZonedDateTime(timeString))
  }

  test("String to unix int test") {
    val timeString = "Fri, 17 Feb 2017 10:00:00 GMT"
    assert(1487325600000L == TimeUtil.stringToUnixInt(timeString))
  }
}
