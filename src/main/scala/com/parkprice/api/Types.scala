package com.parkprice.api

/**
  * Types that are part of the public API.
  */
object Types {

    /**
      * Data Transfer Objects whose fieldnames and types are part of an external API.
      */
    object Dto {
        /** Specifies the price of parking on the given days at the given times **/
        final case class Rate(days: String, times: String, price: Int)
        /** Service options file **/
        final case class Options(rates: Vector[Rate])
    }
}
