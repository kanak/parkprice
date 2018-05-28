package com.parkprice

import com.parkprice.api.Types.Dto
import com.parkprice.domain._

class PriceCalculatorSpec extends BaseSpec {

    test("Weekday, in range") {
        rateFor(ParkingRequest.parse("2015-07-01T07:00:00Z", "2015-07-01T12:00:00Z").get) should be(Available(1500))
    }

    test("Weekday, out of range") {
        // Whole range before opening
        rateFor(ParkingRequest.parse("2015-07-01T05:00:00Z", "2015-07-01T05:30:00Z").get) should be(Unavailable)
        // Overlaps, but before opening
        rateFor(ParkingRequest.parse("2015-07-01T05:00:00Z", "2015-07-01T07:30:00Z").get) should be(Unavailable)
        // Overlaps, but after closing
        rateFor(ParkingRequest.parse("2015-07-01T17:00:00Z", "2015-07-01T19:30:00Z").get) should be(Unavailable)
        // Entirely out of closing
        rateFor(ParkingRequest.parse("2015-07-01T19:00:00Z", "2015-07-01T19:30:00Z").get) should be(Unavailable)
    }

    test("Weekend, in range") {
        rateFor(ParkingRequest.parse("2015-07-04T07:00:00Z", "2015-07-04T12:00:00Z").get) should be(Available(2000))
    }

    test("Weekend, out of range") {
        // Whole range before opening
        rateFor(ParkingRequest.parse("2015-07-04T05:00:00Z", "2015-07-04T05:30:00Z").get) should be(Unavailable)
        // Overlaps, but before opening
        rateFor(ParkingRequest.parse("2015-07-04T05:00:00Z", "2015-07-04T07:30:00Z").get) should be(Unavailable)
        // Overlaps, but after closing
        rateFor(ParkingRequest.parse("2015-07-04T17:00:00Z", "2015-07-04T20:30:00Z").get) should be(Unavailable)
        // Entirely out of closing
        rateFor(ParkingRequest.parse("2015-07-04T20:00:00Z", "2015-07-04T20:30:00Z").get) should be(Unavailable)
    }

    test("Rate range end should be exclusive") {
        rateFor(ParkingRequest.parse("2015-07-01T07:00:00Z", "2015-07-04T20:00:00Z").get) should be(Unavailable)
    }

    test("Requested interval larger than available") {
        // single day
        rateFor(ParkingRequest.parse("2015-07-04T05:00:00Z", "2015-07-04T20:30:00Z").get) should be(Unavailable)
        // multi-day: start at 6:30 on saturday and end at 6:45am on the next day
        rateFor(ParkingRequest.parse("2015-07-04T06:30:00Z", "2015-07-05T06:45:00Z").get) should be(Unavailable)
    }

    lazy val rates = Rates(Vector(
        Rate.fromDto(Dto.Rate("mon,tues,wed,thurs,fri", "0600-1800", 1500)).get,
        Rate.fromDto(Dto.Rate("sat,sun", "0600-2000", 2000)).get))

    def rateFor(req: ParkingRequest): PriceResponse = PriceCalculator.price(rates)(req)


}
