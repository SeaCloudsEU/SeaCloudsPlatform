SeaClouds Dashboard
==================

This component provides an easy way to interact with SeaClouds Platform by using an Angular.js web application. 

# Installing and running SeaClouds Dashboard
The easiest way to install and run this component is explained in SeaClouds [README.md](../README.md). However, there are some
use case that may find interesting to run SeaClouds Dashboard in standalone mode, like adding new features or running SeaClouds Dashboard
wired with a remote SeaClouds Platform instance.

## Requirements
Running SeaClouds Dashboard only requires Java 7 (or greater) installed on the target machine. Among with the software requirements, it also needs:

- SeaClouds [Planner](../planner/Readme.md), [Sla](../sla/Readme.md), [Monitor](../monitor/Readme.md) and [Deployer](../deployer/Readme.md) Components up and running on an accessible endpoint.
- 1 free port.
- A config.yml configuration file.

### Config.yml configuration file
SeaClouds Dashboard is packaged as a Dropwizard application. It requires a configuration file to run. This file contain the following user customizable parameters:

- server.connector.port: A positive number which will be used by Dropwizard to expose the Dashboard. Required. Eg. 8000.
- planner.host: SeaClouds Planner IP. Required. Eg. 127.0.0.1.
- planner.port: SeaClouds Planner Port. Required. Eg. 1234.
- deployer.host: SeaClouds Deployer IP. Required. Eg. 127.0.0.1.
- deployer.port: SeaClouds Planner Port. Required. Eg. 8081.
- deployer.username: SeaClouds Deployer Username. Optional. Eg. user.
- deployer.password: SeaClouds Deployer Password. Optional. Eg. password.
- monitor.manager.host: SeaClouds Monitor (Tower4Clouds Monitoring Manager) IP. Required. Eg. 127.0.0.1.
- monitor.manager.port: SeaClouds Monitor (Tower4Clouds Monitoring Manager) Port. Required. Eg. 8710.
- sla.host: SeaClouds Deployer IP. Required. Eg. 127.0.0.1. 
- sla.port: SeaClouds SLA Port. Required. Eg. 8080. 

SeaClouds already has an example [config.yml](./config.yml) file that takes all the required endpoints from ENV variables (specified by ${ENV_VAR_NAME}) and uses 8000 as default port. 

## Installing SeaClouds Dashboard
If you want to install SeaClouds Dashboard as standalone mode please download the latest artifact from the [Maven Repository](https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=eu.seaclouds-project&a=planner-service&v=LATEST&e=jar) 

## Starting the SeaClouds Dashboard
Once you have fulfilled the requirements you only need to run: 
``` java -jar dashboard.jar server path/to/config.yml ```


# Building SeaClouds Dashboard
SeaClouds Dashboard requires the following dependencies to be fulfilled to be built:
- Git
- Java 7 JDK.
- Maven 3.3.3 or greater.
- Node.js 0.12.x (with npm) or greater.

After you have been installed all the prerequisites, you can download the master branch of the repository from git with 
`git clone git@github.com:SeaCloudsEU/SeaCloudsPlatform.git` browse to dashboard and build it with `mvn clean install` 
(if you want to skip the tests, please add `-DskipTest` flag). 

If you need more information on building SeaClouds on SeaClouds Platform [README.md](../README.md).

#License##
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
