package com.parkprice.domain

import java.time.DayOfWeek._

import com.parkprice.BaseSpec

class RateSpec extends BaseSpec {
    import Rate._

    test("ParseDay Cases in Spec") {
        parseCommaSeparatedDays("mon,tues,wed,thurs,fri").value should be(Set(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY))
        parseCommaSeparatedDays("sat,sun").value should be(Set(SATURDAY, SUNDAY))
        parseCommaSeparatedDays("mon,tues,thurs").value should be(Set(MONDAY, TUESDAY, THURSDAY))
        parseCommaSeparatedDays("fri,sat,sun").value should be(Set(FRIDAY, SATURDAY, SUNDAY))
        parseCommaSeparatedDays("wed").value should be(Set(WEDNESDAY))
        parseCommaSeparatedDays("mon,wed,sat").value should be(Set(MONDAY, WEDNESDAY, SATURDAY))
        parseCommaSeparatedDays("sun,tues").value should be(Set(SUNDAY, TUESDAY))
        // weird capitalization is ok.
        parseCommaSeparatedDays("sUn,MoN,TUES,wed").value should be(Set(SUNDAY, MONDAY, TUESDAY, WEDNESDAY))
    }

    test("Invalid Day") {
        parseCommaSeparatedDays("thors") should be(empty)
    }

    test("ParseTimes Cases in Spec") {
        parseTimes("0900-2100").value should be(timeRange(9, 0, 21, 0).get)
        parseTimes("0600-1800").value should be(timeRange(6, 0, 18, 0).get)
        parseTimes("0600-2000").value should be(timeRange(6, 0, 20, 0).get)
        parseTimes("0100-0500").value should be(timeRange(1, 0, 5, 0).get)
        parseTimes("0100-0700").value should be(timeRange(1, 0, 7, 0).get)
    }

    test("ValidatePrice Cases in Spec") {
        validatePrice(1500).value should be(1500)
        validatePrice(2000).value should be(2000)
        validatePrice(1750).value should be(1750)
    }

    test("InvalidTimes") {
        // 25 and 26 are not valid hours
        parseTimes("2500-2600") should be(empty)
        // minutes are invalid
        parseTimes("0170-0260") should be(empty)
        // hours & mins are valid, but the range is invalid
        parseTimes("0800-0700") should be(empty)
        // garbage values
        parseTimes("abc") should be(empty)
        parseTimes("0800-0900-1000") should be(empty)
    }

    test("parse: all valid") {
        parse("sat,sun", "0900-2100", 1500) should not(be(empty))
    }
}
