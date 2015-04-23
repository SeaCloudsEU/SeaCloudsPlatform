The MODAClouds Deterministic Data Analyzer (rsp-services-csparql)
===========

In the context of MODAClouds European project (www.modaclouds.eu), Politecnico was
one of the partners involved in the development of the QoS Analysis and Monitoring Tools.

The Deterministic Data Analyzer (DDA) is the component responsible of aggregating, analyzing and verifying
conditions on monitoring data.

The original project (rsp-services-csparql) is available at [this link](https://github.com/streamreasoning/rsp-services-csparql),
this is a fork of the original repository, where the configuration was customized for the purposes of the MODAClouds Monitoring Platform.

Please refer to deliverable [D6.3.2](http://www.modaclouds.eu/publications/public-deliverables/) 
to better understand the role of this component in the MODAClouds Monitoring Platform.

Refer to the [Monitoring Platform Wiki](https://github.com/deib-polimi/modaclouds-monitoring-manager/wiki) for installation and usage of the whole platform.

## Change List

0.4.6.2-modaclouds:
* packaged together with executable
* package assembly automated

0.4.6.1-modaclouds:

* port can be specified as parameter (deafault is 8175): java -jar rsp-services-csparql [port]
* package is now built with dependencies by default when compiling: mvn package

## Usage

Requirements:
* JRE 7

Run:
```bash
./rsp-services-csparql [port]
```

