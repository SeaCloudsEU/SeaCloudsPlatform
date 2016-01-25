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

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.io.StringReader;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.sla.generator.ContextInfo.Validity;

public class SlaInfo {
    private static final Validity DEFAULT_VALIDITY = new Validity(2, 0, 0);
    
    private ContextInfo context;
    private Map<String, MonitoringRules> monitoringRules;
    
    public SlaInfo(ContextInfo context, Map<String, MonitoringRules> monitoringRules) {
        this.context = context;
        this.monitoringRules = monitoringRules;
    }

    public ContextInfo getContext() {
        return context;
    }
    
    public Map<String, MonitoringRules> getMonitoringRules() {
        return monitoringRules;
    }
    
    public static class SlaInfoBuilder {
        
        @Resource
        private RulesExtractor rulesExtractor;

        public SlaInfoBuilder() {
        }
        
        public SlaInfoBuilder(RulesExtractor rulesExtractor) {
            this.rulesExtractor = rulesExtractor;
        }
        
        public SlaInfo build(String dam) {
            
            /*
             * Using snakeyaml for the moment. Change to use A4C tosca parser if need to read the topology. 
             */
            try {
                
                Yaml yaml = new Yaml();
                @SuppressWarnings("rawtypes")
                Map doc = (Map) yaml.load(new StringReader(dam));

                Map<String, MonitoringRules> monitoringRules = rulesExtractor.extract(doc);

                return buildImpl(doc, monitoringRules);
            } catch (JAXBException e) {
                
                throw new SlaGeneratorException(e.getMessage(), e);
            }
        }

        public SlaInfo build(String dam, String rulesString) {
            
            try {
                
                Yaml yaml = new Yaml();
                @SuppressWarnings("rawtypes")
                Map doc = (Map) yaml.load(new StringReader(dam));

                Map<String, MonitoringRules> monitoringRules = rulesExtractor.fromSerializedRules(rulesString);
                
                return buildImpl(doc, monitoringRules);
            } catch (JAXBException e) {
                
                throw new SlaGeneratorException(e.getMessage(), e);
            }
        }

        private SlaInfo buildImpl(@SuppressWarnings("rawtypes") Map doc,
                Map<String, MonitoringRules> monitoringRules) {
            String provider = "seaclouds";
            String consumer = "user";
            String service = doc.containsKey("description")? (String) doc.get("description") : "service";
            ContextInfo context = new ContextInfo(provider, consumer, service, DEFAULT_VALIDITY);
            
            return new SlaInfo(context, monitoringRules);
        }
        
    }
}
