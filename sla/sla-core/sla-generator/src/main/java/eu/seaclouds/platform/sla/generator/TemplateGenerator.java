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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.modaclouds.QosModels;
import eu.atos.sla.parser.data.wsag.AllTerms;
import eu.atos.sla.parser.data.wsag.Context;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.atos.sla.parser.data.wsag.KPITarget;
import eu.atos.sla.parser.data.wsag.ServiceDescriptionTerm;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;
import eu.atos.sla.parser.data.wsag.ServiceProperties;
import eu.atos.sla.parser.data.wsag.ServiceScope;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.parser.data.wsag.Terms;
import it.polimi.tower4clouds.rules.Condition;
import it.polimi.tower4clouds.rules.MonitoredTarget;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;

public class TemplateGenerator {
    private static Logger logger = LoggerFactory.getLogger(TemplateGenerator.class);

    private static String DEFAULT_SERVICE_NAME = "service";
    private static GuaranteeTerm NULL_GUARANTEE_TERM = new GuaranteeTerm();
    private static MonitoredTarget NULL_MONITORED_TARGET = new MonitoredTarget();

    static {
        NULL_MONITORED_TARGET.setType("");
        NULL_MONITORED_TARGET.setClazz("");
    }
    
    private Map<String, MonitoringRules> rules;
    private ContextInfo contextInfo;
    
    public TemplateGenerator(SlaInfo slaInfo) {
        
        this.rules = slaInfo.getMonitoringRules();
        this.contextInfo = slaInfo.getContext();
    }
    
    public Template generate() {
        
        Template result = new Template();
        result.setTemplateId(UUID.randomUUID().toString());
        Context context = buildContext(contextInfo);
        result.setContext(context);
        
        Terms terms = new Terms();
        AllTerms allTerms = new AllTerms();
        result.setTerms(terms);
        terms.setAllTerms(allTerms);

        ServiceDescriptionTerm sdt = new ServiceDescriptionTerm();
        allTerms.setServiceDescriptionTerm(sdt);
        
        List<ServiceProperties> serviceProperties = new ArrayList<>();
        allTerms.setServiceProperties(serviceProperties);
        
        List<GuaranteeTerm> gts = new ArrayList<>();
        allTerms.setGuaranteeTerms(gts);
        
        for (String moduleName : this.rules.keySet()) {
            MonitoringRules rules = this.rules.get(moduleName);
            
            for (MonitoringRule rule : rules.getMonitoringRules()) {
                
                GuaranteeTerm gt = generateGuaranteeTerm(rule);
                if (gt != NULL_GUARANTEE_TERM) {
                    gts.add(gt);
                }
            }
        }
        
        return result;
    }
    
    private Context buildContext(ContextInfo contextInfo) {
        
        Context result = new Context();
        
        result.setAgreementInitiator(contextInfo.getConsumer());
        result.setAgreementResponder(contextInfo.getProvider());
        result.setService(contextInfo.getService());
        result.setServiceProvider(Context.ServiceProvider.AGREEMENT_RESPONDER.toString());
        
        return result;
    }

    private GuaranteeTerm generateGuaranteeTerm(MonitoringRule rule) {
        
        logger.debug("Generate guaranteeTerm({}", rule.getId());

        GuaranteeTerm gt = NULL_GUARANTEE_TERM;
        String outputMetric = QosModels.getOutputMetric(rule);
        
        ServiceScope serviceScope = buildScope(rule); 
        String kpiName = serviceScope.getValue() + "/" + rule.getCollectedMetric().getMetricName();

        if (isSuitableRule(rule)) {
        
            gt = new GuaranteeTerm();
            
            gt.setName(rule.getId());
    
            gt.setServiceScope(serviceScope);
            
            ServiceLevelObjective slo = new ServiceLevelObjective();
            KPITarget kpi = new KPITarget();
            kpi.setKpiName(kpiName);
            kpi.setCustomServiceLevel(String.format(
                    "{\"constraint\": \"%s NOT_EXISTS\", \"qos\": \"%s\" }",
                    outputMetric,
                    buildQos(rule.getCondition())
                    ));
            slo.setKpitarget(kpi);
            gt.setServiceLevelObjetive(slo);
            
            /*
             * TODO gt = generateBusinessValueList(gt, rule); 
             */
        }
        
        return gt;
    }
    
    private ServiceScope buildScope(MonitoringRule rule) {
        ServiceScope result = new ServiceScope();
        MonitoredTarget target = getTarget(rule);
        
        String value = target.getType();
        
        result.setServiceName(DEFAULT_SERVICE_NAME);
        result.setValue(value);
        return result;
    }
    
    private boolean isSuitableRule(MonitoringRule rule) {
        
        return rule.getCondition() != null && !"".equals(rule.getCondition());
    }
    
    private MonitoredTarget getTarget(MonitoringRule rule) {
        
        if (rule.getMonitoredTargets() != null) {
            for (MonitoredTarget target : rule.getMonitoredTargets().getMonitoredTargets()) {
                return target;
            }
        }
        return NULL_MONITORED_TARGET;
    }
    
    private String buildQos(Condition condition) {
        String result = condition.getValue().
            replace(">", "LE").
            replace("<", "GE");
        
        return result;
    }

}
