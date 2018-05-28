package com.parkprice.domain

import java.time.LocalDateTime

import com.google.common.collect.{Range => GuavaRange}
import com.parkprice.BaseSpec

class ParkingRequestSpec extends BaseSpec {
    import ParkingRequest._

    test("Parse Spec Example 1") {
        parse("2015-07-01T07:00:00Z", "2015-07-01T12:00:00Z").value should be(
            ParkingRequest(GuavaRange.closed(
                LocalDateTime.of(2015, 7, 1, 7, 0, 0),
                LocalDateTime.of(2015, 7, 1, 12, 0, 0))))
    }

    test("Parse Spec Example 2") {
        parse("2015-07-04T07:00:00Z", "2015-07-04T12:00:00Z").value should be(
            ParkingRequest(GuavaRange.closed(
                LocalDateTime.of(2015, 7, 4, 7, 0, 0),
                LocalDateTime.of(2015, 7, 4, 12, 0, 0))))
    }

    test("Parse Spec Example 3") {
        parse("2015-07-04T07:00:00Z", "2015-07-04T20:00:00Z").value should be(
            ParkingRequest(GuavaRange.closed(
                LocalDateTime.of(2015, 7, 4, 7, 0, 0),
                LocalDateTime.of(2015, 7, 4, 20, 0, 0))))
    }

    test("Fail if Start is after End") {
        parse("2015-07-04T20:00:00Z", "2015-07-04T07:00:00Z") should be(empty)
    }

    test("Fail if either is not ISO date string") {
        parse("2015-07-04T20:00:00Z", "hello") should be(empty)
        parse("hello", "2015-07-04T20:00:00Z") should be(empty)

        parse("2015-07-04 20:00:00", "2015-07-04T20:00:00Z") should be(empty)
    }
}
