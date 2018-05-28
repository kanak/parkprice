package com.parkprice.domain

import java.time.{LocalDateTime, LocalTime, ZonedDateTime}

import com.google.common.collect.{Range => GuavaRange}

import scala.util.Try

final case class ParkingRequest(range: GuavaRange[LocalDateTime]) {
    import ParkingRequest._

    def getTimeRange: GuavaRange[LocalTime] =
        makeRange(range.lowerEndpoint.toLocalTime, range.upperEndpoint.toLocalTime).get
}

object ParkingRequest {

    def parse(startRange: String, endRange: String): Option[ParkingRequest] = {
        for {
            s <- parseIsoDateTime(startRange)
            e <- parseIsoDateTime(endRange)
            r <- makeRange(s, e)
        } yield ParkingRequest(r)
    }

    private def parseIsoDateTime(s: String): Option[LocalDateTime] =
        Try(ZonedDateTime.parse(s).toLocalDateTime).toOption

    // NOTE: closed, not closedOpen because of the example in the spec where end time of request = end time of rate
    // means unavailable.
    private def makeRange[T <: Comparable[_]](start: T, end: T): Option[GuavaRange[T]] =
        Try(GuavaRange.closed(start, end)).toOption
}