[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / DELETE-metrics-id-observers-id

# Metrics

	DELETE /model/resources/:id

## Description
Delete the specified resource from the knowledge base if it exists

***

## URL Parameters

None.

***

## Response

**Status:** **204 No Content**

***

## Errors

* **404 Resource not found** - The resource does not exist (not implemented yet), answer is 204 even if it doesn't exist

***

## Example
**Request**

	DELETE v1/model/resources/vm1
