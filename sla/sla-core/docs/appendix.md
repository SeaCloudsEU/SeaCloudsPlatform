#Appendix REST API examples#

##Providers<a name="providers"></a>##

###Create a provider###

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/providers

	POST /sla-service/providers HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 117
	<?xml version="1.0" encoding="UTF-8"?><provider>    <uuid>provid
	er01</uuid>    <name>provider01name</name></provider>

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/providers/provider01
	Content-Type: application/xml
	Content-Length: 254
	Date: Mon, 26 Jan 2015 12:21:29 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The provider has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/sla-service/providers/provider01" elementId="provider01"/>

---

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider02.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/providers

	POST /sla-service/providers HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 117
	<?xml version="1.0" encoding="UTF-8"?><provider>    <uuid>provid
	er02</uuid>    <name>provider02name</name></provider>

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/providers/provider02
	Content-Type: application/xml
	Content-Length: 254
	Date: Mon, 26 Jan 2015 12:21:29 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The provider has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/sla-service/providers/provider02" elementId="provider02"/>

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider03.json -X POST -H Content-type:application/json -H Accept:application/json http://localhost:8080/sla-service/providers

	POST /sla-service/providers HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/json
	Accept:application/json
	Content-Length: 45
	{"uuid":"provider03","name":"provider03name"}

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/providers/provider03
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:29 GMT
	c2
	{"code":201,"message":"The provider has been stored successfully
	 in the SLA Repository Database. It has location http://localhos
	t:8080/sla-service/providers/provider03","elementId":"provider03
	"}

---

Provider exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider02.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/providers

	POST /sla-service/providers HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 117
	<?xml version="1.0" encoding="UTF-8"?><provider>    <uuid>provid
	er02</uuid>    <name>provider02name</name></provider>

	HTTP/1.1 409 Conflict
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 181
	Date: Mon, 26 Jan 2015 12:21:31 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Provider with id:provider02 or name:provider02
	name already exists in the SLA Repository Database"/>

###Get a provider###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/providers/provider02?

	GET /sla-service/providers/provider02? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 126
	Date: Mon, 26 Jan 2015 12:21:31 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><provider
	><uuid>provider02</uuid><name>provider02name</name></provider>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/providers/provider02?

	GET /sla-service/providers/provider02? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:31 GMT
	2d
	{"uuid":"provider02","name":"provider02name"}

---

Provider not exists.
Accept: 404

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/providers/notexists?

	GET /sla-service/providers/notexists? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 156
	Date: Mon, 26 Jan 2015 12:21:32 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="404" message="There is no provider with uuid notexists in th
	e SLA Repository Database"/>

###Get all the providers###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/providers?

	GET /sla-service/providers? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 291
	Date: Mon, 26 Jan 2015 12:21:32 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><provider
	s><provider><uuid>provider01</uuid><name>provider01name</name></
	provider><provider><uuid>provider02</uuid><name>provider02name</
	name></provider><provider><uuid>provider03</uuid><name>provider0
	3name</name></provider></providers>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/providers?

	GET /sla-service/providers? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:33 GMT
	8b
	[{"uuid":"provider01","name":"provider01name"},{"uuid":"provider
	02","name":"provider02name"},{"uuid":"provider03","name":"provid
	er03name"}]

###Delete a provider###



	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/sla-service/providers/provider03

	DELETE /sla-service/providers/provider03 HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:33 GMT
	3a
	The provider with uuid provider03 was deleted successfully

---

Provider not exists


	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/sla-service/providers/notexists

	DELETE /sla-service/providers/notexists HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:34 GMT
	9e
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<error c
	ode="404" message="There is no provider with uuid notexists in t
	he SLA Repository Database"/>.

##Templates<a name="templates"></a>##

###Create a template###

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/templates

	POST /sla-service/templates HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2535

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/templates/template01
	Content-Type: application/xml
	Content-Length: 254
	Date: Mon, 26 Jan 2015 12:21:34 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The template has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/sla-service/templates/template01" elementId="template01"/>

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/template02.json -X POST -H Content-type:application/json -H Accept:application/json http://localhost:8080/sla-service/templates

	POST /sla-service/templates HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/json
	Accept:application/json
	Content-Length: 718
	{"templateId":"template02","context":{"agreementInitiator":"prov
	ider02","agreementResponder":null,"serviceProvider":"AgreementIn
	itiator","templateId":"template02","service":"service02","expira
	tionTime":"2014-03-07T12:00:00CET"},"name":"ExampleTemplate","te
	rms":{"allTerms":{"serviceDescriptionTerm":{"name":null,"service
	Name":null},"serviceProperties":[{"name":null,"serviceName":null
	,"variableSet":null},{"name":null,"serviceName":null,"variableSe
	t":null}],"guaranteeTerms":[{"name":"FastReaction","serviceScope
	":{"serviceName":"GPS0001","value":"               http://www.gp
	s.com/coordsservice/getcoords            "},"serviceLevelObjetiv
	e":{"kpitarget":{"kpiName":"FastResponseTime","customServiceLeve
	l":null}}}]}}}

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/templates/template02
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:35 GMT
	c2
	{"code":201,"message":"The template has been stored successfully
	 in the SLA Repository Database. It has location http://localhos
	t:8080/sla-service/templates/template02","elementId":"template02
	"}

---

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template02b.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/templates

	POST /sla-service/templates HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2537

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/templates/template02
	b
	Content-Type: application/xml
	Content-Length: 256
	Date: Mon, 26 Jan 2015 12:21:35 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The template has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/sla-service/templates/template02b" elementId="template02b"/>

---

Template exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/templates

	POST /sla-service/templates HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2535

	HTTP/1.1 409 Conflict
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 157
	Date: Mon, 26 Jan 2015 12:21:35 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Element with id:template01 already exists in t
	he SLA Repository Database"/>

---

Linked provider not exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template03.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/templates

	POST /sla-service/templates HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2535

	HTTP/1.1 409 Conflict
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 144
	Date: Mon, 26 Jan 2015 12:21:37 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Provider with UUID provider03 doesn't exist in
	 the database"/>

###Get a template###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/templates/template02?

	GET /sla-service/templates/template02? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 1001
	Date: Mon, 26 Jan 2015 12:21:37 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><wsag:Tem
	plate wsag:TemplateId="template02" xmlns:wsag="http://www.ggf.or
	g/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu"><wsag:
	Name>ExampleTemplate</wsag:Name><wsag:Context><wsag:AgreementIni
	tiator>provider02</wsag:AgreementInitiator><wsag:ServiceProvider
	>AgreementInitiator</wsag:ServiceProvider><wsag:ExpirationTime>2
	014-03-07T12:00:00CET</wsag:ExpirationTime><wsag:TemplateId>temp
	late02</wsag:TemplateId><sla:Service>service02</sla:Service></ws
	ag:Context><wsag:Terms><wsag:All><wsag:ServiceDescriptionTerm/><
	wsag:ServiceProperties/><wsag:ServiceProperties/><wsag:Guarantee
	Term wsag:Name="FastReaction"><wsag:ServiceScope wsag:ServiceNam
	e="GPS0001">               http://www.gps.com/coordsservice/getc
	oords            </wsag:ServiceScope><wsag:ServiceLevelObjective
	><wsag:KPITarget><wsag:KPIName>FastResponseTime</wsag:KPIName></
	wsag:KPITarget></wsag:ServiceLevelObjective></wsag:GuaranteeTerm
	></wsag:All></wsag:Terms></wsag:Template>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/templates/template02?

	GET /sla-service/templates/template02? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Content-Length: 770
	Date: Mon, 26 Jan 2015 12:21:37 GMT
	{"templateId":"template02","context":{"agreementInitiator":"prov
	ider02","agreementResponder":null,"serviceProvider":"AgreementIn
	itiator","expirationTime":"2014-03-07T12:00:00CET","templateId":
	"template02","service":"service02"},"name":"ExampleTemplate","te
	rms":{"allTerms":{"serviceDescriptionTerm":{"name":null,"service
	Name":null},"serviceProperties":[{"name":null,"serviceName":null
	,"variableSet":null},{"name":null,"serviceName":null,"variableSe
	t":null}],"guaranteeTerms":[{"name":"FastReaction","serviceScope
	":{"serviceName":"GPS0001","value":"               http://www.gp
	s.com/coordsservice/getcoords            "},"qualifyingCondition
	":null,"businessValueList":null,"serviceLevelObjetive":{"kpitarg
	et":{"kpiName":"FastResponseTime","customServiceLevel":null}}}]}
	}}

---

Template not exists.
Accept: 404

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/templates/notexists?

	GET /sla-service/templates/notexists? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 154
	Date: Mon, 26 Jan 2015 12:21:38 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="404" message="There is no template with id notexists in the 
	SLA Repository Database"/>

###Get all the templates###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/templates?

	GET /sla-service/templates? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 6020
	Date: Mon, 26 Jan 2015 12:21:38 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><template
	s><wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-ag
	reement" xmlns:sla="http://sla.atos.eu" wsag:TemplateId="templat
	e01">.<wsag:Name>ExampleTemplate01</wsag:Name>.<wsag:Context>.  
	  <wsag:AgreementResponder>provider01</wsag:AgreementResponder>.
	.<wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
	..<wsag:ExpirationTime>2014-03-07T12:00:00+0100</wsag:Expiration
	Time>..<sla:Service xmlns:sla="http://sla.atos.eu">service02</sl
	a:Service>.  .</wsag:Context>.<wsag:Terms>..<wsag:All>...<wsag:S
	erviceDescriptionTerm wsag:Name="SDTName1" wsag:ServiceName="Ser
	viceName">....DSL expression...</wsag:ServiceDescriptionTerm>...
	<wsag:ServiceDescriptionTerm wsag:Name="SDTName2" wsag:ServiceNa
	me="ServiceName">....DSL expression...</wsag:ServiceDescriptionT
	erm>......<wsag:ServiceProperties wsag:Name="NonFunctional" wsag
	:ServiceName="ServiceName">....<wsag:Variables>.....<wsag:Variab
	le wsag:Name="ResponseTime" wsag:Metric="xs:double">......<wsag:
	Location>qos:ResponseTime</wsag:Location>.....</wsag:Variable>..
	...<wsag:Variable wsag:Name="Performance" wsag:Metric="xs:double
	">......<wsag:Location>qos:Performance</wsag:Location>.....</wsa
	g:Variable>....</wsag:Variables>...</wsag:ServiceProperties>...<
	wsag:GuaranteeTerm wsag:Name="GT_ResponseTime">....<wsag:Service
	Scope>ServiceName</wsag:ServiceScope>....<wsag:ServiceLevelObjec
	tive>.....<wsag:KPITarget>......<wsag:KPIName>ResponseTime</wsag
	:KPIName>......<wsag:CustomServiceLevel>{"constraint" : "Respons
	eTime LT qos:ResponseTime"}</wsag:CustomServiceLevel>.....</wsag
	:KPITarget>....</wsag:ServiceLevelObjective>...</wsag:GuaranteeT
	erm>...<wsag:GuaranteeTerm wsag:Name="GT_Performance">....<wsag:
	ServiceScope>ServiceName</wsag:ServiceScope>....<wsag:ServiceLev
	elObjective>.....<wsag:KPITarget>......<wsag:KPIName>Performance
	</wsag:KPIName>......<wsag:CustomServiceLevel>{"constraint" : "P
	erformance GT qos:Performance"}</wsag:CustomServiceLevel>.....</
	wsag:KPITarget>....</wsag:ServiceLevelObjective>....<wsag:Busine
	ssValueList>.....<wsag:Importance>3</wsag:Importance>.....<wsag:
	Penalty>......<wsag:AssessmentInterval>.......<wsag:Count>10</ws
	ag:Count>......</wsag:AssessmentInterval>......<wsag:ValueUnit>E
	UR</wsag:ValueUnit>......<wsag:ValueExpression>99</wsag:ValueExp
	ression>.....</wsag:Penalty>..........<wsag:Reward></wsag:Reward
	>.....<wsag:Preference></wsag:Preference>.....<wsag:CustomBusine
	ssValue></wsag:CustomBusinessValue>....</wsag:BusinessValueList>
	...</wsag:GuaranteeTerm>..</wsag:All>.</wsag:Terms></wsag:Templa
	te><wsag:Template wsag:TemplateId="template02" xmlns:wsag="http:
	//www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.ato
	s.eu"><wsag:Name>ExampleTemplate</wsag:Name><wsag:Context><wsag:
	AgreementInitiator>provider02</wsag:AgreementInitiator><wsag:Ser
	viceProvider>AgreementInitiator</wsag:ServiceProvider><wsag:Expi
	rationTime>2014-03-07T12:00:00CET</wsag:ExpirationTime><wsag:Tem
	plateId>template02</wsag:TemplateId><sla:Service>service02</sla:
	Service></wsag:Context><wsag:Terms><wsag:All><wsag:ServiceDescri
	ptionTerm/><wsag:ServiceProperties/><wsag:ServiceProperties/><ws
	ag:GuaranteeTerm wsag:Name="FastReaction"><wsag:ServiceScope wsa
	g:ServiceName="GPS0001">               http://www.gps.com/coords
	service/getcoords            </wsag:ServiceScope><wsag:ServiceLe
	velObjective><wsag:KPITarget><wsag:KPIName>FastResponseTime</wsa
	g:KPIName></wsag:KPITarget></wsag:ServiceLevelObjective></wsag:G
	uaranteeTerm></wsag:All></wsag:Terms></wsag:Template><wsag:Templ
	ate xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmln
	s:sla="http://sla.atos.eu" wsag:TemplateId="template02b">.<wsag:
	Name>ExampleTemplate02b</wsag:Name>.<wsag:Context>.    <wsag:Agr
	eementResponder>provider02</wsag:AgreementResponder>..<wsag:Serv
	iceProvider>AgreementResponder</wsag:ServiceProvider>..<wsag:Exp
	irationTime>2014-03-07T12:00:00+0100</wsag:ExpirationTime>..<sla
	:Service xmlns:sla="http://sla.atos.eu">service02</sla:Service>.
	  .</wsag:Context>.<wsag:Terms>..<wsag:All>...<wsag:ServiceDescr
	iptionTerm wsag:Name="SDTName1" wsag:ServiceName="ServiceName">.
	...DSL expression...</wsag:ServiceDescriptionTerm>...<wsag:Servi
	ceDescriptionTerm wsag:Name="SDTName2" wsag:ServiceName="Service
	Name">....DSL expression...</wsag:ServiceDescriptionTerm>......<
	wsag:ServiceProperties wsag:Name="NonFunctional" wsag:ServiceNam
	e="ServiceName">....<wsag:Variables>.....<wsag:Variable wsag:Nam
	e="ResponseTime" wsag:Metric="xs:double">......<wsag:Location>qo
	s:ResponseTime</wsag:Location>.....</wsag:Variable>.....<wsag:Va
	riable wsag:Name="Performance" wsag:Metric="xs:double">......<ws
	ag:Location>qos:Performance</wsag:Location>.....</wsag:Variable>
	....</wsag:Variables>...</wsag:ServiceProperties>...<wsag:Guaran
	teeTerm wsag:Name="GT_ResponseTime">....<wsag:ServiceScope>Servi
	ceName</wsag:ServiceScope>....<wsag:ServiceLevelObjective>.....<
	wsag:KPITarget>......<wsag:KPIName>ResponseTime</wsag:KPIName>..
	....<wsag:CustomServiceLevel>{"constraint" : "ResponseTime LT qo
	s:ResponseTime"}</wsag:CustomServiceLevel>.....</wsag:KPITarget>
	....</wsag:ServiceLevelObjective>...</wsag:GuaranteeTerm>...<wsa
	g:GuaranteeTerm wsag:Name="GT_Performance">....<wsag:ServiceScop
	e>ServiceName</wsag:ServiceScope>....<wsag:ServiceLevelObjective
	>.....<wsag:KPITarget>......<wsag:KPIName>Performance</wsag:KPIN
	ame>......<wsag:CustomServiceLevel>{"constraint" : "Performance 
	GT qos:Performance"}</wsag:CustomServiceLevel>.....</wsag:KPITar
	get>....</wsag:ServiceLevelObjective>....<wsag:BusinessValueList
	>.....<wsag:Importance>3</wsag:Importance>.....<wsag:Penalty>...
	...<wsag:AssessmentInterval>.......<wsag:Count>10</wsag:Count>..
	....</wsag:AssessmentInterval>......<wsag:ValueUnit>EUR</wsag:Va
	lueUnit>......<wsag:ValueExpression>99</wsag:ValueExpression>...
	..</wsag:Penalty>..........<wsag:Reward></wsag:Reward>.....<wsag
	:Preference></wsag:Preference>.....<wsag:CustomBusinessValue></w
	sag:CustomBusinessValue>....</wsag:BusinessValueList>...</wsag:G
	uaranteeTerm>..</wsag:All>.</wsag:Terms></wsag:Template></templa
	tes>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/templates?

	GET /sla-service/templates? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Content-Length: 3010
	Date: Mon, 26 Jan 2015 12:21:39 GMT
	[{"templateId":"template01","context":{"agreementInitiator":null
	,"agreementResponder":"provider01","serviceProvider":"AgreementR
	esponder","expirationTime":"2014-03-07T12:00:00CET","templateId"
	:null,"service":"service02"},"name":"ExampleTemplate01","terms":
	{"allTerms":{"serviceDescriptionTerm":{"name":"SDTName2","servic
	eName":"ServiceName"},"serviceProperties":[{"name":"NonFunctiona
	l","serviceName":"ServiceName","variableSet":null}],"guaranteeTe
	rms":[{"name":"GT_ResponseTime","serviceScope":{"serviceName":nu
	ll,"value":"ServiceName"},"qualifyingCondition":null,"businessVa
	lueList":null,"serviceLevelObjetive":{"kpitarget":{"kpiName":"Re
	sponseTime","customServiceLevel":"{\"constraint\" : \"ResponseTi
	me LT qos:ResponseTime\"}"}}},{"name":"GT_Performance","serviceS
	cope":{"serviceName":null,"value":"ServiceName"},"qualifyingCond
	ition":null,"businessValueList":{"customBusinessValue":[{"count"
	:1,"duration":"1970-01-01T00:00:00.000+0000","penalties":[]}],"i
	mportance":3},"serviceLevelObjetive":{"kpitarget":{"kpiName":"Pe
	rformance","customServiceLevel":"{\"constraint\" : \"Performance
	 GT qos:Performance\"}"}}}]}}},{"templateId":"template02","conte
	xt":{"agreementInitiator":"provider02","agreementResponder":null
	,"serviceProvider":"AgreementInitiator","expirationTime":"2014-0
	3-07T12:00:00CET","templateId":"template02","service":"service02
	"},"name":"ExampleTemplate","terms":{"allTerms":{"serviceDescrip
	tionTerm":{"name":null,"serviceName":null},"serviceProperties":[
	{"name":null,"serviceName":null,"variableSet":null},{"name":null
	,"serviceName":null,"variableSet":null}],"guaranteeTerms":[{"nam
	e":"FastReaction","serviceScope":{"serviceName":"GPS0001","value
	":"               http://www.gps.com/coordsservice/getcoords    
	        "},"qualifyingCondition":null,"businessValueList":null,"
	serviceLevelObjetive":{"kpitarget":{"kpiName":"FastResponseTime"
	,"customServiceLevel":null}}}]}}},{"templateId":"template02b","c
	ontext":{"agreementInitiator":null,"agreementResponder":"provide
	r02","serviceProvider":"AgreementResponder","expirationTime":"20
	14-03-07T12:00:00CET","templateId":null,"service":"service02"},"
	name":"ExampleTemplate02b","terms":{"allTerms":{"serviceDescript
	ionTerm":{"name":"SDTName2","serviceName":"ServiceName"},"servic
	eProperties":[{"name":"NonFunctional","serviceName":"ServiceName
	","variableSet":null}],"guaranteeTerms":[{"name":"GT_ResponseTim
	e","serviceScope":{"serviceName":null,"value":"ServiceName"},"qu
	alifyingCondition":null,"businessValueList":null,"serviceLevelOb
	jetive":{"kpitarget":{"kpiName":"ResponseTime","customServiceLev
	el":"{\"constraint\" : \"ResponseTime LT qos:ResponseTime\"}"}}}
	,{"name":"GT_Performance","serviceScope":{"serviceName":null,"va
	lue":"ServiceName"},"qualifyingCondition":null,"businessValueLis
	t":{"customBusinessValue":[{"count":1,"duration":"1970-01-01T00:
	00:00.000+0000","penalties":[]}],"importance":3},"serviceLevelOb
	jetive":{"kpitarget":{"kpiName":"Performance","customServiceLeve
	l":"{\"constraint\" : \"Performance GT qos:Performance\"}"}}}]}}
	}]

