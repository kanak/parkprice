package com.parkprice.http

import cats.effect.Effect
import com.parkprice.PriceCalculator
import com.parkprice.domain._
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

/**
  * Implements the HTTP service that returns parking spot prices.
  */
class PriceService[F[_]: Effect](rates: Rates) extends Http4sDsl[F] {

    val service: HttpService[F] = {
        HttpService[F] {
            case GET -> Root / "price" / from / "to" / to =>
                ParkingRequest.parse(from, to) match {
                    case None =>
                        BadRequest()
                    case Some(req) =>
                        val price = PriceCalculator.price(rates)(req)
                        Ok(Json.obj(
                            "request_start" -> Json.fromString(from),
                            "request_end" -> Json.fromString(to),
                            "price" -> priceAsJson(price)))
                }
        }
    }

    private def priceAsJson(pr: PriceResponse): Json = pr match {
        case Available(price) => Json.fromInt(price)
        case Unavailable => Json.fromString("unavailable")
    }
}
