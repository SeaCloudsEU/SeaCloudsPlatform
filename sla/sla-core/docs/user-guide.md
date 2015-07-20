# SLA Core User Guide #

* [API Introduction](#rest)
* [Generic operations](#operations)
* [Providers](#providers)
* [Templates](#templates)
* [Agreements](#agreements)
* [Enforcement Jobs](#enforcement-jobs)
* [Violations](#violations)
* [Penalties](#penalties)

## <a name="rest">API Introduction</a> ##

The REST interface to the sla-core system has the following conventions:

* Every entity is created with a POST to the collection url. The body request contains the serialized entity in the 
  format specified in the content-type header. The location header of the response refers to the url of the new 
  allocated resource. The return code is a 201 on success. Templates and agreements have special considerations 
  (see the corresponding section). 
* A query for an individual item is a GET to the url of the resource (collection url + external id). The format of the 
  response is specified in the http header with the accept parameter. The return code is 200. As expected, a not found
  resource returns a 404.
* Any other query is usually a GET to the collection's url, using the GET parameters as the query parameters. The 
  result is a list of entities that match the parameters, despite the actual number of entities. The return code is 
  200, even if the list is empty.
* Any unexpected error processing the request returns a 5xx.
* An entity (or list) is serialized in the response body by default with the format specified in the Content-type 
  header (if specified). The request may have an Accept header, that will be used if the resource allows more than one 
  Content-type.
* Updating an entity involves a PUT request, with the corresponding resource serialized in the body in the format 
  specified in the content-type header. The return code is 200.
* If a query has begin and/or end parameters, the following search is done: `begin <= entity date < end`

## <a name="operations">Generic operations</a> ##

The generic operations of resources are shown below. 
Each particular resource (in following sections) show the supported operations, and any deviation from the behavior 
of generic operations. 

The format of a resource can be modified by a project by using serializers.
 
###GET /{resources}/{uuid} <a name="retrieve"></a>###
Retrieve an entity by its uuid.

**Request**

	GET /resources/{uuid} HTTP/1.1

**Response in XML**

	HTTP/1.1 200 OK
	Content-Type: application/xml
	
	<?xml version="1.0" encoding="UTF-8"?>
	<resource>...</resource>

**Response in JSON**

	HTTP/1.1 200 OK
	Content-Type: application/json
	
	{ ... }

**Usage (for JSON and XML)**

	curl  -H "Accept: application/xml" http://localhost:8080/sla-service/resources/fc923960-03fe-41  
	curl  -H "Accept: application/json" http://localhost:8080/sla-service/resources/fc923960-03fe-41

###GET /resources{?param1=value1&amp;param2=value2...} <a name="search"></a>
<!-- Ampersand Escaped, but it shouldn't be !! -->
Search the resources that fulfills the params. All resources are returned if there are no parameters.

**Request**

	GET /resources?param1=value1 HTTP/1.1

**Response in XML**

	HTTP/1.1 200 OK
	Content-type: application/xml
	
	<?xml version="1.0" encoding="UTF-8"?>
	<resources>
	  <resource>...</resource>
	  <resource>...</resource>
	  <resource>...</resource>
	<resources/>

**Response in JSON**

	HTTP/1.1 200 OK
	Content-type: application/json
	
	[{...},{...},{...}]

**Usage (for JSON and XML)**

	curl [-X GET] -H "Accept: application/xml" localhost:8080/sla-service/resources
	curl [-X GET] -H "Accept: application/xml" localhost:8080/sla-service/resources?name=res-name
    curl [-X GET] -H "Accept: application/json" localhost:8080/sla-service/resources
	curl [-X GET] -H "Accept: application/json" localhost:8080/sla-service/resources?name=res-name

### POST /resources <a name="create"></a>###
Create a new resource. The created resource will be accessed by its uuid. A message will be the usual response.

**Request in XML**

	POST /resources HTTP/1.1
	Content-type: application/xml

	<resource>...</resource>

**Request in JSON**

	POST /resources HTTP/1.1
	Content-type: application/json

	{...}


**Usage (for JSON and XML)**

	curl -H "Accept: application/xml" -H "Content-type: application/xml" -d@<filename> -X POST localhost:8080/sla-service/resources
	curl -H "Accept: application/json" -H "Content-type: application/json" -d@<filename> -X POST localhost:8080/sla-service/resources

### UPDATE /resources/{uuid} <a name="update"></a> ###

Updates an existing resource. The content in the body will overwrite the content of the resource. The uuid in the
body must match the one from the url o not being informed.

**Request in XML**

	PUT /resources/{uuid} HTTP/1.1
	Content-type: application/xml
	
	<resource>...</resource>

**Request in JSON**

	PUT /resources/{uuid} HTTP/1.1
	Content-type: application/xml
	
	{...}

**Response in XML**

	HTTP/1.1 200 Ok
	Content-type: application/xml
	
	<?xml version="1.0" encoding="UTF-8"?>
	<resource>
	    ...
	</resource>

**Response in JSON**

	HTTP/1.1 200 Ok
	Content-type: application/json
	
	{...}

**Usage**

	curl  -H "Accept: application/xml" -H "Content-type: application/xml" -d@<filename> -X PUT localhost:8080/sla-service/resources/{uuid}
	curl  -H "Accept: application/json" -H "Content-type: application/json" -d@<filename> -X PUT localhost:8080/sla-service/resources/{uuid}

### DELETE /resources/{uuid} <a name="delete"></a>###
Deletes an existing resource.

**Request**

	DELETE /providers/{uuid} HTTP/1.1

**Response in XML and JSON**

	HTTP/1.1 200 Ok
	Content-type: application/[xml | json]
	
	... (free text indicating that the resource has been removed)

**Usage (for JSON and XML)**

	curl -H "Accept: application/xml" -X DELETE localhost:8080/sla-service/resources/fc923960-03fe-41
	curl -H "Accept: application/json" -X DELETE localhost:8080/sla-service/resources/fc923960-03fe-41
### Messages
Some of the above mentioned methods might return a message. Messages can be returned as XML or JSON.

**Message Response in XML** 

	Content-type: application/xml
	
	<?xml version="1.0" encoding="UTF-8"?>
	<message code="xxx" elemendId="..." message="..."/>

**Message Request in JSON**

	Content-type: application/json

	{"code":"xxx", "elemendId":..., "message": ...}

---

## <a name="providers">Providers</a> ##
* Provider collection URI: /providers
* Provider URI: /providers/{uuid}
 
A provider is serialized in XML as:

	<provider>
	   <uuid>fc923960-03fe-41eb-8a21-a56709f9370f</uuid>
	   <name>provider-prueba</name>
	</provider>

A provider is serialized in JSON as:

    {"uuid":"fc923960-03fe-41eb-8a21-a56709f9370f",
     "name":"provider-prueba"}


###GET /providers/{uuid}###
Retrieves a specific provider identified by uuid

Error message: 
* 404 is returned when the uuid doesn't exist in the database.

###GET /providers###
Retrieves the list of all providers

###POST /providers###
Creates a provider. The uuid is in the file beeing send

Error message:
* 409 is returned when the uuid or name already exists in the database.

###DELETE /providers/{uuid}###
Removes the provider identified by uuid.

Error message: 

* 404 is returned when the uuid doesn't exist in the database.
* 409 is returned when the provider code is used.

---

## <a name="templates">Templates</a> ##
* Templates collection URI: /templates
* Template URI: /templates/{TemplateId}

The TemplateId matches the TemplateId attribute of wsag:Template element when the template is created. A template is serialized in XML as defined by ws-agreement.

An example of template in XML is:

	<?xml version="1.0" encoding="UTF-8"?>
		<wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" 
		wsag:TemplateId="template012">
		<wsag:Name>ExampleTemplate</wsag:Name>
			<wsag:Context>
				<wsag:AgreementInitiator>provider02</wsag:AgreementInitiator>
				<wsag:ServiceProvider>provider01</wsag:ServiceProvider>
				<wsag:ExpirationTime>2014-03-07T12:00:00+0100</wsag:ExpirationTime>
				<wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>		
				<wsag:TemplateId>template01</wsag:TemplateId>
				<sla:Service xmlns:sla="http://sla.atos.eu">service3</sla:Service>	
			</wsag:Context>
			<wsag:Terms>
				<wsag:All>
				<!-- functional description --> 
					<wsag:ServiceDescriptionTerm wsag:Name="General" wsag:ServiceName="Service0001">A GPS service</wsag:ServiceDescriptionTerm>
					<wsag:ServiceDescriptionTerm wsag:Name="GetCoordsOperation" wsag:ServiceName="GPSService0001">operation to call to get the coords</wsag:ServiceDescriptionTerm>
					<!-- domain specific reference to a service (additional or optional to SDT) --> 
					<wsag:ServiceReference wsag:Name="CoordsRequest" wsag:ServiceName="GPSService0001">
						<wsag:EndpointReference>
							<wsag:Address>http://www.gps.com/coordsservice/getcoords</wsag:Address>
							<wsag:ServiceName>gps:CoordsRequest</wsag:ServiceName>
						</wsag:EndpointReference>
					</wsag:ServiceReference>
					<wsag:ServiceProperties wsag:Name="AvailabilityProperties" wsag:ServiceName="GPS0001">
						<wsag:Variables>
							<wsag:Variable wsag:Name="ResponseTime" wsag:Metric="metric:Duration">
								<wsag:Location>qos:ResponseTime</wsag:Location>
							</wsag:Variable>
						</wsag:Variables>
					</wsag:ServiceProperties>
				<wsag:ServiceProperties	wsag:Name="UsabilityProperties" wsag:ServiceName="GPS0001">
					<wsag:Variables>
						<wsag:Variable wsag:Name="CoordDerivation" wsag:Metric="metric:CoordDerivationMetric">
							<wsag:Location>qos:CoordDerivation</wsag:Location>
						</wsag:Variable>
					</wsag:Variables>
				</wsag:ServiceProperties>
				<!-- statements to offered service level(s) -->
				<wsag:GuaranteeTerm wsag:Name="FastReaction" wsag:Obligated="ServiceProvider">
					<wsag:ServiceScope wsag:ServiceName="GPS0001">
						http://www.gps.com/coordsservice/getcoords
					</wsag:ServiceScope>
					<wsag:QualifyingCondition>
						applied when current time in week working hours
					</wsag:QualifyingCondition>
					<wsag:ServiceLevelObjective>
						<wsag:KPITarget>
							<wsag:KPIName>FastResponseTime</wsag:KPIName>
							<wsag:Target>
								//Variable/@Name="ResponseTime" LOWERTHAN 1 second
							</wsag:Target>
						</wsag:KPITarget>
					</wsag:ServiceLevelObjective>
				</wsag:GuaranteeTerm>
			</wsag:All>
		</wsag:Terms>
	</wsag:Template>

An example of template in JSON is:

	{
		"templateId":"template05",
		"context":{
			"agreementInitiator":"provider02",
			"agreementResponder":null,
			"serviceProvider":"AgreementInitiator",
			"templateId":"template01",
			"service":"service3",
			"expirationTime":"2014-03-07T12:00:00CET"
		},
		"name":"ExampleTemplate",
		"terms":{
			"allTerms":{
				"serviceDescriptionTerm":{
					"name":null,
					"serviceName":null
				},
				"serviceProperties":[
					{"name":null, "serviceName":null, "variableSet":null},
					{"name":null, "serviceName":null, "variableSet":null}
				],
				"guaranteeTerms":[
					{
					"name":"FastReaction",
					"serviceScope":{
							"serviceName":"GPS0001",
							"value":"http://www.gps.com/coordsservice/getcoords"
					},
					"serviceLevelObjetive":{
							"kpitarget":{
							 	"kpiName":"FastResponseTime",
							 	"customServiceLevel":null
							}
						}
					}
				]
			}
		}
	}


###GET /templates/{TemplateId}###
Retrieves a template identified by TemplateId.

Error message: 

* 404 is returned when the uuid doesn't exist in the database.

###GET /templates{?serviceIds,providerId}###

The parameter is:

* serviceIds: string with coma separated values (CSV) with the id's of service that is associated to the template 
* providerId: id of the provider that is offering the template

###POST /templates###
Creates a new template. The file might include a TemplateId or not. In case of not beeing included, a uuid will be assigned.

Error message:
 
* 409 is returned when the uuid already exists in the database.
* 409 is returned when the provider uuid specified in the template doesn't exist in the database.
* 500 when incorrect data has been suplied


###PUT /templates/{TemplateId}###
Updates the template identified by TemplateId. The body might include a TemplateId or not. In case of including a TemplateId in the file, it must match with the one from the url.
 
Error message:

* 409 when the uuid from the url doesn't match with the one from the file or when the system has already an agreement associated 
* 409 when template has agreements associated.  
* 409 provider doesn't exist
* 500 when incorrect data has been suplied

###DELETE /templates/{TemplateId}###
Removes the template identified by TemplateId.

Error message:
* 409 when agreements are still associated to the template
* 404 is returned when the uuid doesn't exist in the database.

---

## <a name="agreements">Agreements</a> ##

* Agreements collection URI: /agreements
* Agreement URI: /agreement/{AgreementId}

The AgreementId matches the AgreementId attribute of wsag:Agreement element when the agreement is created. 
An example of agreement in XML is:

	<?xml version="1.0" encoding="UTF-8"?>
	<wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">
		<wsag:Name>ExampleAgreement</wsag:Name>
		<wsag:Context>
			<wsag:ExpirationTime>2014-03-07T12:00:00+0100</wsag:ExpirationTime>
			<wsag:AgreementInitiator>RandomClient</wsag:AgreementInitiator>
			<wsag:AgreementResponder>provider03</wsag:AgreementResponder>
			<wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
			<wsag:TemplateId>template04</wsag:TemplateId>
			<sla:Service>service01</sla:Service>		
		</wsag:Context>
		<wsag:Terms>
			<wsag:All>
				<wsag:ServiceProperties wsag:Name="NonFunctional" wsag:ServiceName="ServiceName">
					<wsag:Variables>
						<wsag:Variable wsag:Name="ResponseTime" wsag:Metric="xs:double">
							<wsag:Location>qos:ResponseTime</wsag:Location>
						</wsag:Variable>
					</wsag:Variables>
				</wsag:ServiceProperties>
				<wsag:GuaranteeTerm wsag:Name="GTResponseTime">
					<wsag:ServiceScope wsag:ServiceName="ServiceName" />
					<wsag:ServiceLevelObjective>
						<wsag:KPITarget>
							<wsag:KPIName>ResponseTime</wsag:KPIName>
							<wsag:CustomServiceLevel>{"constraint" : "ResponseTime LT 100"}</wsag:CustomServiceLevel>
						</wsag:KPITarget>
					</wsag:ServiceLevelObjective>
				</wsag:GuaranteeTerm>
			</wsag:All>
		</wsag:Terms>
	</wsag:Agreement>


An example of agreement in JSON is:

	{
		"agreementId":"agreement07",
		"name":"ExampleAgreement",
		"context":{
			"agreementInitiator":"client-prueba",
			"expirationTime":"2014-03-07T12:00:00+0100",
			"templateId":"template02",
			"service":"service5",
			"serviceProvider":"AgreementResponder",
			"agreementResponder":"provider03"
		},
		"terms": {
			"allTerms":{
				"serviceDescriptionTerm":null,
				"serviceProperties":[
					{
						"name":"ServiceProperties",
						"serviceName":"ServiceName",
						"variableSet":{
						"variables":[
							{ "name":"metric1","metric":"xs:double","location":"metric1"},
							{ "name":"metric2","metric":"xs:double","location":"metric2"},
							{ "name":"metric3","metric":"xs:double","location":"metric3"},
							{ "name":"metric4","metric":"xs:double","location":"metric4"}
						]
					}
				}
			],
			"guaranteeTerms":[
				{
					"name":"GTMetric1",
					"serviceScope":{"serviceName":"ServiceName","value":""},
					"serviceLevelObjetive":{
						"kpitarget":{
							"kpiName":"metric1",
							"customServiceLevel":"{\"constraint\" : \"metric1 BETWEEN (0.05, 1)\"}"
						}
					}
				},{
					"name":"GTMetric2",
					"serviceScope":{"serviceName":"ServiceName","value":""},
					"serviceLevelObjetive":{
						"kpitarget":{
					 		"kpiName":"metric2",
					 		"customServiceLevel":"{\"constraint\" : \"metric2 BETWEEN (0.1, 1)\"}"
						}
					}
				},{
					"name":"GTMetric3",
					"serviceScope":{"serviceName":"ServiceName","value":""},
					"serviceLevelObjetive":{
						"kpitarget":{
					 		"kpiName":"metric3",
					 		"customServiceLevel":"{\"constraint\" : \"metric3 BETWEEN (0.15, 1)\"}"
						}
					}
				},{
					"name":"GTMetric4",
					"serviceScope":{"serviceName":"ServiceName","value":""},
					"serviceLevelObjetive":{
						"kpitarget":{
					 		"kpiName":"metric4",
					 		"customServiceLevel":"{\"constraint\" : \"metric4 BETWEEN (0.2, 1)\"}"
						}
					}
				}
			]
		}
	}

###GET /agreements/{AgreementId}###
Retrieves an agreement identified by AgreementId.

Error message: 

* 404 is returned when the uuid doesn't exist in the database.


###GET /agreements/###
Retrieves the list of all agreements.

###GET /agreements{?consumerId,providerId,templateId,active}###

The parameters are:

* consumerId: uuid of the consumer (value of Context/AgreementInitiator if Context/ServiceProvider equals "AgreementResponder"). 
* providerId: uuid of the provider (value of Context/AgreementResponder if Context/ServiceProvider equals "AgreementResponder")
* templateId: uuid of the template the agreement is based on.
* active: boolean value (value in {1,true,0,false}); if true, agreements currently enforced are returned.

###GET /agreementsPerTemplateAndConsumer{?consumerId,templateUUID}###

The parameters are:

* consumerId: uuid of the consumer (value of Context/AgreementInitiator if Context/ServiceProvider equals "AgreementResponder"). 
* templateUUID: uuid of the template in wicht the agreement is based 

###POST /agreements###
Creates a new agreement. The body might include a AgreementId or not. In case of not being included, a uuid will be assigned. A disabled enforcement job is automatically created.

Error message:

* 409 is returned when the uuid already exists in the database
* 409 is returned when the provider uuid specified in the agreement doesn't exist in the database
* 409 is returned when the template uuid specified in the agreement doesn't exist in the database
* 500 when incorrect data has been suplied.

###DELETE /agreements/{AgreementId}###
Removes the agreement identified by AgreementId.

Error message:

* 404 is returned when the uuid doesn't exist in the database



###GET /agreements/active###
Returns the list of active agreements.

###GET /agreements/{AgreementId}/context###
Only the context from the agreement identified by AgreementId is returned.

Error message:

* 404 is returned when the uuid doesn't exist in the database
* 500 when the data agreement was recorded incorrectly and the data cannot be supplied

**Request in XML**

	GET -H "Accept: application/xml" /agreements/{agreement-id}/context HTTP/1.1

**Request in JSON**

	GET -H "Accept: application/json" /agreements/{agreement-id}/context HTTP/1.1

**Response in XML**

	HTTP/1.1 200 OK
	
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<wsag:Context xmlns:sla="http://sla.atos.eu" xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement">
		<wsag:AgreementInitiator>RandomClient</wsag:AgreementInitiator>
		<wsag:AgreementResponder>provider02</wsag:AgreementResponder>
		<wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
		<wsag:ExpirationTime>2014-03-07T12:00:00CET</wsag:ExpirationTime>
		<wsag:TemplateId>template02</wsag:TemplateId>
		<sla:Service>service02</sla:Service>
	</wsag:Context>

**Response in JSON**

	HTTP/1.1 200 OK
	
	{"AgreementInitiator":"RandomClient",
	 "AgreementResponder":"provider02",
	 "ServiceProvider":"AgreementResponder",
	 "ExpirationTime":"2014-03-07T12:00:00CET",
	 "TemplateId":"template02",
	 "Service":"service02"}


**Usage (for JSON and XML)**

    curl -H "Accept: application/xml" http://localhost:8080/sla-service/agreements/agreement01/context
    curl -H "Accept: application/json" http://localhost:8080/sla-service/agreements/agreement01/context


###GET /agreements/{AgreementId}/guaranteestatus###
Gets the information of the status of the different Guarantee Terms of an agreement.

There are three available states: NON_DETERMINED, FULFILLED, VIOLATED.

Error message:

* 404 is returned when the uuid doesn't exist in the database


**Request in XML**

	GET -H "Accept: application/xml" /agreements/{agreement-id}/guaranteestatus
	HTTP/1.1

**Request in JSON**

	GET -H "Accept: application/json" /agreements/{agreement-id}/guaranteestatus
	HTTP/1.1

**Response in XML**

	HTTP/1.1 200 OK
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<guaranteestatus AgreementId="agreement02" value="FULFILLED">
		<guaranteetermstatus name="GTResponseTime" value="FULFILLED"/>
		<guaranteetermstatus name="GTPerformance" value="FULFILLED"/>
	</guaranteestatus>
	
**Response in JSON**

	HTTP/1.1 200 OK
	{"AgreementId":"agreement02",
	 "guaranteestatus":"FULFILLED",
	 "guaranteeterms":
		[{"name":"GTResponseTime", "status":"FULFILLED"},
		 {"name":"GTPerformance", "status":"FULFILLED"}]
	 }

**Usage (for JSON and XML)**

    curl -H "Accept: application/xml" http://localhost:8080/sla-service/agreements/agreement01/guaranteestatus
    curl -H "Accept: application/json" http://localhost:8080/sla-service/agreements/agreement01/guaranteestatus

---

## <a name="enforcement-jobs">Enforcement Jobs</a> ##
An enforcement job is the entity which starts the enforcement of the agreement guarantee terms. An agreement can be 
enforced only if an enforcement job, linked with it, has been previously created and started. An enforcement job
is automatically created when an agreement is created, so there is no need to create one to start an enforcement.

* Enforcement jobs collection URI: /enforcements
* Enforcement job URI: /enforcements/{AgreementId}

An enforcement job is serialized in XML as:

	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<enforcement_job>
		<agreement_id>agreement02</agreement_id>
		<enabled>true</enabled>
		<last_executed>2014-08-13T10:01:01CEST</last_executed>
	</enforcement_job>

An enforcement job is serialized in JSON as:

	{"enabled":true,
	 "agreement_id":"agreement02",
	 "last_executed":"2014-08-13T10:01:01CEST"}

###GET /enforcements/{AgreementId}###
Retrieves an enforcement job identified by AgreementId.

Error message:

* 404 is returned when the uuid doesn't exist in the database


###GET /enforcements###
Retrieves the list of all enforcement job.

###POST /enforcements###
Creates and enforcement job. Not required anymore. The enforcement job is automatically generated when an agreement 
is created. 

Error message:

* 409 is returned when an enforcement with that uuid already exists in the database
* 404 is returnes when no agreement with uuid exists in the database

###PUT /enforcements/{AgreementId}/start###
Starts an enforcement job.

Error message:

* 403 is returned when it was not possible to start the job


**Request**

	PUT /enforcements/{agreement-id}/start HTTP/1.1

**Response in XML and JSON**

	HTTP/1.1 200 Ok
	Content-type: application/[xml | json]
	
	The enforcement job with agreement-uuid {agreement-id} has started

**Usage (for JSON and XML)**

	curl -H "Accept: application/xml" -X PUT localhost:8080/sla-service/enforcements/fc923960-03fe-41/start
	curl -H "Accept: application/json" -X PUT localhost:8080/sla-service/enforcements/fc923960-03fe-41/start


###PUT /enforcements/{AgreementId}/stop###
Stops an enforcement job

Error message:

* 403 is returned when it was not possible to start the job 


**Request**

	PUT /enforcements/{agreement-id}/stop HTTP/1.1

**Response in XML and JSON**

	HTTP/1.1 200 OK
	Content-type: application/[xml | json]
	
	The enforcement job with agreement-uuid {agreement-id} has stoppped

**Usage (for JSON and XML)**

	curl -H "Accept: application/xml" -X PUT localhost:8080/sla-service/enforcements/fc923960-03fe-41/stop
	curl -H "Accept: application/json" -X PUT localhost:8080/sla-service/enforcements/fc923960-03fe-41/stop

---

## <a name="violations">Violations</a> ##

* Violations collection URI: /violations
* Violation URI: /violations/{uuid}

A violations is serialized in XML as:

	<violation>
		<uuid>ce0e148f-dfac-4492-bb26-ad2e9a6965ec</uuid>
		<contract_uuid>agreement04</contract_uuid>
		<service_scope></service_scope>
		<metric_name>Performance</metric_name>
		<datetime>2014-08-13T10:01:01CEST</datetime>
		<actual_value>0.09555700123360344</actual_value>
	</violation>

A violations is serialized in JSON as:

	{"uuid":"e431d68b-86ac-4c72-a6db-939e949b6c1",
	 "datetime":"2014-08-13T10:01:01CEST",
	 "contract_uuid":"agreement07",
	 "service_name":"ServiceName",
	 "service_scope":"",
	 "metric_name":"time",
	 "actual_value":"0.021749629938806803"}

###GET /violations/{uuid}###
Retrieves information from a violation identified by the uuid.

###GET /violations{?agreementId,guaranteeTerm,providerId,begin,end}###

Parameters:

* agreementId: if specified, search the violations of the agreement with this agreementId,
* guaranteeTerm: if specified, search the violations of the guarantee term with this name (GuaranteeTerm[@name]),
* providerId: if specified, search the violations raised by this provider.
* begin: if specified, set a lower limit of date of violations to search. Date format: yyyy-MM-dd'T'HH:mm:ss
* end: if specified, set an upper limit of date of violations to search. Date format: yyyy-MM-dd'T'HH:mm:ss

Error message:

* 404 when erroneous data is provided in the call 

---

## <a name="penalties">Penalties</a> ##

* Penalties collection URI: /penalties
* Penalty URI: /penalties/{uuid}

A penalty is serialized in XML as:

	<penalty xmlns:sla="http://sla.atos.eu" xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement">
		<uuid>ec7fd8ec-d917-49a2-ad80-80ff9aa8269c</uuid>
		<agreement>agreement-a</agreement>
		<datetime>2015-01-21T18:42:00CET</datetime>
		<definition type="discount" expression="35" unit="%" validity="P1D"/>
	</penalty>	

A penalty is serialized in JSON as:

	{
		"uuid":"bfc4bc66-d647-453a-b813-d130f6116daf",
		"datetime":"2015-01-21T18:49:00CET",
		"definition":{
			"type":"discount",
			"expression":"35",
			"unit":"%",
			"validity":"P1D"
		},
		"agreement":"agreement-a"
	}

###GET /penalties/{uuid}###
Retrieves information from a penalty identified by the uuid.

###GET /penalties{?agreementId,guaranteeTerm,begin,end}###

Parameters:

* agreementId: if specified, search the penalties of the agreement with this agreementId,
* guaranteeTerm: if specified, search the penalties of the guarantee term with this name (GuaranteeTerm[@name]),
* begin: if specified, set a lower limit of date of penalties to search,
* end: if specified, set an upper limit of date of penalties to search.

Error message:

* 404 when erroneous data is provided in the call 