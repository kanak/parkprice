# API Documentation

## Get a price request
### Endpoint
`GET /price/{from}/to/{to}`

### Request Parameters
Path Parameter:
- `from`: The ISO-8601 instant that represents the start (inclusive) of the parking spot request.
- `to`: The ISO-8601 instant that represents the end (inclusive) of the parking spot request.

### Response Format
On success, the status code is `200` and the response body contains a JSON with the following fields:

- `request_start`: The start time that was requested.
- `request_end`: The end time that was requested.
- `price`: Either an integer if request could be satisfied, or the string "unavailable" if it could not.

### Example
`GET /price/2015-07-04T07:00:00Z/to/2015-07-04T20:00:00Z`

Suppose there is a spot available for that time range with a price of $1500. The response would look like:

`{"request_start":"2015-07-04T07:00:00Z","request_end":"2015-07-04T20:00:00Z","price":1500}`

But if there was not any spot available, the response would look like:

`{"request_start":"2015-07-04T07:00:00Z","request_end":"2015-07-04T20:00:00Z","price":"unavailable"}`
