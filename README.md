SeaClouds Platform
==================
![SeaClouds Project][SeaClouds-banner]
[SeaClouds-banner]: http://www.seaclouds-project.eu/img/header_test.jpg  "SeaClouds Project"
==================
[![Build Status](https://api.travis-ci.org/SeaCloudsEU/SeaCloudsPlatform.svg?branch=master)](https://travis-ci.org/SeaCloudsEU/SeaCloudsPlatform)

This is a proof of concept of the **SeaClouds Platform** ([www.seaclouds-project.eu](http://www.seaclouds-project.eu)), integrating a first version of the [Discoverer & Planner](../planner-branch/planner/) (considering the [Matchmaker process](../planner-branch/planner/matchmaker/)), [Deployer](./deployer/), [Monitor](./monitor/) and [SLA Service](https://github.com/SeaCloudsEU/sla-core/) components, into a [**Unified Dashboard**](./dashboard/src/main/webapp).


This work is part of the ongoing European research project *EC-FP7-ICT-610531* SeaClouds, and it's *currently under development*.

Contributing
-------------
If you want to help us with the development of this project please read carefully our [**Contributing Guide**](CONTRIBUTING.md).

Getting Started
-------------------
* Installation guide: please follow the instruction to know [how to deploy SeaClouds](usage/installer/README.md)

How to release it!
-------------------
In order to release a new version:

- `mvn clean install` If everything is ok:
- `mvn -DdryRun=true release:prepare -DreleaseVersion=0.7.0-M19 -Dtag=0.7.0-M19 -DdevelopmentVersion=0.8.0-SNAPSHOT` and wait for a message like `Release preparation simulation complete.`

Then:
- `mvn release:clean`
- `mvn release:prepare -DreleaseVersion=0.7.0-M19 -Dtag=0.7.0-M19 -DdevelopmentVersion=0.8.0-SNAPSHOT`
- `mvn release:perform`

- test the staging repository, and finally promote release it!

##License##
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
