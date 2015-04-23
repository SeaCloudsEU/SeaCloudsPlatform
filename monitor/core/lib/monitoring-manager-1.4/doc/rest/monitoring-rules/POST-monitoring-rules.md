[Documentation table of contents](../../TOC.md) / [API Reference](../../api.md) / POST-monitoring-rules

# Monitoring Rules

	POST /monitoring-rules

## Description
Install monitoring rules.

***

## URL Parameters

None

***

## Data Parameters

An XML object with monitoring rules conforming to the [monitoring_rules_schema.xsd][].

***

## Response

**Status:** **204 No Content**

***

## Errors

All known errors cause the resource to return HTTP error code header together with a description of the error.

* **400 Bad Request** - One or more monitoring rules were not valid

***

## Example
**Request**

	POST v1/monitoring-rules


``` xml
<monitoringRules xmlns="http://www.modaclouds.eu/xsd/1.0/monitoring_rules_schema">
	<monitoringRule id="mr_1" label="CPU Utilization Rule"
		startEnabled="true" timeStep="60" timeWindow="60">
		<monitoredTargets>
			<monitoredTarget type="tr_1" class="VM"/>
		</monitoredTargets>
		<collectedMetric inherited="false" metricName="CpuUtilization">
			<parameter name="samplingTime">10</parameter>
		</collectedMetric>
		<metricAggregation groupingClass="CloudProvider"
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

[monitoring_rules_schema.xsd]: https://github.com/deib-polimi/modaclouds-qos-models/blob/master/metamodels/monitoringrules/monitoring_rules_schema.xsd
