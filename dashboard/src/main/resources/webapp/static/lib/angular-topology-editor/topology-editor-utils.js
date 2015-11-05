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
'use strict';
var TopologyEditorUtils = (function() {

    var brooklynToTopologyTranslator = function(brooklynEntity) {
        var parentTopology = {
            nodes: [],
            links: []
        }

        if (brooklynEntity) {
            var type = undefined;
            if(brooklynEntity.type.search("database") >= 0){
                type = "Database"
            }else if(brooklynEntity.type.search("webapp") >= 0){
                type = "WebApplication"
            }else if(brooklynEntity.type.search("SameServerEntity") >= 0){
                type = "Host"
            }else {
                type = "BasicApplication"
            }

            parentTopology.nodes.push(
                {
                    name: brooklynEntity.name,
                    label: brooklynEntity.name,
                    properties: {
                        status: brooklynEntity.serviceState
                    },
                    type : type
                })

            if (brooklynEntity.children) {
                brooklynEntity.children.forEach(function (childEntity) {
                    parentTopology.links.push({
                        source: brooklynEntity.name,
                        target: childEntity.name,
                        properties: {}
                    })

                    var childTopology = brooklynToTopologyTranslator(childEntity);
                    parentTopology.nodes = parentTopology.nodes.concat(childTopology.nodes);
                    parentTopology.links = parentTopology.links.concat(childTopology.links);
                })
            }
        }
        return parentTopology;
    }

    return {
        getTopologyFromEntities: brooklynToTopologyTranslator
    }
})()