[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / DELETE-monitoring-rules-id

# Monitoring Rules

	DELETE /monitoring-rules/:id

## Description
Deletes the monitoring rule.

***

## URL Parameters

None.

***

## Response

**Status:** **204 No Content**

***

## Errors

All known errors cause the resource to return HTTP error code header together with a description of the error.

* **404 Resource not found** - The monitoring rule does not exist.

***

## Example

**Request**

	DELETE v1/monitoring-rules/mr_1