[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / GET-metrics-id-observers

# Metrics

	GET /metrics/:id/observers

## Description
Returns the list of observers attached to the metric.

***

## URL Parameters

None

***

## Response

**Status:** **200 OK**

**Body:** A json object with a list of information about attached observers.

***

## Errors

* **404 Resource not found** - The metric does not exist.

***

## Example
**Request**

	GET v1/metrics/ResponseTime/observers

**Response**

	Status: 200 OK

``` json
{
	"observers": [
		{
			"id"="109384935893",
			"callbackUrl"="http://url.to.observer.1:9999/path"
		},
		{
			"id"="109384314891",
			"callbackUrl"="http://url.to.observer.2:9999/another/path"
		},
	]
}
```