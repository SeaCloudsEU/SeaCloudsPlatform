[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / POST-metrics-id-observers

# Monitoring Rules

	POST /metrics/:id/observers

## Description
Attach an observer to the metric.

***

## URL Parameters

None

***

## Data Parameters

the callback url of the observer.

***

## Response

**Status:** **201 Created**

**Body:** the observer id.

***

## Errors

* **404 Resource not found** - The metric does not exist.

***

## Example
**Request**

	POST v1/metrics/ResponseTime/observers
	
```
http://url.to.observer.1:8176/ResponseTime
```

**Response**

	Status: 201 Created

```
observer-id
```