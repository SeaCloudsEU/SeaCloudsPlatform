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
package eu.seaclouds.platform.sla.generator;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.yaml.snakeyaml.Yaml;

import it.polimi.tower4clouds.rules.MonitoringRules;

/**
 * Extracts the monitoring rules from a DAM or directly from a serialized representation. 
 * The rules are returned as a map 
 * (key: component name, value: MonitoringRules object) 
 */
@SuppressWarnings("rawtypes")
public class RulesExtractor {

    private static final String MEMBERS = "members";
    private static final String GROUPS = "groups";
    private static final String POLICIES = "policies";
    private static final String MONITORING_RULES = "monitoringRules";

    public RulesExtractor() {
    }

    public Map<String, MonitoringRules> extract(String dam) throws JAXBException {
    
        StringReader reader = new StringReader(dam);
        return extract(reader);
    }
    
    public Map<String, MonitoringRules> extract(Reader reader) throws JAXBException {
        
        Yaml yaml = new Yaml();
        Object doc = yaml.load(reader);
        
        return extract((Map)doc);
    }
    
    public Map<String, MonitoringRules> extract(Map yaml) throws JAXBException {
        
        Map<String, MonitoringRules> result = new HashMap<String, MonitoringRules>();
        Map groups = (Map)yaml.get(GROUPS);
        
        for (Object item : groups.values()) {
            
            Map group = (Map) item;
            String memberName = getMemberName(group);
            List groupPolicies = getPolicies(group);
            String rulesString = getRules(groupPolicies);
            
            if (!"".equals(rulesString)) {
                MonitoringRules rules = deserializeRules(rulesString);
                result.put(memberName, rules);
            }
        }
        return result;
    }

    public Map<String, MonitoringRules> fromSerializedRules(String rulesString) throws JAXBException {
        Map<String, MonitoringRules> result = new HashMap<String, MonitoringRules>();
        MonitoringRules rules = deserializeRules(rulesString);
        result.put("application", rules);
        
        return result;
    }
    
    private MonitoringRules deserializeRules(String rulesString)
            throws JAXBException {
        StringReader reader = new StringReader(rulesString);
        MonitoringRules rules = JaxbUtils.load(MonitoringRules.class, reader);
        return rules;
    }
    
    private List getPolicies(Map group) {
        
        List result;
        
        if (group.containsKey(POLICIES)) {
            result = (List) group.get(POLICIES);
        }
        else {
            result = Collections.<Object>emptyList();
        }
        return result;
    }
    
    private String getRules(List policies) {
        
        for (Object item : policies) {
            Map policy = (Map) item;

            if (policy.containsKey(MONITORING_RULES)) {
                return (String) policy.get(MONITORING_RULES);
            }
        }
        return "";
    }
    
    private String getMemberName(Map group) {
        
        @SuppressWarnings("unchecked")
        List<String> members = (List<String>) group.get(MEMBERS);
        for (String member : members) {
            return member;
        }
        return "";
    }
}