###Delete a template###



	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/sla-service/templates/template02b

	DELETE /sla-service/templates/template02b HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:39 GMT
	37
	Template with uuid template02b was deleted successfully

---

Template not exists


	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/sla-service/templates/notexists

	DELETE /sla-service/templates/notexists HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:40 GMT
	9e
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<error c
	ode="404" message="There is no template with uuid notexists in t
	he SLA Repository Database"/>.

##Agremeents<a name="agreements"></a>##

###Create an agreement###

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/agreements

	POST /sla-service/agreements HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2504

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/agreements/agreement
	01
	Content-Type: application/xml
	Content-Length: 258
	Date: Mon, 26 Jan 2015 12:21:40 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The agreement has been stored successfully i
	n the SLA Repository Database. It has location http://localhost:
	8080/sla-service/agreements/agreement01" elementId="agreement01"
	/>

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement02.json -X POST -H Content-type:application/json -H Accept:application/json http://localhost:8080/sla-service/agreements

	POST /sla-service/agreements HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/json
	Accept:application/json
	Content-Length: 1632

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/agreements/agreement
	02
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:42 GMT
	c6
	{"code":201,"message":"The agreement has been stored successfull
	y in the SLA Repository Database. It has location http://localho
	st:8080/sla-service/agreements/agreement02","elementId":"agreeme
	nt02"}

---

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement02b.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/agreements

	POST /sla-service/agreements HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2748

	HTTP/1.1 201 Created
	Server: Apache-Coyote/1.1
	location: http://localhost:8080/sla-service/agreements/agreement
	02b
	Content-Type: application/xml
	Content-Length: 260
	Date: Mon, 26 Jan 2015 12:21:42 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The agreement has been stored successfully i
	n the SLA Repository Database. It has location http://localhost:
	8080/sla-service/agreements/agreement02b" elementId="agreement02
	b"/>

---

Linked provider not exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement03.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/agreements

	POST /sla-service/agreements HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2747

	HTTP/1.1 409 Conflict
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 150
	Date: Mon, 26 Jan 2015 12:21:43 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Provider with id:provider03 doesn't exist SLA 
	Repository Database"/>

---

Linked template not exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement04.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/agreements

	POST /sla-service/agreements HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2747

	HTTP/1.1 409 Conflict
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 150
	Date: Mon, 26 Jan 2015 12:21:43 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Template with id:template04 doesn't exist SLA 
	Repository Database"/>

---

Agreement exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/sla-service/agreements

	POST /sla-service/agreements HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2504

	HTTP/1.1 409 Conflict
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 160
	Date: Mon, 26 Jan 2015 12:21:44 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Agreement with id:agreement01 already exists i
	n the SLA Repository Database"/>

###Get an agreement###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/agreements/agreement01?

	GET /sla-service/agreements/agreement01? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 2521
	Date: Mon, 26 Jan 2015 12:21:44 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><wsag:Agr
	eement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement".x
	mlns:sla="http://sla.atos.eu" wsag:AgreementId="agreement01">.<w
	sag:Name>ExampleAgreement</wsag:Name>.<wsag:Context>..<wsag:Agre
	ementInitiator>RandomClient</wsag:AgreementInitiator>..<wsag:Agr
	eementResponder>provider01</wsag:AgreementResponder>..<wsag:Serv
	iceProvider>AgreementResponder</wsag:ServiceProvider>..<wsag:Exp
	irationTime>2014-03-07T12:00:00+0100</wsag:ExpirationTime>..<wsa
	g:TemplateId>template01</wsag:TemplateId>..<sla:Service>service0
	1</sla:Service>.....</wsag:Context>.<wsag:Terms>..<wsag:All>...<
	wsag:ServiceDescriptionTerm wsag:Name="SDTName1" wsag:ServiceNam
	e="ServiceName">....DSL expression...</wsag:ServiceDescriptionTe
	rm>...<wsag:ServiceDescriptionTerm wsag:Name="SDTName2" wsag:Ser
	viceName="ServiceName">....DSL expression...</wsag:ServiceDescri
	ptionTerm>...<wsag:ServiceProperties wsag:Name="NonFunctional" w
	sag:ServiceName="ServiceName">....<wsag:VariableSet>.....<wsag:V
	ariable wsag:Name="ResponseTime" wsag:Metric="xs:double">......<
	wsag:Location>qos:ResponseTime</wsag:Location>.....</wsag:Variab
	le>.....<wsag:Variable wsag:Name="Performance" wsag:Metric="xs:d
	ouble">......<wsag:Location>qos:Performance</wsag:Location>.....
	</wsag:Variable>....</wsag:VariableSet>...</wsag:ServiceProperti
	es>...<wsag:GuaranteeTerm wsag:Name="GT_ResponseTime">....<wsag:
	ServiceScope wsag:ServiceName="ServiceName">ScopeName1</wsag:Ser
	viceScope>....<wsag:ServiceLevelObjective>.....<wsag:KPITarget>.
	.....<wsag:KPIName>ResponseTime</wsag:KPIName> <!--  same name a
	s property for the moment -->......<wsag:CustomServiceLevel>{"co
	nstraint" : "ResponseTime LT 0.9"}</wsag:CustomServiceLevel>....
	.</wsag:KPITarget>....</wsag:ServiceLevelObjective>...</wsag:Gua
	ranteeTerm>...<wsag:GuaranteeTerm wsag:Name="GT_Performance">...
	.<wsag:ServiceScope wsag:ServiceName="ServiceName">ScopeName2</w
	sag:ServiceScope>....<wsag:ServiceLevelObjective>.....<wsag:KPIT
	arget>......<wsag:KPIName>Performance</wsag:KPIName> <!--  same 
	name as property for the moment -->......<wsag:CustomServiceLeve
	l>{"constraint" : "Performance GT 0.1"}</wsag:CustomServiceLevel
	>.....</wsag:KPITarget>....</wsag:ServiceLevelObjective>....<wsa
	g:BusinessValueList>.....<wsag:Importance>3</wsag:Importance>...
	..<wsag:CustomBusinessValue>......<sla:Penalty type="discount" v
	alue="100" unit="euro"/>.....</wsag:CustomBusinessValue>....</ws
	ag:BusinessValueList>...</wsag:GuaranteeTerm>..</wsag:All>.</wsa
	g:Terms></wsag:Agreement>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/agreements/agreement01?

	GET /sla-service/agreements/agreement01? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Content-Length: 1349
	Date: Mon, 26 Jan 2015 12:21:44 GMT
	{"agreementId":"agreement01","name":"ExampleAgreement","context"
	:{"agreementInitiator":"RandomClient","agreementResponder":"prov
	ider01","serviceProvider":"AgreementResponder","expirationTime":
	"2014-03-07T12:00:00CET","templateId":"template01","service":"se
	rvice01"},"terms":{"allTerms":{"serviceDescriptionTerm":{"name":
	"SDTName2","serviceName":"ServiceName"},"serviceProperties":[{"n
	ame":"NonFunctional","serviceName":"ServiceName","variableSet":{
	"variables":[{"name":"ResponseTime","metric":"xs:double","locati
	on":"qos:ResponseTime"},{"name":"Performance","metric":"xs:doubl
	e","location":"qos:Performance"}]}}],"guaranteeTerms":[{"name":"
	GT_ResponseTime","serviceScope":{"serviceName":"ServiceName","va
	lue":"ScopeName1"},"qualifyingCondition":null,"businessValueList
	":null,"serviceLevelObjetive":{"kpitarget":{"kpiName":"ResponseT
	ime","customServiceLevel":"{\"constraint\" : \"ResponseTime LT 0
	.9\"}"}}},{"name":"GT_Performance","serviceScope":{"serviceName"
	:"ServiceName","value":"ScopeName2"},"qualifyingCondition":null,
	"businessValueList":{"customBusinessValue":[{"count":1,"duration
	":"1970-01-01T00:00:00.000+0000","penalties":[{"type":"discount"
	,"expression":"","unit":"euro","validity":""}]}],"importance":3}
	,"serviceLevelObjetive":{"kpitarget":{"kpiName":"Performance","c
	ustomServiceLevel":"{\"constraint\" : \"Performance GT 0.1\"}"}}
	}]}}}

