SeaClouds - NURO Data Collector
==================

This is a Tower 4Clouds Data Collector developed in the context of SeaClouds to supply application level metrics that are not provided by already existing Data Collectors.
You can download [here](https://oss.sonatype.org/content/repositories/snapshots/eu/seaclouds-project/monitor/nuro-data-collector/0.8.0-SNAPSHOT/) one of the available builds.

This Data Collector takes as input a .properties configuration file whose path is passed using standard java arguments.

In the following we report the list of all the required environmental variables that need to be specified in the .properties file (in round brackets a description of the meaning of the property is reported)


+ MANAGER_IP: 127.0.0.1 (the IP of the machine hosting the running instance of Tower 4Clouds Manager)
+ MANAGER_PORT: 8170 (the port on which Tower 4Clouds Manager is listening)
+ NURO_INSTANCE_IP= 127.0.0.1 (the IP of machine hosting the NURO sensor.php)
+ NURO_INSTANCE_PORT= 8080 (the port on which the NURO sensor.php is exposed)
+ NURO_INSTANCE_USERNAME=seaclouds (NURO sensor.php user id)
+ NURO_INSTANCE_PASSWORD=preview (NURO sensor.php user password)
+ DC_SYNC_PERIOD=10 (tells the Data Collector how often to synchronize with Tower 4Clouds Manager)
+ RESOURCES_KEEP_ALIVE_PERIOD=25 (tells to Tower 4Clouds Manager how long to wait an acknowledgement from the data collector before considering it and its resources expired)
+ INTERNAL_COMPONENT_TYPE=NuroApplication (tells the Data Collector the type of the resource it is monitoring)
+ INTERNAL_COMPONENT_ID=NuroApplicationId (tells the Data Collector the ID of the resource it is monitoring)

Then it is just necessary to run the compiled .jar file giving as first argument the path to the .properties configuration file:

    java -jar nuro-data-collector-0.8.0.jar ./config.properties

The SeaClouds Data Collector currently provides the following metrics:

+ NUROServerLastMinuteAverageRunTime
+ NUROServerLastMinuteAverageThroughput
+ NUROServerLastMinutePlayerCount
+ NUROServerLastMinuteRequestCount
+ NUROServerLastTenSecondsAverageRunTime
+ NUROServerLastTenSecondsAverageThroughput
+ NUROServerLastTenSecondsPlayerCount
+ NUROServerLastTenSecondsRequestCount

Finally here is an example of a monitoring rule to install into Tower 4Clouds in order to get data from the NURO Data Collector:

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

Once an instance of Tower 4Clouds is running you can install the rule simply accessing the provided webapp going to <TOWER4CLOUDS_IP>:<TOWER4CLOUDS_PORT>/webapp (i.e. 127.0.0.1:8170/webapp), or performing a POST request to the endpoint /monitoring-rules (i.e. 127.0.0.1:8170/v1/monitoring-rules) adding the above xml as the request body. [Here](http://deib-polimi.github.io/tower4clouds/) you can find further information about how to access and use Tower 4Clouds.
