# SeaClouds - Monitoring DAM Generator Service

The main goal of this service within the SeaClouds Platform is to provide an automatic mechanism to generate all the required monitoring artifacts for an application managed by SeaClouds. The Monitoring DAM Generator Service basically wraps the Monitoring DAM Generator Core.
Given an Abstract Deployment Plan (ADP) specified in the SeaClouds TOSCA syntax, the Monitoring DAM Generator Core builds up a set of monitoring rules to be then installed into Tower 4Clouds platform in order to retrieve and filter at runtime the monitring data sent by the deployed data collectors. The Monitoring DAM Generator Core also builds a set of bash scripts suitable to deploy all the necessary data collectors to collect the metrics requested by the generated monitoring rules. Finally it generates all the required pieces of yaml to be added into the SeaClouds DAM in order to be able to deploy the data collectors. This are generated according to the current DAM definition ( https://docs.google.com/document/d/1Z9RdaWn54IomgI-jCiw-tdutsCdDoHfk67PNUASOFmY/edit?disco=AAAAAiYOzxM ) and each TOSCA script is associated to one of the bash script previously generated.

The Monitoring DAM Generator Service is a Dropwizard application and by default it listen on port 8177.

# Usage

The Monitoring DAM Generator basically exhibits three distinct endpoints:

POST /damgen : this is the main endpoint which generates the application specific monitoring information. The request body must be the yaml representing the application ADP. It returns a JSON object with a single field containing an ID which can be used later on in order to retrieve from the Monitoring DAM Generator Service itself the generated rules and data collectors deployment scripts.

GET /{applicationId}/generatedDam : this interface returns a JSON object in which each field represent a TOSCA based deployment script for one of the data collectors which have to be deployed with the application.

GET /{applicationId}/monitoringRule : this interface return a JSON object with a single field contaning all the monitoring rules that have to be installed in order to monitor the application.

In order to run the service one need to use the following lines of code:

    export MONITOR_IP=<MONITORING-MANAGER-IP>    
    export MONITOR_PORT=<MONITORING-MANAGER-PORT>    
    cd monitoring-dam-generator    
    mvn clean install    
    cd target    
    java -jar monitoring-dam-generator-service-0.8.0-SNAPSHOT.jar server ../config.yaml 

# Issues

Currently the Monitoring DAM Generator Service is not storing the generated application IDs along with the rules and data collectors script in a persistent system. The service is basically stateful and keep all the generate dinformation in memory until it is running. Future developments will be devoted to add a persistence layer.

