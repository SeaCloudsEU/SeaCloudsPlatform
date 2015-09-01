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

##How to deploy##

Read the [Installation Guide][3]

##Duke example##

As part of the first integrated platform, the SLA Service reads a pregenerated agreement.

The agreement and monitoring rules for the Duke example are located in samples/. To store the agreement 
and rules:

    $ curl http://localhost:8080/sla-service/seaclouds/agreements -X POST -F sla=@"samples/duke-agreement.xml" -F rules=@"samples/duke-rules.xml"

##License##

Licensed under the [Apache License, Version 2.0][8]

[1]: docs/TOC.md
[2]: docs/developer-guide.md
[3]: docs/installation-guide.md
[8]: http://www.apache.org/licenses/LICENSE-2.0