###Get all the agreements###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/agreements?

	GET /sla-service/agreements? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 7924
	Date: Mon, 26 Jan 2015 12:21:45 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><agreemen
	ts><wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-
	agreement".xmlns:sla="http://sla.atos.eu" wsag:AgreementId="agre
	ement01">.<wsag:Name>ExampleAgreement</wsag:Name>.<wsag:Context>
	..<wsag:AgreementInitiator>RandomClient</wsag:AgreementInitiator
	>..<wsag:AgreementResponder>provider01</wsag:AgreementResponder>
	..<wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider
	>..<wsag:ExpirationTime>2014-03-07T12:00:00+0100</wsag:Expiratio
	nTime>..<wsag:TemplateId>template01</wsag:TemplateId>..<sla:Serv
	ice>service01</sla:Service>.....</wsag:Context>.<wsag:Terms>..<w
	sag:All>...<wsag:ServiceDescriptionTerm wsag:Name="SDTName1" wsa
	g:ServiceName="ServiceName">....DSL expression...</wsag:ServiceD
	escriptionTerm>...<wsag:ServiceDescriptionTerm wsag:Name="SDTNam
	e2" wsag:ServiceName="ServiceName">....DSL expression...</wsag:S
	erviceDescriptionTerm>...<wsag:ServiceProperties wsag:Name="NonF
	unctional" wsag:ServiceName="ServiceName">....<wsag:VariableSet>
	.....<wsag:Variable wsag:Name="ResponseTime" wsag:Metric="xs:dou
	ble">......<wsag:Location>qos:ResponseTime</wsag:Location>.....<
	/wsag:Variable>.....<wsag:Variable wsag:Name="Performance" wsag:
	Metric="xs:double">......<wsag:Location>qos:Performance</wsag:Lo
	cation>.....</wsag:Variable>....</wsag:VariableSet>...</wsag:Ser
	viceProperties>...<wsag:GuaranteeTerm wsag:Name="GT_ResponseTime
	">....<wsag:ServiceScope wsag:ServiceName="ServiceName">ScopeNam
	e1</wsag:ServiceScope>....<wsag:ServiceLevelObjective>.....<wsag
	:KPITarget>......<wsag:KPIName>ResponseTime</wsag:KPIName> <!-- 
	 same name as property for the moment -->......<wsag:CustomServi
	ceLevel>{"constraint" : "ResponseTime LT 0.9"}</wsag:CustomServi
	ceLevel>.....</wsag:KPITarget>....</wsag:ServiceLevelObjective>.
	..</wsag:GuaranteeTerm>...<wsag:GuaranteeTerm wsag:Name="GT_Perf
	ormance">....<wsag:ServiceScope wsag:ServiceName="ServiceName">S
	copeName2</wsag:ServiceScope>....<wsag:ServiceLevelObjective>...
	..<wsag:KPITarget>......<wsag:KPIName>Performance</wsag:KPIName>
	 <!--  same name as property for the moment -->......<wsag:Custo
	mServiceLevel>{"constraint" : "Performance GT 0.1"}</wsag:Custom
	ServiceLevel>.....</wsag:KPITarget>....</wsag:ServiceLevelObject
	ive>....<wsag:BusinessValueList>.....<wsag:Importance>3</wsag:Im
	portance>.....<wsag:CustomBusinessValue>......<sla:Penalty type=
	"discount" value="100" unit="euro"/>.....</wsag:CustomBusinessVa
	lue>....</wsag:BusinessValueList>...</wsag:GuaranteeTerm>..</wsa
	g:All>.</wsag:Terms></wsag:Agreement><wsag:Agreement wsag:Agreem
	entId="agreement02" xmlns:wsag="http://www.ggf.org/namespaces/ws
	-agreement" xmlns:sla="http://sla.atos.eu"><wsag:Name>ExampleAgr
	eement</wsag:Name><wsag:Context><wsag:AgreementInitiator>client-
	prueba</wsag:AgreementInitiator><wsag:AgreementResponder>provide
	r02</wsag:AgreementResponder><wsag:ServiceProvider>AgreementResp
	onder</wsag:ServiceProvider><wsag:ExpirationTime>2014-03-07T12:0
	0:00CET</wsag:ExpirationTime><wsag:TemplateId>template02</wsag:T
	emplateId><sla:Service>service02</sla:Service></wsag:Context><ws
	ag:Terms><wsag:All><wsag:ServiceProperties wsag:Name="ServicePro
	perties" wsag:ServiceName="ServiceName"><wsag:VariableSet><wsag:
	Variable wsag:Name="metric1" wsag:Metric="xs:double"><wsag:Locat
	ion>metric1</wsag:Location></wsag:Variable><wsag:Variable wsag:N
	ame="metric2" wsag:Metric="xs:double"><wsag:Location>metric2</ws
	ag:Location></wsag:Variable><wsag:Variable wsag:Name="metric3" w
	sag:Metric="xs:double"><wsag:Location>metric3</wsag:Location></w
	sag:Variable><wsag:Variable wsag:Name="metric4" wsag:Metric="xs:
	double"><wsag:Location>metric4</wsag:Location></wsag:Variable></
	wsag:VariableSet></wsag:ServiceProperties><wsag:GuaranteeTerm ws
	ag:Name="GT_Metric1"><wsag:ServiceScope wsag:ServiceName="Servic
	eName"></wsag:ServiceScope><wsag:ServiceLevelObjective><wsag:KPI
	Target><wsag:KPIName>metric1</wsag:KPIName><wsag:CustomServiceLe
	vel>.......{"constraint" : "metric1 BETWEEN (0.05, 1)"}......</w
	sag:CustomServiceLevel></wsag:KPITarget></wsag:ServiceLevelObjec
	tive></wsag:GuaranteeTerm><wsag:GuaranteeTerm wsag:Name="GT_Metr
	ic2"><wsag:ServiceScope wsag:ServiceName="ServiceName"></wsag:Se
	rviceScope><wsag:ServiceLevelObjective><wsag:KPITarget><wsag:KPI
	Name>metric2</wsag:KPIName><wsag:CustomServiceLevel>.......{"con
	straint" : "metric2 BETWEEN (0.1, 1)"}......</wsag:CustomService
	Level></wsag:KPITarget></wsag:ServiceLevelObjective></wsag:Guara
	nteeTerm><wsag:GuaranteeTerm wsag:Name="GT_Metric3"><wsag:Servic
	eScope wsag:ServiceName="ServiceName"></wsag:ServiceScope><wsag:
	ServiceLevelObjective><wsag:KPITarget><wsag:KPIName>metric3</wsa
	g:KPIName><wsag:CustomServiceLevel>.......{"constraint" : "metri
	c3 BETWEEN (0.15, 1)"}......</wsag:CustomServiceLevel></wsag:KPI
	Target></wsag:ServiceLevelObjective></wsag:GuaranteeTerm><wsag:G
	uaranteeTerm wsag:Name="GT_Metric4"><wsag:ServiceScope wsag:Serv
	iceName="ServiceName"></wsag:ServiceScope><wsag:ServiceLevelObje
	ctive><wsag:KPITarget><wsag:KPIName>metric4</wsag:KPIName><wsag:
	CustomServiceLevel>.......{"constraint" : "metric4 BETWEEN (0.2,
	 1)"}......</wsag:CustomServiceLevel></wsag:KPITarget></wsag:Ser
	viceLevelObjective></wsag:GuaranteeTerm></wsag:All></wsag:Terms>
	</wsag:Agreement><wsag:Agreement xmlns:wsag="http://www.ggf.org/
	namespaces/ws-agreement".xmlns:sla="http://sla.atos.eu" wsag:Agr
	eementId="agreement02b">.<wsag:Name>ExampleAgreement</wsag:Name>
	.<wsag:Context>..<wsag:AgreementInitiator>RandomClient</wsag:Agr
	eementInitiator>..<wsag:AgreementResponder>provider02</wsag:Agre
	ementResponder>..<wsag:ServiceProvider>AgreementResponder</wsag:
	ServiceProvider>..<wsag:ExpirationTime>2014-03-07T12:00:00+0100<
	/wsag:ExpirationTime>..<wsag:TemplateId>template02</wsag:Templat
	eId>..<sla:Service>service02</sla:Service>.....</wsag:Context>.<
	wsag:Terms>..<wsag:All>...<wsag:ServiceDescriptionTerm wsag:Name
	="SDTName1" wsag:ServiceName="ServiceName">....DSL expression...
	</wsag:ServiceDescriptionTerm>...<wsag:ServiceDescriptionTerm ws
	ag:Name="SDTName2" wsag:ServiceName="ServiceName">....DSL expres
	sion...</wsag:ServiceDescriptionTerm>...<wsag:ServiceProperties 
	wsag:Name="NonFunctional" wsag:ServiceName="ServiceName">....<ws
	ag:VariableSet>.....<wsag:Variable wsag:Name="ResponseTime" wsag
	:Metric="xs:double">......<wsag:Location>qos:ResponseTime</wsag:
	Location>.....</wsag:Variable>.....<wsag:Variable wsag:Name="Per
	formance" wsag:Metric="xs:double">......<wsag:Location>qos:Perfo
	rmance</wsag:Location>.....</wsag:Variable>....</wsag:VariableSe
	t>...</wsag:ServiceProperties>...<wsag:GuaranteeTerm wsag:Name="
	GT_ResponseTime">....<wsag:ServiceScope wsag:ServiceName="Servic
	eName">ScopeName1</wsag:ServiceScope>....<wsag:ServiceLevelObjec
	tive>.....<wsag:KPITarget>......<wsag:KPIName>ResponseTime</wsag
	:KPIName> <!--  same name as property for the moment -->......<w
	sag:CustomServiceLevel>{"constraint" : "ResponseTime LT 0.9"}</w
	sag:CustomServiceLevel>.....</wsag:KPITarget>....</wsag:ServiceL
	evelObjective>...</wsag:GuaranteeTerm>...<wsag:GuaranteeTerm wsa
	g:Name="GT_Performance">....<wsag:ServiceScope wsag:ServiceName=
	"ServiceName">ScopeName2</wsag:ServiceScope>....<wsag:ServiceLev
	elObjective>.....<wsag:KPITarget>......<wsag:KPIName>Performance
	</wsag:KPIName> <!--  same name as property for the moment -->..
	....<wsag:CustomServiceLevel>{"constraint" : "Performance GT 0.1
	"}</wsag:CustomServiceLevel>.....</wsag:KPITarget>....</wsag:Ser
	viceLevelObjective>....<wsag:BusinessValueList>.....<wsag:Import
	ance>3</wsag:Importance>.....<wsag:Penalty>......<wsag:Assessmen
	tInterval>.......<wsag:Count>10</wsag:Count>......</wsag:Assessm
	entInterval>......<wsag:ValueUnit>EUR</wsag:ValueUnit>......<wsa
	g:ValueExpression>99</wsag:ValueExpression>.....</wsag:Penalty>.
	.........<wsag:Reward></wsag:Reward>.....<wsag:Preference></wsag
	:Preference>.....<wsag:CustomBusinessValue></wsag:CustomBusiness
	Value>....</wsag:BusinessValueList>...</wsag:GuaranteeTerm>..</w
	sag:All>.</wsag:Terms></wsag:Agreement></agreements>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/agreements?

	GET /sla-service/agreements? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Content-Length: 4478
	Date: Mon, 26 Jan 2015 12:21:45 GMT
	[{"agreementId":"agreement01","name":"ExampleAgreement","context
	":{"agreementInitiator":"RandomClient","agreementResponder":"pro
	vider01","serviceProvider":"AgreementResponder","expirationTime"
	:"2014-03-07T12:00:00CET","templateId":"template01","service":"s
	ervice01"},"terms":{"allTerms":{"serviceDescriptionTerm":{"name"
	:"SDTName2","serviceName":"ServiceName"},"serviceProperties":[{"
	name":"NonFunctional","serviceName":"ServiceName","variableSet":
	{"variables":[{"name":"ResponseTime","metric":"xs:double","locat
	ion":"qos:ResponseTime"},{"name":"Performance","metric":"xs:doub
	le","location":"qos:Performance"}]}}],"guaranteeTerms":[{"name":
	"GT_ResponseTime","serviceScope":{"serviceName":"ServiceName","v
	alue":"ScopeName1"},"qualifyingCondition":null,"businessValueLis
	t":null,"serviceLevelObjetive":{"kpitarget":{"kpiName":"Response
	Time","customServiceLevel":"{\"constraint\" : \"ResponseTime LT 
	0.9\"}"}}},{"name":"GT_Performance","serviceScope":{"serviceName
	":"ServiceName","value":"ScopeName2"},"qualifyingCondition":null
	,"businessValueList":{"customBusinessValue":[{"count":1,"duratio
	n":"1970-01-01T00:00:00.000+0000","penalties":[{"type":"discount
	","expression":"","unit":"euro","validity":""}]}],"importance":3
	},"serviceLevelObjetive":{"kpitarget":{"kpiName":"Performance","
	customServiceLevel":"{\"constraint\" : \"Performance GT 0.1\"}"}
	}}]}}},{"agreementId":"agreement02","name":"ExampleAgreement","c
	ontext":{"agreementInitiator":"client-prueba","agreementResponde
	r":"provider02","serviceProvider":"AgreementResponder","expirati
	onTime":"2014-03-07T12:00:00CET","templateId":"template02","serv
	ice":"service02"},"terms":{"allTerms":{"serviceDescriptionTerm":
	null,"serviceProperties":[{"name":"ServiceProperties","serviceNa
	me":"ServiceName","variableSet":{"variables":[{"name":"metric1",
	"metric":"xs:double","location":"metric1"},{"name":"metric2","me
	tric":"xs:double","location":"metric2"},{"name":"metric3","metri
	c":"xs:double","location":"metric3"},{"name":"metric4","metric":
	"xs:double","location":"metric4"}]}}],"guaranteeTerms":[{"name":
	"GT_Metric1","serviceScope":{"serviceName":"ServiceName","value"
	:""},"qualifyingCondition":null,"businessValueList":null,"servic
	eLevelObjetive":{"kpitarget":{"kpiName":"metric1","customService
	Level":"\t\t\t\t\t\t\t{\"constraint\" : \"metric1 BETWEEN (0.05,
	 1)\"}\t\t\t\t\t\t"}}},{"name":"GT_Metric2","serviceScope":{"ser
	viceName":"ServiceName","value":""},"qualifyingCondition":null,"
	businessValueList":null,"serviceLevelObjetive":{"kpitarget":{"kp
	iName":"metric2","customServiceLevel":"\t\t\t\t\t\t\t{\"constrai
	nt\" : \"metric2 BETWEEN (0.1, 1)\"}\t\t\t\t\t\t"}}},{"name":"GT
	_Metric3","serviceScope":{"serviceName":"ServiceName","value":""
	},"qualifyingCondition":null,"businessValueList":null,"serviceLe
	velObjetive":{"kpitarget":{"kpiName":"metric3","customServiceLev
	el":"\t\t\t\t\t\t\t{\"constraint\" : \"metric3 BETWEEN (0.15, 1)
	\"}\t\t\t\t\t\t"}}},{"name":"GT_Metric4","serviceScope":{"servic
	eName":"ServiceName","value":""},"qualifyingCondition":null,"bus
	inessValueList":null,"serviceLevelObjetive":{"kpitarget":{"kpiNa
	me":"metric4","customServiceLevel":"\t\t\t\t\t\t\t{\"constraint\
	" : \"metric4 BETWEEN (0.2, 1)\"}\t\t\t\t\t\t"}}}]}}},{"agreemen
	tId":"agreement02b","name":"ExampleAgreement","context":{"agreem
	entInitiator":"RandomClient","agreementResponder":"provider02","
	serviceProvider":"AgreementResponder","expirationTime":"2014-03-
	07T12:00:00CET","templateId":"template02","service":"service02"}
	,"terms":{"allTerms":{"serviceDescriptionTerm":{"name":"SDTName2
	","serviceName":"ServiceName"},"serviceProperties":[{"name":"Non
	Functional","serviceName":"ServiceName","variableSet":{"variable
	s":[{"name":"ResponseTime","metric":"xs:double","location":"qos:
	ResponseTime"},{"name":"Performance","metric":"xs:double","locat
	ion":"qos:Performance"}]}}],"guaranteeTerms":[{"name":"GT_Respon
	seTime","serviceScope":{"serviceName":"ServiceName","value":"Sco
	peName1"},"qualifyingCondition":null,"businessValueList":null,"s
	erviceLevelObjetive":{"kpitarget":{"kpiName":"ResponseTime","cus
	tomServiceLevel":"{\"constraint\" : \"ResponseTime LT 0.9\"}"}}}
	,{"name":"GT_Performance","serviceScope":{"serviceName":"Service
	Name","value":"ScopeName2"},"qualifyingCondition":null,"business
	ValueList":{"customBusinessValue":[{"count":1,"duration":"1970-0
	1-01T00:00:00.000+0000","penalties":[]}],"importance":3},"servic
	eLevelObjetive":{"kpitarget":{"kpiName":"Performance","customSer
	viceLevel":"{\"constraint\" : \"Performance GT 0.1\"}"}}}]}}}]

