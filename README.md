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
* Installation guide: TODO
* Quickstart: TODO

How to release it!
-------------------
In order to release a new version:

1. create a new `release` branch out of `master` branch
2. update the maven version inside `release/new-branch` pom.xml:

    $ find . -iname pom.xml | xargs sed -i '' "s;<version>0.1.0-SNAPSHOT</version>;<version>0.1.0-M19</version>;g"

3. mvn clean install

If everything is ok, finally push it to Sonatype OSSRH:

4. mvn deploy -Prelease -DskipTests

##License##
Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
