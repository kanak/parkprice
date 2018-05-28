package com.parkprice.domain

import com.parkprice.api.Types.Dto

final case class Rates(rates: Vector[Rate])

object Rates {
    def fromDto(dtos: Vector[Dto.Rate]): Option[Rates] = {
        val parsed = dtos.flatMap(Rate.fromDto)
        // if any object is invalid, treat entire payload as invalid.
        if (parsed.size == dtos.size) Some(Rates(parsed)) else None
    }
}