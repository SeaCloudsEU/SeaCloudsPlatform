SeaClouds Discoverer
====================

The SeaClouds Discoverer module provides information about cloud offering used by SeaCloudsPlatform.

# Installing and running SeaClouds Discoverer
The easiest way to install and run this component is explained in SeaClouds [README.md](../README.md).

## Requirements
Running SeaClouds Discoverer only requires Java 7 (or greater) installed on the target machine. Among with the software requirements, it also needs:

- A discovererconf.yml configuration file.

### Config.yml configuration file
SeaClouds Discoverer is packaged as a Dropwizard application. It requires a configuration file to run. SeaClouds already has an example [discovererconf.yml](./discovererconf.yml)
file. The configuration information are:

- discoverer port (1236 by default)
- activeCrawlers is the list of crawlers to use (currently we only support "CloudHarmonyCrawler" and "PaasifyCrawler")
- repositoryPath is the path where the collected information are stored
- initializeRepository used to specify if the discoverer should initialize the repository with some pre-fetched information (true by default)

All the configuration variables are required.

## Starting the SeaClouds Discoverer
Once you have fulfilled the requirements you only need to run on JRE (>=1.7): 

``` java -jar discoverer.jar server path/to/config.yml ```

# Deployment diagram for discoverer
![alt tag](discoverer.png)

# Building SeaClouds Discoverer
If you want to build SeaClouds Discoverer please follow the instructions on SeaClouds [README.md](../README.md). 

#License##
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).