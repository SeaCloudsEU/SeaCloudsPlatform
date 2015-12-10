# SeaClouds SLA Core #

[![Build Status](https://api.travis-ci.org/SeaCloudsEU/sla-core.svg?branch=develop)](https://travis-ci.org/SeaCloudsEU/sla-core)

##Description##

The SLA Core is an implementation of an SLA module, compliant with WS-Agreement.

It supports:

* one-shot negotiation
* agreement enforcement
* REST API

Read the [documentation][1]

##Technical description##

Read the [Developer Guide][2]

##How to install##

Read the [Installation Guide][3]

##Running##
The SLA Service needs the following env vars:

* DB_DRIVER: JDBC Driver. Default: `com.mysql.jdbc.Driver`
* DB_URL: A JDBC URL. Default: `jdbc:mysql://localhost:3306/sc_sla`
* DB_USERNAME: DB Username. Default: `atossla`
* DB_PASSWORD: DB Password. Default: `_atossla_`
* MONITOR_METRICS_URL: URL of the Tower 4Clouds metrics endpoint. Default: `http://localhost:8170/v1/metrics`
* SLA_URL: Endpoint of the SLA service (needed to subscribe to T4C as an observer). Default: `http://localhost:8080/sla-service`

After setting the needed vars, the recommended way to start the SLA Service is using jetty-runner.
`java -jar $jetty-runner-home/jetty-runner.jar --port $port --path / sla/sla-core/sla-service/target/sla-service.war`

## SeaClouds Workflow ##

You have a sequence diagram in docs/img/seaclouds-sla-workflow.png

###Generate and store a template from DAM and Monitoring Rules:

`curl http://localhost:9003/seaclouds/templates -X POST -F dam=@"sla-generator/src/test/resources/DemoDAM2.yml" -F rules=@"sla-generator/src/test/resources/seacloudsRules.xml" -H"Content-Type: multipart/form-data"`

###Generate agreement from template
`curl http://localhost:9003/seaclouds/commands/fromtemplate?templateId=5ec5d6b2-216c-47b3-9ba7-5819d2a698bb`

###POST agreement (maybe after adding business rules)
`curl -X POST http://localhost:9003/seaclouds/agreements?agreementId=desired-uuid -d@"sla-generator/src/test/resources/agre ement.xml" -H"Content-type: application/xml"`

###Start enforcement (when application is deployed and monitored)
`curl -X POST http://localhost:9003/seaclouds/commands/rulesready?agreementId=desired-uuid`

##License##

Licensed under the [Apache License, Version 2.0][8]

[1]: docs/TOC.md
[2]: docs/developer-guide.md
[3]: docs/installation-guide.md
[8]: http://www.apache.org/licenses/LICENSE-2.0
