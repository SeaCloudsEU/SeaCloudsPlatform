SeaClouds - NURO Data Collector

=======================================

This is a Tower 4Clouds Data Collector specifically developed for the NURO case study,
which basically collects metrics from NURO sensor.php and feed Tower 4Clouds with these.
It is exploitable for both the IaaS and PaaS monitoring of the NURO case study.
The NURO Data Collectors need the following environmental variables to work:

MANAGER_IP: the IP of Tower 4Clouds monitoring manager
MANAGER_PORT: the port of Tower 4Clouds monitoring manager
NURODC/DEPLOYERDC_CONFIG_FILE: the path to a .properties configuration file

In the following we report an example of the .properties configuration file with all the required properties to be set:

NURO_INSTANCE_IP=127.0.0.1
NURO_INSTANCE_PORT=8080
NURO_INSTANCE_USERNAME=seaclouds
NURO_INSTANCE_PASSWORD=preview
DC_SYNC_PERIOD=10
RESOURCES_KEEP_ALIVE_PERIOD=25
INTERNAL_COMPONENT_TYPE=NuroApplication
INTERNAL_COMPONENT_ID=NuroApplicationId

Then it is just necessary to run the compiled .jar file.

The NURO Data Collector provides the following metrics, which names are quite self-descriptive:

NUROServerLastMinuteAverageRunTime
NUROServerLastMinuteAverageThroughput
NUROServerLastMinutePlayerCount
NUROServerLastMinuteRequestCount
NUROServerLastTenSecondsAverageRunTime
NUROServerLastTenSecondsAverageThroughput
NUROServerLastTenSecondsPlayerCount
NUROServerLastTenSecondsRequestCount

Finally here is an example of a monitoring rule to install in order to get data from the NURO Data Collector:

    <ns2:monitoringRules>
        <ns2:monitoringRule id="nuroTestPcRule" timeStep="30" timeWindow="30">
            <ns2:monitoredTargets>
                <ns2:monitoredTarget class="InternalComponent" type="NuroApplication"/>
            </ns2:monitoredTargets>
            <ns2:collectedMetric metricName="NUROServerLastTenSecondsPlayerCount">
                <ns2:parameter name="samplingTime">10</ns2:parameter>
            </ns2:collectedMetric>
            <ns2:metricAggregation groupingClass="InternalComponent" aggregateFunction="Average"/>
            <ns2:actions>
                <ns2:action name="OutputMetric">
                    <ns2:parameter name="metric">NUROServerLastTenSecondsPlayerCountOut</ns2:parameter>
                    <ns2:parameter name="value">METRIC</ns2:parameter>
                    <ns2:parameter name="resourceId">ID</ns2:parameter>
                </ns2:action>
            </ns2:actions>
        </ns2:monitoringRule>
    </ns2:monitoringRules>
