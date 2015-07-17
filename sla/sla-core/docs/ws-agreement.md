# WS-Agreement #

* [Introduction](#introduction)
* [Context](#context)
* [Service Description Terms](#sdt)
* [Service References](#sr)
* [Service Properties](#sp)
* [Guarantee Terms](#gt)
	* [Service Level Objective](#slo)
	* [Business Values](#business)

## <a name="introduction">Introduction</a> ##

It will be introduced here the ws-agreement specification and how this 
implementation deviates from the standard. Refer to the specification
for full explanation of the xml format.

The sla core tries to be [WS-Agreement][1] compliant. 
As such, this document uses the terms used in the specification.

This [WS-Agreement Language Guide][2] contains a summary of the specification.

WS-Agreement specifies an xml structure to define agreements and templates, 
and two layers interface of web services for operation.

This implementation is focused on the xml structure, and defines simpler 
REST interfaces for operations (see the [user guide][3]).

The XML representation of an agreement or a template has the following
structure:

	<wsag:Agreement AgreementId="xs:string">
		<wsag:Name>xs:string</wsag:Name> ?
		<wsag:Context>
			wsag:AgreementContextType
		</wsag:Context>
		<wsag:Terms>
			wsag:TermCompositorType
		</wsag:Terms>
	</wsag:Agreement>

The following describes the attributes and tags listed in the schema outlined 
above:

* `/wsag:Agreement`
 
  	This is the outermost document tag which encapsulates the entire agreement.
  An agreement contains an agreement context and a collection of agreement
  terms.
  
* `/wsag:Agreement/@AgreementId`

  	This is a mandatory identifier of this particular version of the agreement. It
  MUST be unique between Agreement Initiator and Agreement Responder.
  Through the effect of extended negotiation mechanisms not defined in this
  specification, different agreement documents MAY be regarded semantically as
  updated versions of an existing agreement relationship, potentially having the
  same Name and being exposed by the same Endpoint Reference. This id
  attribute helps agreement responder and consumer uniquely identify the version
  currently in force. If an agreement instance document is modified during the
  lifecycle of an Agreement resource, the identifier MUST also be replaced with a
  new, unique identifier.
  
* `/wsag:Terms`
 
  	The terms of an agreement comprises one or more service definition terms, and
  zero or more guarantee terms grouped using logical grouping operators.

The following is an example of an agreement:

	<?xml version="1.0" encoding="UTF-8"?>
	<wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement"
	  AgreementId="sample-agreement">
	
	  <wsag:Name>Sample Agreement</wsag:Name>
	  <wsag:Context>
	    <wsag:AgreementInitiator>client-prueba</wsag:AgreementInitiator>
	    <wsag:AgreementResponder>f4c993580-03fe-41eb-8a21</wsag:AgreementResponder>
	    <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
	    <wsag:ExpirationTime>2014-03-07T12:00:00</wsag:ExpirationTime>
	    <wsag:TemplateId>template02</wsag:TemplateId>
	    <sla:Service xmlns:sla="http://sla.atos.eu">sample-service</sla:Service>
	  </wsag:Context>
	  <wsag:Terms>
	    <wsag:All>
	      <wsag:ServiceDescriptionTerms Name="SDT" ServiceName="ServiceName"/>
	      <wsag:ServiceProperties Name="ServiceProperties" ServiceName="ServiceName">
	        <wsag:VariableSet>
	          <wsag:Variable Name="availability" Metric="xs:double">
	            <wsag:Location>metric1</wsag:Location>
	          </wsag:Variable>
	        </wsag:VariableSet>
	      </wsag:ServiceProperties>
	      <wsag:GuaranteeTerm Name="GT-availability">
	        <wsag:ServiceScope ServiceName="ServiceName"/>
	        <wsag:ServiceLevelObjective>
	          <wsag:KPITarget>
	            <wsag:KPIName>AVAILABILITY</wsag:KPIName>
	            <wsag:CustomServiceLevel>
	              {"constraint" : "availability BETWEEN (0.99, 1)"}
	            </wsag:CustomServiceLevel>
	          </wsag:KPITarget>
	        </wsag:ServiceLevelObjective>
	      </wsag:GuaranteeTerm>
	    </wsag:All>
	  </wsag:Terms>
	</wsag:Agreement>

A template has basically the same structure, and the intention of templates 
is to serve as base for new agreements. So, a procedure to create a new 
agreement for a template could be:

1. Retrieve the template for a service. The template could have "prefilled" 
   the context element (excepting the consumer), the service properties, the 
   guarantee terms...
2. Build an agreement xml using the template as a base, and filling the rest 
   of needed elements.
3. Initiate the negotiation.

The step 2 is domain-dependant, and it is recommended to add a domain factory that 
encapsulates this workflow, but having a simpler interface. For example:

1. Retrieve the template for a service, and extract the properties and boundaries 
   for this service.
2. Send the (consumer, templateId, properties, boundaries) in json format to the 
   domain service.
3. The domain service retrieves the template, builds the agreement and initiates 
   the negotiation.

To create templates for a service, it is recommended a similar procedure.

The sla core implements a mechanism to facilitate this kind of factory, with the
use of IParsers. The project can provide a translator from a simple format to 
WS-Agreement, so the the inputs of agreements and templates to the sla 
core are in this simple format. This helps to reduce the complexity of 
WS-Agreement format.

**The default implementation only allows a wsag:All term**.

**The default implementation does not handle the CreationContraints elements**. 
**It should be handled in the suggested domain layer**.

## <a name="context">Context</a> ##

The context describes some metadata about the agreement/template.

The specification is:

	<wsag:Context xs:anyAttribute>
		<wsag:AgreementInitiator>xs:anyType</wsag:AgreementInitiator> ?
		<wsag:AgreementResponder>xs:anyType</wsag:AgreementResponder> ?
		<wsag:ServiceProvider>wsag:AgreementRoleType</wsag:ServiceProvider>
		<wsag:ExpirationTime>xs:DateTime</wsag:ExpirationTime> ?
		<wsag:TemplateId>xs:string</wsag:TemplateId> ?
		<wsag:TemplateName>xs:string</wsag:TemplateName> ?
		<xs:any/> *
	</wsag:Context>
	
* `/wsag:Context/wsag:AgreementInitiator`

  	This optional element identifies the initiator of the agreement creation 
  request.
  
* `/wsag:Context/wsag:AgreementResponder`

  	This optional element identifies the agreement responder, i.e. the entity 
  that responds to the agreement creation request.
  
* `/wsag:Context/wsag:ServiceProvider`

  	This element identifies the service provider and is either 
  `AgreementInitiator` or `AgreementResponder`. 
  The default is `AgreementResponder`.
  
* `/wsag:Context/wsag:TemplateId`

  	If a template was used to create an offer, the TemplateId in the Context 
  MUST be set.

* `/wsag:Context/wsag:TemplateName`

  	The template name MUST be included in an offer if the
  offer is based on a template

The default implementation handles sla:Service element (WS-Agreement 
allows this kind of extensions), to identify the service provided in 
the agreement, as the WS-Agreement allows several provider services to 
be in the agreement.

The attribute ServiceName is present in the rest of elements in the agreement. 
The value of this attribute specifies an individual service of the several 
ones that may be inside an agreement/template, but is intended to only have 
meaning inside the agreement. As the ServiceName does not identify a service 
as known externally, the sla:Service element should be used for this matter.

In the case of only one ServiceName per agreement, the ServiceName value is 
a don't care value; it can have the same value as the sla:Service element, 
or have a fixed value. It is a domain task to specify this.

## <a name="sdt">Service description terms (SDT)</a> ##

The Service Description Term describes the offered service. Its main purpose 
is to describe the type of service to be provisioned in the case that this 
provision is made in the sla-system itself. 

The definition is:

	<wsag:ServiceDescriptionTerm
		wsag:Name="xs:string" wsag:ServiceName="xs:string">
		
		<xs:any> ... </xs:any>
	</wsag:ServiceDescriptionTerm> +

The default implementation does not handle the service description terms, and as 
such, the service must be provisioned externally.

The implementer may provide additional features handling the Service Description 
Terms. For example, the SDT can be filled by the system with needed information 
about the allocated resources, and only known after the allocation (e.g. IP).

## <a name="sr">Service references (SR)</a> ##

A service reference points to a service. So, if the service provided in 
the agreement is an external service, it may be referenced here. 
This way, the url/identifier/whatever associated with a ServiceName 
attribute can be known. Refer to page 20 of the spec for more details.

The definition is:

	<wsag:ServiceReference
		wsag:Name="xs:string" wsag:ServiceName="xs:string">
		
		<xs:any> ... </xs:any>
	</wsag:ServiceReference> +

**The default implementation does not handle the service references.**

## <a name="sp">Service properties (SP) </a> ##

ServiceProperties are used to define measurable and exposed properties associated
with a service, such as response time and throughput.

	<wsag:ServiceProperties
		wsag:Name="xs:string" wsag:ServiceName="xs:string">
		
		<wsag:VariableSet>
			<wsag:Variable wsag:Name="xs:string" wsag:Metric="xs:URI">
				<wsag:Location>xs:string</wsag:Location>
			</wsag:Variable> *
		</wsag:VariableSet>
	</wsag:ServiceProperties> +

The service properties are a set of variables that are used in the guarantee 
terms contraints. So, for example, if a constraint is : _uptime > 90_, there 
can be two service properties: ActualUptime and DesiredUptime. And the 
constraint will be ActualUptime > DesiredUptime. 

The default implementation does not use the service properties this way. It 
does not use the thresholds as service properties; only the actual metric. 

The following is a sample of a service property being valid in the default 
implementation:
 
	<wsag:Variable Name="Uptime" Metric="xs:double">
	    <wsag:Location>service-ping/Uptime</wsag:Location>
	</wsag:Variable>

 
* The name of the variable is used in the Guarantee Terms.
* The optional metric attribute refers to a schema type that the value of the 
  variable must fulfill.
* The location is defined in the spec as "the value of this element is a 
  structural reference to a field of arbitrary granularity in the service 
  terms - including fields within the domain-specific service descriptions". 
  According to [WSAJ Guarantee Evaluation Example][4], 
  this is interpreted as the place where to find the actual value of the 
  metric, referencing to an element in the SDT with, f.e., xpath.

In the default implementation, as the SDTs are not handled, the location is ignored. 

Alternative implementations may interpret the location as the "abstract 
location of the metric". So, the location can be used if the monitoring 
module expects a name different than the metric name to return measures.

## <a name="gt">Guarantee terms (GT)</a> ##
 	
The guarantee terms hold the constraints that are being enforced in the 
service exposed in this agreement. 

The definition is:

	<wsag:GuaranteeTerm Name="xs:string" Obligated="wsag:ServiceRoleType">
		<wsag:ServiceScope ServiceName="xs:string">
			xs:any ?
		</wsag:ServiceScope> *
		<wsag:QualifyingCondition> xs:anyType </wsag:QualifyingCondition> ?
		<wsag:ServiceLevelObjective>
			...
		</wsag:ServiceLevelObjective>
		<wsag:BusinessValueList>
			...
		</wsag:BusinessValueList>
	</wsag:GuaranteeTerm>

* `/wsag: GuaranteeTerm/@wsag:Name`

  	The MANDATORY name attribute (of type xs:string) represents the name given
  to a guarantee. Since an Agreement MAY encompass multiple GuaranteeTerms
  each term SHOULD be given a unique name.

* `/wsag:GuaranteeTerm/@wsag:Obligated`

	This attribute defines, which party enters the obligation to the guarantee 
  term. The wsag:ServiceRoleType can be either `ServiceConsumer` or 
  `ServiceProvider`. 
  **The default implementation does take this attribute into account,**
  **and always consider it as `ServiceProvider`**.

* `/wsag:GuaranteeTerm/wsag:ServiceScope`

	A guarantee term can have one or more service scopes. A service scope
  describes to what service element specifically a guarantee term applies. It
  contains a ServiceName attribute and any other XML structure describing a 
  substructure of a service to which the scope applies. For example, a 
  performance guarantee might only apply to one operation of a Web service at 
  a particular end point.
  
* `/wsag:GuaranteeTerm/wsag:ServiceScope/@ServiceName`

	The name of a service to which the guarantee term refers. A guarantee term
  service scope applies to exactly one service.

An example of guarantee term is:
 
	<wsag:GuaranteeTerm Name="GT-ResponseTime">
	    <wsag:ServiceScope ServiceName="service-ping"/>
	    <wsag:ServiceLevelObjective>
	        <wsag:KPITarget>
	            <wsag:KPIName>Uptime</wsag:KPIName>
	            <wsag:CustomServiceLevel>
	                    {"constraint" : "Uptime BETWEEN (90, 100)"}
	            </wsag:CustomServiceLevel>
	        </wsag:KPITarget>
	    </wsag:ServiceLevelObjective>
	</wsag:GuaranteeTerm>
 
  
### <a name="slo">Service Level Objective (SLO)</a> ###

The SLO in an assertion over the service attributes and/or external factors
as date, time.

The definition is:

	<wsag:ServiceLevelObjective>
		<wsag:KPITarget>
			<wsag:KPIName>xs:string</wsag:KPIName>
			<wsag:CustomServiceLevel>xs:any</wsag:CustomServiceLevel>
		</wsag:KPITarget> 
		|
		<wsag:CustomServiceLevel> xs:anyType </wsag:CustomServiceLevel>
	</wsag:ServiceLevelObjective>

KpiName is a name given to the constraint, The sample uses the same name 
as the service property used in the constraint. This makes more sense when 
using thresholds as service properties. This value is used as the attribute
kpiName of any violation of this GT.

The CustomServiceLevel is not specified by WS-Agreement, and a simple default
implementation is provided. See ConstraintEvaluator section in the 
developer guide.

<strong>Although there are three ways to define an SLO in WS-Agreement, 
the one supported in the sla core is shown in the previous example</strong>.

### <a name="business">Business Values</a> ###

Associated with each Service Level Objective is a Business Value List that 
contains multiple business values, each expressing a different value aspect 
of the objective. 

The definition is:

	<wsag:BusinessValueList>
		<wsag:Importance> xs:integer </wsag:Importance> ?
		<wsag:Penalty> 
			<wsag:AssessmentInterval>
				<wsag:TimeInterval>xs:duration</wsag:TimeInterval> |
				<wsag:Count>xs:positiveInteger</wsag:Count>
			</wsag:AssessmentInterval>
			<wsag:ValueUnit>xs:string</wsag:ValueUnit>?
			<wsag:ValueExpression>xs:anyType</wsag:ValueExpr>
		</wsag:Penalty> *
		<wsag:Reward>
			<wsag:AssessmentInterval>
				<wsag:TimeInterval>xs:duration</wsag:TimeInterval> |
				<wsag:Count>xs:positiveInteger</wsag:Count>
			</wsag:AssessmentInterval>
			<wsag:ValueUnit>xs:string</wsag:ValueUnit>?
			<wsag:ValueExpression>xs:anyType</wsag:ValueExpr>
		</wsag:Reward> *
		<wsag:Preference>
			<wsag:ServiceTermReference>xs:string </wsag:ServiceTermReference> *
			<wsag:Utility>xs:float</wsag:Utility> *
		</wsag:Preference> ?
		<wsag:CustomBusinessValue>xs:anyType</wsag:CustomBusinessValue> *
	</wsag:BusinessValueList>

For example:

	<wsag:GuaranteeTerm Name="GT-ResponseTime">
	    <wsag:ServiceScope ServiceName="service-ping"/>
	    <wsag:ServiceLevelObjective>...</wsag:ServiceLevelObjective>
	    <wsag:BusinessValueList>
	        <wsag:Importante>3</wsag:Importante>
	        <wsag:Penalty>
	            <wsag:AssessmentInterval>
	                <wsag:Count>100</wsag:Count>
	            </wsag:AssessmentInterval>
	            <wsag:ValueUnit>EUR</wsag:ValueUnit>
	            <wsag:ValueExpression>10</wsag:ValueExpression>
	        </wsag:Penalty>
	    </wsag:BusinessValueList>
	</wsag:GuaranteeTerm>

The concept behind this is that a violation of a GT can involve a business
penalty. On the other hand, a fulfilled GT can involve a business reward.

In the example, a violation of the SLO every 100 invocations to the service 
results in a penalization of 10 €.

The sla core implements a simple handling of business values. See the 
developer guide.

[1]: http://www.ogf.org/documents/GFD.192.pdf "WS-Agreement"
[2]: https://packcs-e0.scai.fraunhofer.de/wsag4j/wsag/wsag-language.html "WS-Agreement Language Guide"
[3]: user-guide.md
[4]: https://packcs-e0.scai.fraunhofer.de/wsag4j/server/guarantee_evaluation_example.html