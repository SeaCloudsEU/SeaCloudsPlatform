# SeaClouds - Monitoring DAM Generator Core

The main goal of this Java library within the SeaClouds Platform is to provide an automatic mechanism to generate all the required monitoring artifacts for an application managed by SeaClouds.
Given an Abstract Deployment Plan (ADP) specified in the SeaClouds TOSCA syntax, the Monitoring DAM Generator Core builds up a set of monitoring rules to be then installed into Tower 4Clouds platform in order to retrieve and filter at runtime the monitring data sent by the deployed data collectors. The Monitoring DAM Generator Core also builds a set of bash scripts suitable to deploy all the necessary data collectors to collect the metrics requested by the generated monitoring rules. Finally it generates all the required pieces of yaml to be added into the SeaClouds DAM in order to be able to deploy the data collectors. This are generated according to the current DAM definition ( https://docs.google.com/document/d/1Z9RdaWn54IomgI-jCiw-tdutsCdDoHfk67PNUASOFmY/edit?disco=AAAAAiYOzxM ) and each TOSCA script is associated to one of the bash script previously generated.

# Usage

In order to instantiate a MonitoringDamGenerator you need to give it the IP and port number of Tower 4Clouds Monitoring Manager instance in the SeaClouds runtime Platform.
The MonitoringDamGenerator basically exhibits a single method which takes as input a String contaning a serialization of the ADP yaml and returns back a List of Module.
From each Module in the List it is possible to access to the MonitoringRules and to all the data collectors deployment scripts generated for that Module (Application Level Monitoring) (both in their the bash and the TOSCA representation). Each Module is associated with the Host on which it is going to be deployed. From the Host it is possible to access to all the MonitoringRules and to all the data collectors deployment scripts generated for that Host (Infrastructural Levele Monitoring). Here is a simple introductive example of how to use the library.

    MonitoringDamGenerator monDamGen= new MonitoringDamGenerator(monitoringManagerIp, monitoringManagerPort);
    List<Module> generated = monDamGen.generateMonitoringInfo(adp);
    for(Module module: generated){
            
        if(module.getRules()!=null){

        	Host host=module.getHost();
            MonitoringRules applicationRules=toReturn.module.getRules().getMonitoringRules();
            MonitoringRules hostRules=host.getRules().getMonitoringRules();

        	//processing continue
        }
            
    }    
    
# Issue

Currently the Monitoring DAM Generator Core does not upload the generated scripts for deploying the data collectors anywhere.

