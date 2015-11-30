SeaClouds Dashboard
==================

This component provides an easy way to interact with SeaClouds Platform by using an Angular.js web application. 

# Installing and running SeaClouds Dashboard
The easiest way to install and run this component is explained in SeaClouds [README.md](../README.md). However, there are some
use case that may find interesting to run SeaClouds Dashboard in standalone mode, like adding new features or running SeaClouds Dashboard
wired with a remote SeaClouds Platform instance.

## Requirements
Running SeaClouds Dashboard only requires Java 7 (or greater) installed on the target machine. Among with the software requirements, it also needs:

- SeaClouds Planner, Sla, Monitor and Deployer Components up and running on an accessible endpoint.
- 1 open port if you want to access to the Dashboard remotely.
- A config.yml configuration file.

### Config.yml configuration file
SeaClouds Dashboard is packaged as a Dropwizard application. It requires a configuration file to run. SeaClouds already has an example [config.yml](./config.yml)
file that takes all the required endpoints from ENV variables (specified by ${ENV_VAR_NAME}) and uses 8000 as default port. If you want to override this endpoints please replace it on the config.yml file or
modify the ENV variables.

## Starting the SeaClouds Dashboard
Once you have fulfilled the requirements you only need to run: 
``` java -jar dashboard.jar server path/to/config.yml ```


# Building SeaClouds Dashboard
If you want to build SeaClouds Dashboard please follow the instructions on SeaClouds [README.md](../README.md). 

#License##
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
