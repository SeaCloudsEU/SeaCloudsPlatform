[Documentation table of contents](TOC.md) / Required Interfaces

# Required Interfaces

## Deterministic Data Analyzer

The Deterministic Data Analyzer must provide an implementation of RDF Stream Processor 
REST Services (rsp-services) for C-SPARQL engine. Current supported version [0.4.1](https://github.com/streamreasoning/rsp-services/releases/tag/0.4.1)

Refer also to the iswc poster [*"A Restful Interface for RDF Stream Processors"*](http://ceur-ws.org/Vol-1035/iswc2013_poster_8.pdf), 
by Balduini M. and Della Valle E., for further details on the specification.

## Knowledge Base

The Knowledge Base must provide a REST interface compliant with the SPARQL protocol over HTTP (http://www.w3.org/TR/2013/REC-sparql11-http-rdf-update-20130321/).