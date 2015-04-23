[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / POST-metrics-id-observers

# Model

	PUT /model/resources

## Description
Upload a new model on the knowledge base. If a previous model existed, the existing model is replaced. 
A new resource is created for each resource in the uploaded model with the id specified.
The Java representation of the model is available [here](https://github.com/deib-polimi/modaclouds-monitoring-manager/blob/master/src/main/java/it/polimi/modaclouds/monitoring/monitoring_manager/server/Model.java).
The Java representation of resources are available [here](https://github.com/deib-polimi/modaclouds-qos-models/tree/master/src/main/java/it/polimi/modaclouds/qos_models/monitoring_ontology).
A graphical representation of resources and their relations is available [here](https://github.com/deib-polimi/modaclouds-qos-models/blob/master/doc/user-manual.md#the-monitoring-ontology).

***

## URL Parameters

None

***

## Data Parameters

A JSON containing the new model that must be uploaded on the knowledge base

***

## Response

**Status:** **204 No Content**


***

## Errors

* **505 error while uploading the model** - The model was not valid

***

## Example
**Request**

	PUT v1/model/resources
	
``` json
{
  "cloudProviders": [
    {
      "id": "amazon", 
      "type": "IaaS"
    }
  ], 
  "internalComponents": [
    {
      "id": "mic1", 
      "providedMethods": [
        "mic1-register", 
        "mic1-answerQuestions", 
        "mic1-saveAnswers"
      ], 
      "requiredComponents": [
        "frontend1"
      ], 
      "type": "Mic"
    },
    {
      "id": "mic2", 
      "providedMethods": [
        "mic2-register", 
        "mic2-answerQuestions", 
        "mic2-saveAnswers"
      ], 
      "requiredComponents": [
        "frontend2"
      ], 
      "type": "Mic"
    }
  ], 
  "methods": [
    {
      "id": "mic1-answerQuestions", 
      "type": "answerQuestions"
    }, 
    {
      "id": "mic1-saveAnswers", 
      "type": "saveAnswers"
    }, 
    {
      "id": "mic1-register", 
      "type": "register"
    },
    {
      "id": "mic2-answerQuestions", 
      "type": "answerQuestions"
    }, 
    {
      "id": "mic2-saveAnswers", 
      "type": "saveAnswers"
    }, 
    {
      "id": "mic2-register", 
      "type": "register"
    }
  ], 
  "vMs": [
    {
      "cloudProvider": "amazon", 
      "id": "frontend1", 
      "numberOfCPUs": 2, 
      "type": "Frontend"
    },
    {
      "cloudProvider": "amazon", 
      "id": "frontend2", 
      "numberOfCPUs": 2, 
      "type": "Frontend"
    }
  ]
}
```

**Response**

	Status: 204 No Content
