[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / GET-monitoring-rules

# Monitoring Rules

	GET /monitoring-rules

## Description
Returns the list of installed monitoring rules.

***

## URL Parameters

None

***

## Response

**Status:** **200 OK**

**Body:** An XML object with a list of monitoring rules, conforming to the [monitoring_rules_schema.xsd][].

***

## Errors

None

***

## Example
**Request**

	GET v1/monitoring-rules

**Response**

	Status: 200 OK

``` xml
<monitoringRules
	xmlns="http://www.modaclouds.eu/xsd/1.0/monitoring_rules_schema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.modaclouds.eu/xsd/1.0/monitoring_rules_schema
	https://raw.githubusercontent.com/deib-polimi/modaclouds-qos-models/v1.0/metamodels/monitoringrules/monitoring_rules_schema.xsd">
	<monitoringRule id="mr_1" label="CPU Utilization Rule"
		startEnabled="true" timeStep="60" timeWindow="60">
		<monitoredTargets>
			<monitoredTarget type="tr_1" class="VM"/>
		</monitoredTargets>
		<collectedMetric inherited="false" metricName="CpuUtilization">
			<parameter name="samplingTime">10</parameter>
		</collectedMetric>
		<metricAggregation groupingClass="Region"
			aggregateFunction="Average">
		</metricAggregation>
		<condition>METRIC &gt;= 0.6</condition>
		<actions>
			<action name="OutputMetric">
				<parameter name="resourceId">ID</parameter>
				<parameter name="metric">CpuUtilizationViolation</parameter>
				<parameter name="value">METRIC</parameter>
			</action>
		</actions>
	</monitoringRule>
</monitoringRules>
```

[monitoring_rules_schema.xsd]: https://raw.githubusercontent.com/deib-polimi/modaclouds-qos-models/v1.0/metamodels/monitoringrules/monitoring_rules_schema.xsd
