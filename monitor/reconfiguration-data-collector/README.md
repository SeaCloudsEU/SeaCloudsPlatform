SeaClouds - Reconfiguration Data Collector

=======================================This is a Tower 4Clouds Data Collector developed in order to
collects metrics from the SeaClouds Deployer about the deployed application and to feed Tower 4Clouds with these.
The Reconfiguration Data Collectors need the following environmental variables to work:

MANAGER_IP: the IP of Tower 4Clouds monitoring manager
MANAGER_PORT: the port of Tower 4Clouds monitoring manager
NURODC/DEPLOYERDC_CONFIG_FILE: the path to a .properties configuration file

In the following we report an example of the .properties configuration file with all the required properties to be set:

DEPLOYER_INSTANCE_IP=127.0.0.1
DEPLOYER_INSTANCE_PORT=8081
DC_SYNC_PERIOD=10
RESOURCES_KEEP_ALIVE_PERIOD=25

Then it is just necessary to run the compiled .jar file.

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
