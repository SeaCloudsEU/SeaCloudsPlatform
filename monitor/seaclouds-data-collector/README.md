SeaClouds - NURO Data Collector
==================

This is a Tower 4Clouds Data Collector developed in the context of SeaClouds to supply application level metrics that are not provided by already existing Data Collectors.
You can download [here](https://oss.sonatype.org/content/repositories/snapshots/eu/seaclouds-project/monitor/seaclouds-data-collector/0.8.0-SNAPSHOT/) one of the available builds.

The seaclouds-data-collector is a dropwizard application
This Data Collector takes as input a .properties carrying its configuration.
In the following we report the list of all the required variables that need to be specified in the .properties file (in round brackets a description of the meaning of the property is reported)

+ MANAGER_IP: 127.0.0.1 (the IP of the machine hosting the running instance of Tower 4Clouds Manager)
+ MANAGER_PORT: 8170 (the port on which Tower 4Clouds Manager is listening)
+ DC_SYNC_PERIOD=10 (tells the Data Collector how often to synchronize with Tower 4Clouds Manager)
+ RESOURCES_KEEP_ALIVE_PERIOD=25 (tells to Tower 4Clouds Manager how long to wait an acknowledgement from the data collector before considering it and its resources expired)

Then it is just necessary to run the compiled .jar file passing it the required config.yml file:

    java -jar seaclouds-data-collector-0.8.0.jar server config.yml

Be sure that the config.yaml and config.properties files are in the same directory of the executable jar. The required config.properties and config.yml (static) files can be found under the resources directory.

The SeaClouds Data Collector currently provides the following metrics:

+ NUROServerLastMinuteAverageRunTime
+ NUROServerLastMinuteAverageThroughput
+ NUROServerLastMinutePlayerCount
+ NUROServerLastMinuteRequestCount
+ NUROServerLastTenSecondsAverageRunTime
+ NUROServerLastTenSecondsAverageThroughput
+ NUROServerLastTenSecondsPlayerCount
+ NUROServerLastTenSecondsRequestCount
+ PaaSModuleAvailability

Finally here is an example of a monitoring rule to install into Tower 4Clouds in order to get data from the NURO Data Collector:

<monitoringRules xmlns="http://www.modaclouds.eu/xsd/1.0/monitoring_rules_schema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.modaclouds.eu/xsd/1.0/monitoring_rules_schema">
    <monitoringRule id="paasAvailabilityRuleNuroTest" timeStep="2" timeWindow="2">
        <monitoredTargets>
            <monitoredTarget class="InternalComponent" type="Nuro"/>
        </monitoredTargets>
        <collectedMetric metricName="PaaSModuleAvailability">
            <parameter name="samplingTime">5</parameter>
        </collectedMetric>
        <actions>
            <action name="OutputMetric">
                <parameter name="metric">NuroTestPaaSAvailability</parameter>
                <parameter name="value">METRIC</parameter>
                <parameter name="resourceId">ID</parameter>
            </action>
        </actions>
    </monitoringRule>
</monitoringRules>

Once an instance of Tower 4Clouds is running you can install the rule simply accessing the provided webapp going to <TOWER4CLOUDS_IP>:<TOWER4CLOUDS_PORT>/webapp (i.e. 127.0.0.1:8170/webapp), or performing a POST request to the endpoint /monitoring-rules (i.e. 127.0.0.1:8170/v1/monitoring-rules) adding the above xml as the request body. [Here](http://deib-polimi.github.io/tower4clouds/) you can find further information about how to access and use Tower 4Clouds.
