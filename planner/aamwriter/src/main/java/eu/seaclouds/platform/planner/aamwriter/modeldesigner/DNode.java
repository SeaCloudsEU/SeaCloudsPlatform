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
package eu.seaclouds.platform.planner.aamwriter.modeldesigner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class DNode {
    public static final class Attributes {
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String PROPERTIES = "properties";
    }
    
    public static final DNode NOT_FOUND = new DNode("[null]", "[null]");

    @SuppressWarnings("unused")
    private DGraph graph;
    private String name;
    private String type;
    private Map<String, Object> properties;
    
    private String language;
    private String minVersion;
    private String maxVersion;
    private String category;
    private String artifact;
    private String infrastructure;
    private String container;
    private String numCpus;
    private String diskSize;
    private String benchmarkResponseTime;
    private String benchmarkPlatform;
    private List<Map<String, String>> qos;

    public DNode(JSONObject jnode, DGraph graph) {
        this.graph = graph;

        this.name = (String) jnode.get(Attributes.NAME);
        this.type = (String) jnode.get(Attributes.TYPE);

        this.properties = new HashMap<String, Object>();
        this.properties.putAll(readProperties(jnode));
    }

    private DNode(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readProperties(JSONObject jnode) {
        Map<String, Object> map =  (Map<String, Object>)jnode.get(Attributes.PROPERTIES);
        
        language = extractStringFromMap("language", map);
        minVersion = extractStringFromMap("min_version", map);
        maxVersion = extractStringFromMap("max_version", map);
        category = extractStringFromMap("category", map);
        artifact = extractStringFromMap("artifact", map);
        infrastructure = extractStringFromMap("infrastructure", map);
        container = extractStringFromMap("container", map);
        numCpus = extractStringFromMap("num_cpus", map);
        diskSize = extractStringFromMap("disk_size", map);
        benchmarkResponseTime = extractStringFromMap("benchmark_rt", map);
        benchmarkPlatform = extractStringFromMap("benchmark_platform", map);
        qos = (List) extractQosFromMap("qos", map);
        return map;
    }
    
    private String extractStringFromMap(String key, Map<String, Object> map) {
        String value = (String) map.remove(key);
        
        return (value == null)? "" : value;
    }

    private List<Object> extractListFromMap(String key, Map<String, Object> map) {
        List<Object> value = (List) map.remove(key);
        
        return (value == null)? Collections.EMPTY_LIST : value;
    }
    
    private List<Map<String, String>> extractQosFromMap(String key, Map<String, Object> map) {
        List<Map<String, Object>> qos = (List) map.remove(key);
        
        if (qos != null) {
            for (Map<String, Object> listitem : qos) {
                for (String mapkey : listitem.keySet()) {
                    Object mapvalue = listitem.get(mapkey);
                    listitem.put(mapkey, mapvalue.toString());
                }
            }
        }
        return (qos == null)? Collections.EMPTY_LIST : qos;
    }
    
    @Override
    public String toString() {
        return String.format("Node [name=%s, type=%s, properties=%s]", 
                name, type, properties);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DNode other = (DNode) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
    
    public Map<String, Object> getOtherProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    public String getLanguage() {
        return language;
    }
    
    public String getMinVersion() {
        return minVersion;
    }
    
    public String getMaxVersion() {
        return maxVersion;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getArtifact() {
        return artifact;
    }
    
    public String getInfrastructure() {
        return infrastructure;
    }
    
    public String getContainer() {
        return container;
    }
    
    public String getNumCpus() {
        return numCpus;
    }
    
    public String getDiskSize() {
        return diskSize;
    }
    
    public String getBenchmarkResponseTime() {
        return benchmarkResponseTime;
    }
    
    public String getBenchmarkPlatform() {
        return benchmarkPlatform;
    }
    
    public List<Map<String, String>> getQos() {
        return qos;
    }
}
