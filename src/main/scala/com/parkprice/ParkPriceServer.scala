package com.parkprice

import cats.effect.{Effect, IO}
import com.parkprice.api.Types.Dto
import com.parkprice.domain.Rates
import com.parkprice.http.PriceService
import com.typesafe.scalalogging.StrictLogging
import fs2.StreamApp
import io.circe.generic.auto._
import io.circe.parser._
import org.http4s.server.blaze.BlazeBuilder
import org.rogach.scallop._

import scala.concurrent.ExecutionContext
import scala.io.Source

/**
  * Entrypoint of the program
  */
object ParkPriceService extends StreamApp[IO] with StrictLogging {
    import scala.concurrent.ExecutionContext.Implicits.global

    def stream(args: List[String], requestShutdown: IO[Unit]) = {
        val conf = new CommandLineConf(args)
        val rates = parseRatesFromFile(conf.rateFile())
        if (rates.isEmpty) throw new IllegalArgumentException(s"${conf.rateFile()} is not a valid rate file")
        ServerStream.stream[IO](conf.port(), rates.get)
    }

    private def parseRatesFromFile(path: String): Option[Rates] = {
        logger.info(s"Parsing Rates from $path")
        // Just read it fully into memory and parse it since this should be a really small file.
        val rateJson = Source.fromFile(path).mkString
        val decoded = decode[Dto.Options](rateJson)
        logger.info(s"Decoded Rates: $decoded")
        val validated = decoded.toOption.flatMap(opts => Rates.fromDto(opts.rates))
        logger.info(s"Validated Rates: $validated")
        validated
    }
}

final class CommandLineConf(args: Seq[String]) extends ScallopConf(args) {
    val rateFile = opt[String](descr="Path to JSON file containing parking rates", required = true)
    val port = opt[Int](descr="Port the service should listen on", default = Some(8080))
    verify()
}

object ServerStream {
    def rateService[F[_]: Effect](rates: Rates) = new PriceService[F](rates).service

    def stream[F[_]: Effect](port: Int, rates: Rates)(implicit ec: ExecutionContext) =
        BlazeBuilder[F]
            .bindHttp(port, "0.0.0.0")
            .mountService(rateService(rates), "/")
            .serve
}
