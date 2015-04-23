[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / GET-metrics-id-observers-id

# Metrics

	GET /model/resources/:id

## Description
Get the specified resource from the knowledge base

***

## URL Parameters

None.

***

## Response

**Status:** **200 OK**

**Body:** A JSON representation of the resource

***

## Errors

* **404 Resource not found** - The resource does not exist

***

## Example
**Request**

	GET v1/model/resources/mic1
	
**Response**

	Status: 200 OK

``` json
{"requiredComponents":["frontend1"],"providedMethods":["mic1-register","mic1-answerQuestions","mic1-saveAnswers"],"type":"Mic","id":"mic1"}
```
