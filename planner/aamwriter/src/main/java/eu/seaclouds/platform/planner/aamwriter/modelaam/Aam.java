/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.platform.planner.aamwriter.modelaam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Aam extends LinkedHashMap {
    private static final long serialVersionUID = 1L;

    private TopologyTemplate topology_template;
    private Map<String, NodeType> nodeTypes = new LinkedHashMap();
    private Map<String, Operation> groups = new LinkedHashMap();

    public Aam(String description) {
        if (description == null) {
            throw new IllegalArgumentException("description cannot be null");
        }
        if ("".equals(description)) {
            description = "<description_not_set>";
        }
        this.put("tosca_definitions_version", "tosca_simple_yaml_1_0_0_wd03");
        this.put("description", description);
        ArrayList<String> imports = new ArrayList();
        this.put("imports", imports);
        imports.add("tosca-normative-types:1.0.0.wd06-SNAPSHOT");
        
        topology_template = new TopologyTemplate();
        this.put("topology_template", topology_template);

        this.put("node_types", nodeTypes);
        this.put("groups", groups);
    }
    
    public TopologyTemplate getTopologyTemplate() {
        return topology_template;
    }
    
    public void addNodeType(NodeType node) {
        this.nodeTypes.put(node.getName(), node);
    }
    
    public void addOperation(Operation operation) {
        this.groups.put(operation.getName(), operation);
    }
    
    public Operation getOperation(String operationName) {
        return groups.get(operationName);
    }
    
    public NodeType getNodeType(String name) {
        return nodeTypes.get(name);
    }
    
}