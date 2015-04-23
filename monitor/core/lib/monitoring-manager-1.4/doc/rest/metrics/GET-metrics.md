[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / GET-metrics

# Metrics

	GET /metrics

## Description
Returns the list of metrics available to be observed. Only metrics that are
created using the OutputMetric action in a monitoring rule can be observed.

***

## URL Parameters

None

***

## Response

**Status:** **200 OK**

**Body:** A json object with a list of metrics.

***

## Errors

None

***

## Example
**Request**

	GET v1/metrics

**Response**

	Status: 200 OK

``` json
{
	"metrics": [
		"CpuUtilization","ResponseTime","CpuUtilizationViolation"
	]
}
```