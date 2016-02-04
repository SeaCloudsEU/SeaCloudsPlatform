/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
"use strict";
var AdpPretifier = (function () {

    var isNodeType = function (str) {
        return str.includes("sc_req") || str.includes("seaclouds.nodes") && !isLocationType(str);
    }

    var isLocationType = function (str) {
        return str.includes("seaclouds.nodes.Compute") || str.includes("seaclouds.nodes.Platform");
    }

    var findLocationByName = function (location, index, array) {
        return this == location.name;
    }


    if (!log) {
        throw "AdpPrettifier requires loglevel.js"
    }

    if (!jsyaml) {
        throw "AdpPrettifier requires JS-YAML"
    }
    var adpToObject = function (adp) {
        var jsAdp = jsyaml.safeLoad(adp);
        var application = {
            name: undefined,
            locations: []
        };

        application.name = jsAdp.description;
        var services = [];

        var nodeTemplates = jsAdp.topology_template.node_templates;
        var nodeTypes = jsAdp.node_types;

        // node_templates are not a list of nodes but an object with several properties. This way iterates thought properties
        for (var nodeTemplateKey in nodeTemplates) {
            if (!nodeTemplates.hasOwnProperty(nodeTemplateKey)) {
                //The current property is not a direct property of jsAdp.topology_template.node_templates
                continue;
            }

            var nodeTemplate = nodeTemplates[nodeTemplateKey];

            if (isLocationType(nodeTemplate.type)) {
                // If it's a location we add an empty list of hosted services and we add into the application topology
                nodeTemplate.name = nodeTemplateKey;
                nodeTemplate.services = [];
                application.locations.push(nodeTemplate);
            } else if (isNodeType(nodeTemplate.type)) {
                // We will add the node_template the de pending work list
                nodeTemplate.name = nodeTemplateKey;

                // Adding parent nodetype
                nodeTemplate.parentType = nodeTypes[nodeTemplate.type].derived_from;
                services.push(nodeTemplate);
            } else {
                log.warn("Unrecognized Node Type");
                continue;
            }
        }

        services.forEach(function (service) {
            if (service.requirements) {
                service.requirements.forEach(function (requirement) {
                    if (requirement.host) {
                        var location = application.locations.find(findLocationByName, requirement.host);

                        if (!location) {
                            // Maybe an exception it's better for this
                            log.warn("The service is hosted on an unknown location, it will be ignored");
                        } else {
                            location.services.push(service);
                        }
                    }
                });
            } else {
                // Maybe an exception it's better for this
                log.warn("The service is has no requirments, it will be ignored. ");
            }

        });


        return application;
    }


    var adpToTopology = function (adp) {
        var application = AdpToObject(adp);

        var topology = {
            "nodes": [],
            "links": []
        };

        log.warn("This function is currently unsupported");
        return topology;
    }

    return {
        adpToObject: adpToObject,
        adpToTopology: adpToTopology
    };
})()
