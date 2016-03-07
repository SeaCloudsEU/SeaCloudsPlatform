SeaClouds Platform
==================
![SeaClouds Project][SeaClouds-banner]
[SeaClouds-banner]: http://www.seaclouds-project.eu/img/header_test.jpg  "SeaClouds Project"
==================
[![Build Status](https://api.travis-ci.org/SeaCloudsEU/SeaCloudsPlatform.svg?branch=master)](https://travis-ci.org/SeaCloudsEU/SeaCloudsPlatform)&nbsp;[![codecov.io](https://codecov.io/github/SeaCloudsEU/SeaCloudsPlatform/coverage.svg?branch=master)](https://codecov.io/github/SeaCloudsEU/SeaCloudsPlatform?branch=master)&nbsp;[![Gitter](https://badges.gitter.im/gitterHQ/gitter.svg)](https://gitter.im/SeaCloudsEU/SeaCloudsPlatform)

This is a proof of concept of the **SeaClouds Platform** ([www.seaclouds-project.eu](http://www.seaclouds-project.eu)), integrating a first version of the [Discoverer & Planner](../planner-branch/planner/) (considering the [Matchmaker process](../planner-branch/planner/matchmaker/)), [Deployer](./deployer/), [Monitor](./monitor/) and [SLA Service](https://github.com/SeaCloudsEU/sla-core/) components, into a [**Unified Dashboard**](./dashboard/src/main/webapp).


This work is part of the ongoing European research project *EC-FP7-ICT-610531* SeaClouds, and it's *currently under development*.

# Building SeaClouds
Building SeaClouds shouldn't be a big deal, however it has the following prerequisites:
- Git
- Java 7 JDK.
- Maven 3.3.3 or greater.
- Node.js 0.12.x (with npm) or greater.

After you have been installed all the prerequisites, you can download the master branch of the repository from git with `git clone git@github.com:SeaCloudsEU/SeaCloudsPlatform.git` and build it with `mvn clean install` (if you want to skip the tests, please add `-DskipTest` flag). 

# Deploy SeaClouds
A deployment of SeaClouds can be launched using Apache Brooklyn. We currently support deployments against Bring Your
Own Nodes (BYON) and to all the IaaS provider supported by [Apache jclouds](http://jclouds.org).

## Setup your environment

Make sure you have [Vagrant](https://www.vagrantup.com/), [Virtualbox](https://www.virtualbox.org/)

## Deploying SeaClouds on BYON

- Configure your local environment:
```bash
cd $SEACLOUDS_HOME/byon
vagrant up
```
This spins up a virtual environment, made up of 2 VMs `brooklyn` and `seaclouds1`, that are accessible at `10.10.10.100` and `10.10.10.101`, respectively. 
**Note that deploying on BYON requires at least 4 GB of RAM available and a quad-core CPU with hardware virtualization support.**

- Point your favourite browser at `http://10.10.10.100:8081`
- Select `SeaClouds Platform on BYON` application from Apache Brooklyn dropdown menu
- Click on `Finish` button.


## Deploying SeaClouds on the cloud

- Configure your local environment:
```bash
cd $SEACLOUDS_HOME/byon
vagrant up brooklyn
```
This spins up an Apache Brooklyn server accessible at `http://10.10.10.100:8081`.

- Point your favourite browser at `http://10.10.10.100:8081`
- Select `SeaClouds platform` application from Apache Brooklyn dropdown menu
- Edit `location` specifying the cloud provider and the credentials to use it, in the YAML format.
- Click on `Finish` button


## SeaClouds release 0.7.0-M19

A detailed description of [0.7.0-M19](https://github.com/SeaCloudsEU/SeaCloudsPlatform/releases/tag/0.7.0-M19) SeaClouds release including:
- SeaClouds components and their interactions
- A guide to get an install SeaClouds Platform
- An example of how to use SeaClouds Platform and exploit its capabilities and the capabilies of each of its components

can be found in the [Integrated Platform deliverable](https://drive.google.com/file/d/0B3naRHlVBGTEdmYySFVWSGdIYzA/view?usp=sharing).

# Contributing
-------------
If you want to help us with the development of this project please read carefully our [**Contributing Guide**](CONTRIBUTING.md). 

# Troubleshooting
-------------
When deploying SeaClouds platform an [Apache Brooklyn](http://brooklyn.io) instance will be started on your
workstation, accessible at `http://localhost:8081` by default. Please double-check in nohup.out the correct url.

You may need to update the `privateKeyFile` property in the blueprint to the actual path.
By default, it points to `./seaclouds_id_rsa`.

Notice, if you are a `Windows` user, you may need to specify the absolute path to reach the `seaclouds_id_rsa` file on your filesystem.

For more information, please visit [Apache Brooklyn](https://brooklyn.incubator.apache.org/download/index.html)

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