###Get agreement status###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/agreements/agreement02/guaranteestatus?

	GET /sla-service/agreements/agreement02/guaranteestatus? HTTP/1.
	1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 391
	Date: Mon, 26 Jan 2015 12:21:46 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><guarante
	estatus AgreementId="agreement02" value="NON_DETERMINED"><guaran
	teetermstatus name="GT_Metric1" value="NON_DETERMINED"/><guarant
	eetermstatus name="GT_Metric2" value="NON_DETERMINED"/><guarante
	etermstatus name="GT_Metric3" value="NON_DETERMINED"/><guarantee
	termstatus name="GT_Metric4" value="NON_DETERMINED"/></guarantee
	status>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/agreements/agreement02/guaranteestatus?

	GET /sla-service/agreements/agreement02/guaranteestatus? HTTP/1.
	1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:46 GMT
	10a
	{"value":"NON_DETERMINED","AgreementId":"agreement02","guarantee
	termstatus":[{"name":"GT_Metric1","value":"NON_DETERMINED"},{"na
	me":"GT_Metric2","value":"NON_DETERMINED"},{"name":"GT_Metric3",
	"value":"NON_DETERMINED"},{"name":"GT_Metric4","value":"NON_DETE
	RMINED"}]}

###Delete an agreement###



	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/sla-service/agreements/agreement02b

	DELETE /sla-service/agreements/agreement02b HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:47 GMT
	4f
	The agreement id agreement02bwith it's enforcement job was succe
	ssfully deleted

---

Agreement not exists


	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/sla-service/agreements/notexists

	DELETE /sla-service/agreements/notexists HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:47 GMT
	9d
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<error c
	ode="404" message="There is no agreement with id notexists in th
	e SLA Repository Database"/>.

###Get agreement status###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/agreements/agreement02/guaranteestatus?

	GET /sla-service/agreements/agreement02/guaranteestatus? HTTP/1.
	1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 391
	Date: Mon, 26 Jan 2015 12:21:47 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><guarante
	estatus AgreementId="agreement02" value="NON_DETERMINED"><guaran
	teetermstatus name="GT_Metric1" value="NON_DETERMINED"/><guarant
	eetermstatus name="GT_Metric2" value="NON_DETERMINED"/><guarante
	etermstatus name="GT_Metric3" value="NON_DETERMINED"/><guarantee
	termstatus name="GT_Metric4" value="NON_DETERMINED"/></guarantee
	status>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/agreements/agreement02/guaranteestatus?

	GET /sla-service/agreements/agreement02/guaranteestatus? HTTP/1.
	1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:49 GMT
	10a
	{"value":"NON_DETERMINED","AgreementId":"agreement02","guarantee
	termstatus":[{"name":"GT_Metric1","value":"NON_DETERMINED"},{"na
	me":"GT_Metric2","value":"NON_DETERMINED"},{"name":"GT_Metric3",
	"value":"NON_DETERMINED"},{"name":"GT_Metric4","value":"NON_DETE
	RMINED"}]}

##Enforcement Jobs<a name="enforcements"></a>##

###Start enforcement job###

Content type: 

	$ /usr/bin/curl -u user:password -X PUT http://localhost:8080/sla-service/enforcements/agreement02/start

	PUT /sla-service/enforcements/agreement02/start HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept: */*

	HTTP/1.1 202 Accepted
	Server: Apache-Coyote/1.1
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:49 GMT
	3f
	The enforcement job with agreement-uuid agreement02 has started

###Stop enforcement job###

Content type: 

	$ /usr/bin/curl -u user:password -X PUT http://localhost:8080/sla-service/enforcements/agreement02/stop

	PUT /sla-service/enforcements/agreement02/stop HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept: */*

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:49 GMT
	3f
	The enforcement job with agreement-uuid agreement02 has stopped

##Violations<a name="violations"></a>##

Content type: 

	$ /usr/bin/curl -u user:password -X PUT http://localhost:8080/sla-service/enforcements/agreement01/start

	PUT /sla-service/enforcements/agreement01/start HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept: */*

	HTTP/1.1 202 Accepted
	Server: Apache-Coyote/1.1
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:51 GMT
	3f
	The enforcement job with agreement-uuid agreement01 has started

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/metric01.json -X POST -H Content-type:application/json http://localhost:8080/sla-service/enforcement-test/agreement01

	POST /sla-service/enforcement-test/agreement01 HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept: */*
	Content-type:application/json
	Content-Length: 77
	{."key": "Performance",."value": "0",."datetime": "2014-08-25T12
	:54:00+0000"}

	HTTP/1.1 202 Accepted
	Server: Apache-Coyote/1.1
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:51 GMT
	10
	Metrics received

###Get all the violations###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/violations?

	GET /sla-service/violations? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 387
	Date: Mon, 26 Jan 2015 12:21:56 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><violatio
	ns><violation><uuid>67c764e4-1272-4bdc-a2bd-affee26a2694</uuid><
	contract_uuid>agreement01</contract_uuid><service_name>ServiceNa
	me</service_name><service_scope>ScopeName2</service_scope><metri
	c_name>Performance</metric_name><datetime>2014-08-25T14:54:00CES
	T</datetime><actual_value>0</actual_value></violation></violatio
	ns>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/violations?

	GET /sla-service/violations? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:56 GMT
	dd
	[{"uuid":"67c764e4-1272-4bdc-a2bd-affee26a2694","datetime":"2014
	-08-25T14:54:00CEST","contract_uuid":"agreement01","service_name
	":"ServiceName","service_scope":"ScopeName2","metric_name":"Perf
	ormance","actual_value":"0"}]

##Penalties<a name="penalties"></a>##

###Get all the penalties###

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/sla-service/penalties?

	GET /sla-service/penalties? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/xml

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/xml
	Content-Length: 377
	Date: Mon, 26 Jan 2015 12:21:56 GMT
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><penaltie
	s><penalty xmlns:sla="http://sla.atos.eu" xmlns:wsag="http://www
	.ggf.org/namespaces/ws-agreement"><uuid>daa8c54c-5c52-4efe-8e4c-
	a5b7f31cf3fb</uuid><agreement>agreement01</agreement><datetime>2
	015-01-26T13:21:51CET</datetime><definition type="discount" expr
	ession="" unit="euro" validity=""/></penalty></penalties>

---

Accept: 200

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/sla-service/penalties?

	GET /sla-service/penalties? HTTP/1.1
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.38.0
	Host: localhost:8080
	Accept:application/json

	HTTP/1.1 200 OK
	Server: Apache-Coyote/1.1
	Content-Type: application/json
	Transfer-Encoding: chunked
	Date: Mon, 26 Jan 2015 12:21:58 GMT
	bc
	[{"uuid":"daa8c54c-5c52-4efe-8e4c-a5b7f31cf3fb","datetime":"2015
	-01-26T13:21:51CET","definition":{"type":"discount","expression"
	:"","unit":"euro","validity":""},"agreement":"agreement01"}]

