package com.parkprice.domain

sealed trait PriceResponse
final case class Available(price: Int) extends PriceResponse
case object Unavailable extends PriceResponse
