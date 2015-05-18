SeaClouds Platform
==================
![SeaClouds Project][SeaClouds-banner]
[SeaClouds-banner]: http://www.seaclouds-project.eu/img/header_test.jpg  "SeaClouds Project"
==================
[![Build Status](https://api.travis-ci.org/SeaCloudsEU/SeaCloudsPlatform.svg?branch=master)](https://travis-ci.org/SeaCloudsEU/SeaCloudsPlatform)
[![Coverage Status](https://coveralls.io/repos/SeaCloudsEU/SeaCloudsPlatform/badge.svg?branch=master)](https://coveralls.io/r/SeaCloudsEU/SeaCloudsPlatform?branch=master)

This is a proof of concept of the **SeaClouds Platform** ([www.seaclouds-project.eu](http://www.seaclouds-project.eu)), integrating a first version of the [Discoverer & Planner](../planner-branch/planner/) (considering the [Matchmaker process](../planner-branch/planner/matchmaker/)), [Deployer](./deployer/), [Monitor](./monitor/) and [SLA Service](https://github.com/SeaCloudsEU/sla-core/) components, into a [**Unified Dashboard**](./dashboard/src/main/webapp).


This work is part of the ongoing European research project *EC-FP7-ICT-610531* SeaClouds, and it's *currently under development*.

Contributing
-------------
If you want to help us with the development of this project please read carefully our [**Contributing Guide**](CONTRIBUTING.md).

Getting Started
-------------------
* Installation guide: TODO
* Quickstart: TODO

Monitor Service Component
-------------------
* What is it: This component is a RESTful Web service that allows the initiation of MODAClouds monitoring platform and offers some basic functionalities over this platform.
These functionalities concern the installation of monitoring rules and deployment plans to the monitoring platform.
They are also related to the retrieval of proper data collector execution and installation files.
Through the provided API, monitoring rules can also be un-installed.
Finally, a list of monitoring metrics, used by data collectors, can be retrieved.

* Compiling, deploying, and running it: Through using the Maven command: "mvn clean install tomcat6:run-war", you can compile the project, deploy the generated .war to a tomcat6 server and start the server, running at localhost.
Also, basic unit tests are provided in 'monitorTest/MonitorTest.java', executed at compile time.

* Using it: Through executing the main method, which exists in resources/MonitorCLI.java, you can use all the aforementioned functionalities.
In particular, a command line menu of these functionalities is offered.
Each menu choice performs suitable RESTFul invocations to the corresponding operations of the Monitor RESTFul Web service.


##License##
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
