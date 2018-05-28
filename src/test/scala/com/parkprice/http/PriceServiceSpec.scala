package com.parkprice.http

import cats.effect.IO
import com.parkprice.BaseSpec
import com.parkprice.domain.Rates
import org.http4s._
import org.http4s.implicits._

class PriceServiceSpec extends BaseSpec {

    test("Provided Example 1") {
        val resp = getRate("2015-07-01T07:00:00Z", "2015-07-01T12:00:00Z")
        resp.status should be(Status.Ok)
    }

    test("Provided Example 2") {
        val resp = getRate("2015-07-04T07:00:00Z", "2015-07-04T12:00:00Z")
        resp.status should be(Status.Ok)
    }

    test("Provided Example 3") {
        val resp = getRate("2015-07-04T07:00:00Z", "2015-07-04T20:00:00Z")
        resp.status should be(Status.Ok)
        //resp.as[String].unsafeRunSync should be("Hello")
    }

    test("Incomplete Uri") {
        val uri = Uri.uri("/price") / "2015-07-04T07:00:00Z"
        getRate(uri).status should be(Status.NotFound)
        getRate(uri / "to").status should be(Status.NotFound)
    }

    test("Not ISO datetimes") {
        getRate("2015-07-04T07:00:00Z", "bad").status should be(Status.BadRequest)
        getRate("bad", "2015-07-04T07:00:00Z").status should be(Status.BadRequest)
        getRate("bad", "ugly").status should be(Status.BadRequest)

        getRate("2015-07-04", "2015-07-04").status should be(Status.BadRequest)
        getRate("2015-07-04T07:00:00", "2015-07-04T20:00:00").status should be(Status.BadRequest)
    }

    def getRate(from: String, to: String): Response[IO] =
        getRate(Uri.uri("/price") / from / "to" / to)

    def getRate(uri: Uri): Response[IO] = {
        val getRate = Request[IO](Method.GET, uri)
        val rates = Rates(Vector())
        new PriceService[IO](rates).service.orNotFound(getRate).unsafeRunSync()
    }
}
