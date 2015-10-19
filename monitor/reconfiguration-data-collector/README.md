#SeaClouds - Reconfiguration Data Collector

This is a Tower 4Clouds Data Collector developed in order to
collects metrics from the SeaClouds Deployer about the deployed application and to feed Tower 4Clouds with these.
This Data Collector takes as input a .properties configuration file whose path is passed using standard java arguments.

In the following we report an example of the .properties configuration file with all the required properties to be set (in round brackets a description of the meaning of the property is reported):

    MANAGER_IP: 127.0.0.1 (the IP of the machine hosting the running instance of Tower 4Clouds Manager)
    MANAGER_PORT: 8170 (the port on which Tower 4Clouds Manager is listening)
    DEPLOYER_INSTANCE_IP=127.0.0.1 (the IP of the machine hosting the SeaClouds Deployer instance)
    DEPLOYER_INSTANCE_PORT=8081 (the port on which SeaClouds Deployer is listening)
    DC_SYNC_PERIOD=10 (tells the Data Collector how often to synchronize with Tower 4Clouds Manager)
    RESOURCES_KEEP_ALIVE_PERIOD=25 (tells to Tower 4Clouds Manager how long to wait an acknowledgement from the data collector before considering it and its resources expired)

Then it is just necessary to run the compiled .jar file giving as first argument the path to the .properties configuration file (i.e java -jar nuro-data-collector.jar path/to/config.properties).

The Reconfiguration Data Collector provides the following metrics, which names are quite self-descriptive:

IsAppInFire

Finally here is an example of a monitoring rule to install in order to get data from the NURO Data Collector:

    <ns2:monitoringRules>
        <ns2:monitoringRule id="isAppOnFirewxta1PAkrule" timeStep="20" timeWindow="20">
            <ns2:monitoredTargets>
                <ns2:monitoredTarget class="InternalComponent" type="wxta1PAk"/>
            </ns2:monitoredTargets>
            <ns2:collectedMetric metricName="IsAppOnFire">
                <ns2:parameter name="samplingTime">5</ns2:parameter>
            </ns2:collectedMetric>
            <ns2:actions>
                <ns2:action name="OutputMetric">
                    <ns2:parameter name="metric">IsAppOnFire_wxta1PAk</ns2:parameter>
                    <ns2:parameter name="value">METRIC</ns2:parameter>
                    <ns2:parameter name="resourceId">ID</ns2:parameter>
                </ns2:action>
            </ns2:actions>
        </ns2:monitoringRule>
    </ns2:monitoringRules>

Once an instance of Tower 4Clouds is running you can install the rule simply accessing the provided webapp going to <TOWER4CLOUDS_IP>:<TOWER4CLOUDS_PORT>/webapp (i.e. 127.0.0.1:8170/webapp), or performing a POST request to the endpoint /monitoring-rules (i.e. 127.0.0.1:8170/v1/monitoring-rules) adding the above xml as the request body. [Here](http://deib-polimi.github.io/tower4clouds/) you can find further information about how to access and use Tower 4Clouds.