# Parking Price REST API Service

## Summary
I used Scala with the http4s library ( https://http4s.org/ ), which is at a level comparable to JAX-RS, to implement the library.

I have a single route: `/price/<FROM>/to/<TO>` which returns a response that looks like `{"request_start":"2015-07-04T07:00:00Z","request_end":"2015-07-04T12:00:00Z","price":2000}`. The `APIDoc.md` file documents this API.

The rate file is specified at startup via a command-line flag `--rate-file`.


## Running the Solution

### Start the server

The easiest way is to let `sbt` run the web server: `sbt "run --rate-file ./sample_rates1.json"`.

Alternatively, `sbt assembly` will generate a fat-jar at `target/scala-2.12/` called `parkprice-assembly-0.1.0-SNAPSHOT.jar`. This jar can then be run using `java -jar target/scala-2.12/parkprice-assembly-0.1.0-SNAPSHOT.jar  --rate-file ./sample_rates1.json`.

The default port is 8080, but can be changed via `--port 8181`.

Upon startup, the server prints out some diagnostic info about the rate file:

```
[main] INFO  c.p.HelloWorldServer$ - Parsing Rates from ./sample_rates1.json
[main] INFO  c.p.HelloWorldServer$ - Decoded Rates: Right(Options(Vector(Rate(mon,tues,wed,thurs,fri,0600-1800,1500), Rate(sat,sun,0600-2000,2000))))
[main] INFO  c.p.HelloWorldServer$ - Validated Rates: Some(Rates(Vector(Rate(Set(THURSDAY, TUESDAY, FRIDAY, MONDAY, WEDNESDAY),[06:00..18:00),1500), Rate(Set(SATURDAY, SUNDAY),[06:00..20:00),2000))))
[main] INFO  o.h.b.c.n.NIO1SocketServerGroup - Service bound to address /0:0:0:0:0:0:0:0:8080
[main] INFO  o.h.s.b.BlazeBuilder -   _   _   _        _ _
[main] INFO  o.h.s.b.BlazeBuilder -  | |_| |_| |_ _ __| | | ___
[main] INFO  o.h.s.b.BlazeBuilder -  | ' \  _|  _| '_ \_  _(_-<
[main] INFO  o.h.s.b.BlazeBuilder -  |_||_\__|\__| .__/ |_|/__/
[main] INFO  o.h.s.b.BlazeBuilder -              |_|
[main] INFO  o.h.s.b.BlazeBuilder - http4s v0.18.11 on blaze v0.12.13 started at http://[0:0:0:0:0:0:0:0]:8080/
```

### Sample Interaction (Available Price)
`curl -i http://localhost:8080/price/2015-07-04T07:00:00Z/to/2015-07-04T12:00:00Z -w "\n"`

gives:
```
HTTP/1.1 200 OK
Content-Type: application/json
Date: Mon, 28 May 2018 03:16:28 GMT
Content-Length: 90

{"request_start":"2015-07-04T07:00:00Z","request_end":"2015-07-04T12:00:00Z","price":2000}
```

### Sample Interaction (Unavailable Price)
`curl -i http://localhost:8080/price/2015-07-04T07:00:00Z/to/2015-07-04T20:00:00Z -w "\n"`

gives:

```
HTTP/1.1 200 OK
Content-Type: application/json
Date: Mon, 28 May 2018 11:58:49 GMT
Content-Length: 99

{"request_start":"2015-07-04T07:00:00Z","request_end":"2015-07-04T20:00:00Z","price":"unavailable"}
```

## Solution Overview

This section has a high level overview of my thought process when implementing the solution.

### Use types
Use types extensively to enforce invariants, immutable types to make things easier, and minimize the use of side-effects. I've also been careful to separate the types that are also a part of the public API so that their evolution is more controlled and deliberate. The `api` package includes the "DTO" (data transfer object) definitions while the `domain` package includes the domain objects that are internal.

### Use libraries when dealing with complex domains
The problem involves reasoning about datetimes and ranges. I used JDK8 time library for the former and Guava's range for the latter. Both libraries are designed with the same type-safety goals that I had, so it was really easy to integrate and use them. The only minor hitch was that both libraries use exceptions, but I used the scala `Try` object to turn them into `Options`, so that failure is a value not an effect.

### Simple algorithms to perform matching
The matching algorithm is implemented as a linear scan over all `Rate`s. The performance gains by using hashing or using an indexing structure like interval trees isn't worth it at the time because the number of rates is expected to be small, and also because I didn't want to speculatively build an indexing strategy when I'm not sure how the rule complexity will change.

### Tests
I'm using the scalatest library and have tried to add unit tests covering happy and unhappy paths for all units. I also complement them with larger tests at the service level. In many tests, I have tried to ensure that the examples provided in the spec give the same results.


## Incomplete
- Static API documentation. I wanted to implement Swagger api documentation, but the library I wanted to use, http4s-rho https://github.com/http4s/rho is a work-in-progress and a bit unstable to work with.

- XML support. When the client sets Accepts to XML, I wanted to return the same data as the JSON in an XML. I ran out of time before I could figure out how to plug in a different `EntityEncoder` depending on the Accepts in a header.

- Metrics. Ran out of time before I could hook in either the dropwizard metrics library ( https://metrics.dropwizard.io/3.1.0/ ) or the Prometheus client. I was thinking of tracking (1) counter for unavailable requests, (2) counter for available requests, (3) histogram for latencies.
