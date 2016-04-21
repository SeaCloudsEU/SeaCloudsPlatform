SeaClouds Planner
==================

This component provides design time functionalities for the SeaClouds Platform. 
It provides planning, replanning, DAM generation and monitoring rules retrieval via rest API.

# Installing and running SeaClouds Planner
The easiest way to install and run this component is explained in SeaClouds [README.md](../README.md).

## Requirements
Running SeaClouds Planner only requires Java 7 (or greater) installed on the target machine. Among with the software requirements, it also needs:

- SeaClouds Discoverer, Sla, Monitor component services up and running since they will be called via HTTP by the planner.
- A plannerconf.yml configuration file.

### Config.yml configuration file
SeaClouds Planner is packaged as a Dropwizard application. It requires a configuration file to run. SeaClouds already has an example [plannerconf.yml](./service/plannerconf.yml)
file. The configuration info are:

- planner port  (1234 by default)
- discovererURL is the Discoverer Component URL and port in the format http://{ip}:{port}/
- monitorGeneratorURL is the Monitor Component URL  (http://{ip})
- monitorGeneratorPort is the Monitor Component port ({number})
- slaGeneratorURL: is the SLA Component URL and port in the format http://{ip}:{port}/
- deployableProviders is the list of providers that the Deployer component is able to deploy (by default ["openstack-nova","openstack-keystone","openstack-nova-ec2", "byon", "sts", "elasticstack", "cloudstack", "rackspace-cloudidentity","aws-ec2","gogrid","elastichosts-lon-p","elastichosts-sat-p","elastichosts-lon-b","openhosting-east1","serverlove-z1-man","skalicloud-sdg-my","go2cloud-jhb1","softlayer","hpcloud-compute","rackspace-cloudservers-us","rackspace-cloudservers-uk","azurecompute","google-compute-engine","CloudFoundry"])
- filterOfferings is a boolean flag that enables the filter of non deployable providers for the matching process (default is false)
- influxdbURL is the InfluxDB service URL 
- influxdbPort is the InlfuxDB service port
- influxdbDatabase is the database name on InfluxDB service
- influxdbUsername is the username on InfluxDB service
- influxdbPassword is the password on InfluxDB service
- grafanaEndpoint is Grafana service URL and port in the format http://{ip}:{port}/
- grafanaUsername is the username on Grafana service
- grafanaPassword is the password on Grafana service

All the configuration info are required.

## Starting the SeaClouds Planner
Once you have fulfilled the requirements you only need to run on JRE (>=1.7): 
``` java -jar planner-service.jar server path/to/config.yml ```

You can get the last snapshot of the planner from the [repository](https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=eu.seaclouds-project&a=planner-service&v=LATEST&e=jar)

# Deployment diagram for planner
![](https://raw.githubusercontent.com/szenzaro/SeaCloudsPlatform/deploymentDiagram/planner/Planner.png)

# Building SeaClouds Planner
If you want to build SeaClouds Planner please follow the instructions on SeaClouds [README.md](../README.md). 

#License##
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
