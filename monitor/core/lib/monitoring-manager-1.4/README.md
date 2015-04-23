The MODAClouds Monitoring Manager
=============================

In the context of MODAClouds European project (www.modaclouds.eu), Politecnico was
one of the partners involved in the development of the Monitoring Platform.

The monitoring manager is the main coordinator of the platform. 
It is responsible of installing monitoring rules, configuring monitoring components,
 attaching external observers to requested metrics and keeping the knowledge 
 base up to date through the interaction with Models@Runtime. The monitoring manager is also 
 is the main interface towards external components.

Please refer to deliverable [D6.3.2](http://www.modaclouds.eu/publications/public-deliverables/) 
to better understand the role of this component in the MODAClouds Monitoring Platform.

Refer to the [Monitoring Platform Wiki](https://github.com/deib-polimi/modaclouds-monitoring-manager/wiki) for installation and usage of the whole platform.

## Documentation

Take a look at the [documentation table of contents](doc/TOC.md).

## Change List

v1.4:
* Aggregations and mathematical expressions can now be expressed in the OutputAction value parameter.
* The monitoring manager waits for the availability of the DDA and KB for 30 seconds before failing the startup 
* Both DDA and KB are now cleaned and reset automatically at monitoring manager startup
* All REST api were bug fixed and are now working
* REST api changes: both `GET /metrics` and `GET /metrics/{id}/observers` now return a json file, see the doc
* KB and DDA are now run both on a single vagrant machine during integration test
* updated to [csparqool 1.2.2](https://github.com/deib-polimi/csparqool/releases/tag/v1.2.2)
* updated to [knowledge-base-api 2.2.1](https://github.com/deib-polimi/modaclouds-knowledge-base-api/releases/tag/v2.2.1)
* updatad to [data-collector-factory 0.3.2](https://github.com/deib-polimi/modaclouds-data-collector-factory/releases/tag/v0.3.2)
* updated to [qos-models 2.3](https://github.com/deib-polimi/modaclouds-qos-models/releases/tag/v2.3)

v1.3.4:
* the default list of metrics for monitoring rules can now be overwritten by a custom one, see the [doc](https://github.com/deib-polimi/modaclouds-monitoring-manager/blob/master/doc/user-manual.md#usage)

v1.3.3:
* multiple configuration options added, see the [doc](https://github.com/deib-polimi/modaclouds-monitoring-manager/blob/master/doc/user-manual.md#usage)
* updated to [qos-models 2.2.1](https://github.com/deib-polimi/modaclouds-qos-models/releases/tag/v2.2.1)

v1.3.1:
* packaged together with executable
* package assembly automated
* updated to [data-collector-factory 0.3.1](https://github.com/deib-polimi/modaclouds-data-collector-factory/releases/tag/v0.3.1)

v1.3:
* implemented API: `GET /model/resources/:id`
* updated to [csparqool 1.2.1](https://github.com/deib-polimi/csparqool/releases/tag/v1.2.1)
* updated to [knowledge-base-api 2.2](https://github.com/deib-polimi/modaclouds-knowledge-base-api/releases/tag/v2.2)
* updatad to [data-collector-factory 0.3](https://github.com/deib-polimi/modaclouds-data-collector-factory/releases/tag/v0.3)
* updated to [qos-models 2.2](https://github.com/deib-polimi/modaclouds-qos-models/releases/tag/v2.2)
* queries are now created according to the new qos-models 2.2 version of monitoring rules:
  * metricAggregation is optional
* outputMetric action in monitoring rules now accepts 3 parameters (resourceId, metric, value), check the updated [list](https://github.com/deib-polimi/modaclouds-qos-models/blob/master/src/main/resources/monitoring_actions.xml)
* sda are now just observers & data collectors from the monitoring manager perspective
* bug fixes

v1.2:
* internalComponents can now be a monitoredTarget
* system properties can be used now besides environemnt variables
* inheritance was removed

v1.1.2:

* Fixed a bug that didn't allow to upload a new model the first time using the PUT method
* updated to qos-models 2.1.4: check qos-models change list for the new of version of monitoring rules
* installation instructions updated (look at the documentation)
