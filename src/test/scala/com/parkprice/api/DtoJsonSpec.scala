package com.parkprice.api

import com.parkprice.BaseSpec
import com.parkprice.api.Types.Dto
import io.circe.generic.auto._
import io.circe.parser._
import org.scalatest.EitherValues

class DtoJsonSpec extends BaseSpec with EitherValues {
    import Dto._

    test("Valid Rate") {
        val rawJson =
            """
              |{ "days": "mon,tues,thurs",
              |  "times": "0900-2100",
              |  "price": 1500 }
            """.stripMargin
        val decoded = decode[Rate](rawJson)
        decoded.right.value should be(Rate("mon,tues,thurs", "0900-2100", 1500))
    }

    test("Valid Options") {
        val rawJson =
            """
              |{ "rates": [
              |        {
              |          "days": "mon,tues,wed,thurs,fri",
              |          "times": "0600-1800",
              |          "price": 1500
              |        },
              |        {
              |          "days": "sat,sun",
              |          "times": "0600-2000",
              |          "price": 2000
              |        }
              |      ]
              |    }
            """.stripMargin
        val decoded = decode[Options](rawJson)

        decoded.right.value should be(Dto.Options(Vector(
            Rate("mon,tues,wed,thurs,fri", "0600-1800", 1500),
            Rate("sat,sun", "0600-2000", 2000))))
    }

    test("Options with empty rates list") {
        val rawJson = """{ "rates": [] }"""
        val decoded = decode[Options](rawJson)
        decoded.right.value should be(Options(Vector()))
    }
}
