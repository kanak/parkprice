package com.parkprice

import com.parkprice.domain._

/**
  * Matches [[ParkingRequest]] with [[Rates]] and returns a [[PriceResponse]]
  */
object PriceCalculator {

    /**
      * Matches [[ParkingRequest]] with [[Rates]] and returns a [[PriceResponse]]
      */
    def price(rates: Rates)(parkingRequest: ParkingRequest): PriceResponse = {
        // Since rates are defined for a given day, a spanning multiple days is always invalid.
        if (parkingRequest.range.lowerEndpoint.toLocalDate != parkingRequest.range.upperEndpoint.toLocalDate) {
            return Unavailable
        }
        val requestDayOfWeek = parkingRequest.range.lowerEndpoint.getDayOfWeek
        val requestTimeRange = parkingRequest.getTimeRange
        rates.rates
            .find(r => r.days.contains(requestDayOfWeek) && r.times.encloses(requestTimeRange))
            .map(r => Available(r.price))
            .getOrElse(Unavailable)
    }
}