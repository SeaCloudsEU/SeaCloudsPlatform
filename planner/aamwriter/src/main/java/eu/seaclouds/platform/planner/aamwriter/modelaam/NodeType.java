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

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NodeType extends LinkedHashMap {
    
    public static final class Attributes {
        public static final String PROPERTIES = "properties";
        public static final String DERIVEDFROM = "derived_from";
    }

    private static final long serialVersionUID = 1L;
    
    String name;
    Map _properties = new LinkedHashMap();
    
    public NodeType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDerivedFrom(String value) {
        this.put(Attributes.DERIVEDFROM, value);
    }
    
    public void addProperty(String name, Object value) {
        this.properties().put(name, value);
    }
    
    public void addResourceTypeConstraintProperty(String value) {
        if ("".equals(value)) {
            return;
        }
        Constraint constraint = new Constraint(Constraint.Names.EQ, value);
        addConstrainedProperty("resource_type", constraint);
    }
    
    public void addSupportItemConstraintProperty(String supportItem) {
        if ("".equals(supportItem)) {
            return;
        }
        Constraint constraint = new Constraint(Constraint.Names.EQ, Boolean.TRUE);
        addConstrainedProperty(supportItem, constraint);
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
        return this._properties;
    }

}