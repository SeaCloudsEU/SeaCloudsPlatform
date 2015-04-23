[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / DELETE-metrics-id-observers-id

# Metrics

	DELETE /metrics/:id/observers/:id

## Description
Detach the observer from the metric.

***

## URL Parameters

None.

***

## Response

**Status:** **204 No Content**

***

## Errors

* **404 Resource not found** - Either the metric or the observer does not exist.

***

## Example
**Request**

	DELETE v1/metrics/ResponseTime/observers/observer-1