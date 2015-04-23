[Documentation table of contents](TOC.md) / User Manual

#User Manual

## What to configure

* DDA URL: the Deterministic Data Analyzer endpoint
* KB URL: the Knowledge Base endpoint
* Monitoring Manager Port: the port the Monitoring Manager should listen to
* Monitoring metrics file: the xml file list of metrics used for validating monitoring rules. The list should contain all metrics data collectors can provide. The file should be validated by the [metrics_schema](https://raw.githubusercontent.com/deib-polimi/modaclouds-qos-models/master/metamodels/commons/metrics_schema.xsd). The [default list](https://raw.githubusercontent.com/deib-polimi/modaclouds-qos-models/master/src/main/resources/monitoring_metrics.xml) can be overridden by a custom one either using a local file or a public URL.

## How to configure

The monitoring manager can be configured by means of different options (latters replaces the formers):
* Default Configuration
* Environment Variables
* System Properties
* CLI Arguments

### Default Configuration

* DDA URL: `http://127.0.0.1:8175`
* KB URL: `http://127.0.0.1:3030/modaclouds/kb`
* Monitoring Manager Port: `8170`
* Monitoring metrics file: [default list of monitoring metrics](https://raw.githubusercontent.com/deib-polimi/modaclouds-qos-models/master/src/main/resources/monitoring_metrics.xml)

### Environment Variables

```
MODACLOUDS_MONITORING_DDA_ENDPOINT_IP
MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT
MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP
MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT
MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH
MODACLOUDS_MONITORING_MANAGER_PORT
MODACLOUDS_MONITORING_MONITORING_METRICS_FILE
```

where:
* DDA URL: `http://${MODACLOUDS_MONITORING_DDA_ENDPOINT_IP}:${MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT}`
* KB URL: `http://${MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP}:${MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT}${MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH}`
* Monitoring Manager Port: `${MODACLOUDS_MONITORING_MANAGER_PORT}`
* Monitoring metrics file: `${MODACLOUDS_MONITORING_MONITORING_METRICS_FILE}`

### System Properties

Same names used for Environment Variables.

### CLI Arguments

Usage available by running `./monitoring-manager -help`:

```
Usage: monitoring-manager [options]
  Options:
    -ddaip
       DDA endpoint IP address
       Default: 127.0.0.1
    -ddaport
       DDA endpoint port
       Default: 8175
    -help
       Shows this message
       Default: false
    -kbip
       KB endpoint IP address
       Default: 127.0.0.1
    -kbpath
       KB URL path
       Default: /modaclouds/kb
    -kbport
       KB endpoint port
       Default: 3030
    -mmport
       Monitoring Manager endpoint port
       Default: 8170
    -validmetrics
       The xml file containing the list of valid metrics. Will overwrite default ones
```
