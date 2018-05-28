package com.parkprice.domain

import java.time.{DayOfWeek, LocalTime}

import com.google.common.collect.{Range => GuavaRange}
import com.parkprice.api.Types.Dto

import scala.util.Try

final case class Rate(
    days: Set[DayOfWeek],
    times: GuavaRange[LocalTime],
    price: Int)

object Rate {

    def fromDto(dto: Dto.Rate): Option[Rate] = parse(dto.days, dto.times, dto.price)

    private[parkprice] def parse(days: String, times: String, price: Int): Option[Rate] = {
        for {
            ds <- parseCommaSeparatedDays(days)
            ts <- parseTimes(times)
            // price is already an int, so only validate that it is positive
            p <- validatePrice(price)
        } yield Rate(ds, ts, p)
    }

    private[parkprice] def parseCommaSeparatedDays(s: String): Option[Set[DayOfWeek]] = {
        val days = s.split(',')
        val y = Some(days.flatMap(parseDay).toSet)
        y.filter(_.size == days.length)
    }

    private def parseDay(s: String): Option[DayOfWeek] = s.toLowerCase match {
        case "sun" => Some(DayOfWeek.SUNDAY)
        case "mon" => Some(DayOfWeek.MONDAY)
        case "tues" => Some(DayOfWeek.TUESDAY)
        case "wed" => Some(DayOfWeek.WEDNESDAY)
        case "thurs" => Some(DayOfWeek.THURSDAY)
        case "fri" => Some(DayOfWeek.FRIDAY)
        case "sat" => Some(DayOfWeek.SATURDAY)
        case _ => None
    }

    private[parkprice] def parseTimes(s: String): Option[GuavaRange[LocalTime]] = {
        val pattern = """(\d{2})(\d{2})-(\d{2})(\d{2})""".r
        s match {
            case pattern(startHr, startMin, endHr, endMin) =>
                timeRange(startHr.toInt, startMin.toInt, endHr.toInt, endMin.toInt)
            case _ => None
        }
    }

    private[parkprice] def timeRange(fromHr: Int, fromMin: Int, toHr: Int, toMin: Int): Option[GuavaRange[LocalTime]] =
        Try(GuavaRange.closedOpen(LocalTime.of(fromHr, fromMin), LocalTime.of(toHr, toMin))).toOption

    private[parkprice] def validatePrice(s: Int): Option[Int] =
        Some(s).filter(_ > 0)
}