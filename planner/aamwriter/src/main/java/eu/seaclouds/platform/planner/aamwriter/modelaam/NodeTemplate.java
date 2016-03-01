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
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NodeTemplate extends LinkedHashMap {
    
    public static final class Attributes {
        public static final String TYPE = "type";
        public static final String PROPERTIES = "properties";
        public static final String ARTIFACTS = "artifacts";
        public static final String REQUIREMENTS = "requirements";
    }
    private static final long serialVersionUID = 1L;

    private String name;
    private Map _properties = new LinkedHashMap();
    private List<Map<String, String>> _artifacts = new ArrayList();
    private List<Map<String, Object>> _requirements = new ArrayList();
    
    public NodeTemplate(String name) {
        this.name = name;
        this.setType("<notset>");
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return (String) this.get(Attributes.TYPE);
    }
    
    public void setType(String type) {
        this.put(Attributes.TYPE, type);
    }
    
    public void setType(NodeType type) {
        this.setType(type.getName());
    }
    
    public void addProperties(Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            properties().put(entry.getKey(), entry.getValue());
        }
    }
    
    public void addProperty(String name, String value) {
        properties().put(name, value);
    }
    
    public void addArtifact(String name, String value, String type) {
        
        Map<String, String> artifact = new LinkedHashMap();
        artifact.put(name, value);
        artifact.put("type", type);
        
        artifacts().add(artifact);
    }
    
    public void setHostRequirement(NodeTemplate node) {
        
        for (Map<String, Object> item : _requirements) {
            if (item.containsKey("host")) {
                item.put("host", node.getName());
                return;
            }
        }
        Map<String, Object> requirement = new LinkedHashMap();
        requirement.put("host", node.getName());
        
        requirements().add(requirement);
    }
    
    /**
     * Add an endpoint requirement to a NodeTemplate
     * @return name given to the requirement
     */
    public String addConnectionRequirement(NodeTemplate target, String type, String varName) {
        Map<String, Object> requirement = new LinkedHashMap();
        
        String requirementName = "endpoint";
        requirement.put(requirementName, target.getName());
        requirement.put("type", type);
        if (!varName.isEmpty()) {
            Map<String, String> properties = new LinkedHashMap();
            properties.put("prop.name", varName);
            requirement.put("properties", properties);
        }
        requirements().add(requirement);
        
        return requirementName;
    }
    
    public void addConstrainedProperty(String propertyName, Constraint... constraints) {
        ConstraintProperty constraintProperty = new ConstraintProperty();
        for (Constraint constraint : constraints) {
            if (constraint != null) {
                constraintProperty.addConstraint(constraint);
            }
        }
        this.properties().put(propertyName, constraintProperty);
    }
    
    private Map properties() {
        if (!this.containsKey(Attributes.PROPERTIES)) {
            this.put(Attributes.PROPERTIES, _properties);
        }
        return _properties;
    }
    
    private List<Map<String, String>> artifacts() {
        if (!this.containsKey(Attributes.ARTIFACTS)) {
            this.put(Attributes.ARTIFACTS, _artifacts);
        }
        return _artifacts;
    }
    
    private List<Map<String, Object>> requirements() {
        if (!this.containsKey(Attributes.REQUIREMENTS)) {
            this.put(Attributes.REQUIREMENTS, _requirements);
        }
        return _requirements;
    }
}