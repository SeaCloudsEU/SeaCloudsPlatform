SeaClouds Deployer
====================


The main goal of the SeaClouds Deployer component is to deploy the application in a multi-cloud environment.


#Brief Introduction
The Seaclouds Deployer supports a syntax to specify the structure of an application, called the Deployable Application Model (DAM), which contains the necessary information to deploy an application over a set of cloud providers (locations). Following the TOSCA specification, DAM uses a YAML syntax allowing the application topology to be described. An application is composed by several modules and relationships, which is essential to maintain the knowledge about the application structure, features, constraints, target providers, and dependencies among modules and how they are related.

So, the Deployer receives a DAM, which specifies the application to deploy, its distribution and orchestration, and follows the instruction to deploy the application using the services indicated in the target locations and the cloud resources in a homogeneous way. Then, the Deployer maintains the management of the application and monitors its status (this last phase of the lifecycle is described at the end of this document), in communication with the Monitor and SLA Service components.

The SeaClouds [Initial Architecture](http://www.seaclouds-project.eu/deliverables/SeaClouds-D2_2-Initial_architecture_and_design_of_the_SeaClouds_platform.pdf) describes the Deployer component.

Different engines could be used to deploy the application. In our first solution, Brooklyn could be used as a Deployer engine, to accomplish the heterogeneous management of the cloud providers. And in a general solution, we define the Deployable Application Model based on the YAML Blueprint specification of Brooklyn.

#Building

SeaClouds Deployer can be built using Maven, as described in
[SeaClouds/README](https://github.com/SeaCloudsEU/SeaCloudsPlatform/blob/master/README.md#building-seaclouds)

   mvn clean install


#Installing and Running

SeaClouds Deployer contains a [Brooklyn](brooklyn.apache.org) plugin which adds PaaS management. CloudFoundry-based Platforms were added, such as [Pivotal](https://run.pivotal.io/), [Blueix](https://console.ng.bluemix.net/), and [OpenShift](https://www.openshift.com/). Moreover, it contains some Brooklyn entities and policies that are required by SeaClouds.

Then, this plugin has to be added to a Brooklyn installation. It can be found how downloading, installing and running Brooklyn in the [official documentation](https://brooklyn.apache.org/v/0.9.0-SNAPSHOT/).
SeaClouds Deployer has been developed to operate with Brooklyn 0.9.0-SNAPSHOT version.

Then, SeaClouds deployer jar that was generated during project building has to be added to a Brooklyn installation. The jar should be added to `$BROOKLYN_HOME/bin/lib/dropins` folder.

In order to enable TOSCA support in Brooklyn, it is necessary to add
[cloudfoundry/brooklyn-tosca](https://github.com/cloudsoft/brooklyn-tosca) plugin. Then, to add TOSCA support to SeaClouds Deployer, it is necessary to build [kiuby88/brooklyn-tosca temporal-integration branch](https://github.com/seacloudseu/brooklyn-tosca/tree/integration/temporal-integration). This branch contains a simple customization which allows brooklyn-tosca to be build with all required dependencies.



