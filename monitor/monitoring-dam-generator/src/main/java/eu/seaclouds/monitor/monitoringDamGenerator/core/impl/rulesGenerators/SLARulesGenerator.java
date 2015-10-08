package eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.core.interfaces.RulesGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.util.CustomizedApplications;
import eu.seaclouds.monitor.monitoringDamGenerator.util.RuleSchemaGenerator;
import it.polimi.tower4clouds.rules.*;

public class SLARulesGenerator implements RulesGenerator {

    private static final Logger logger = LoggerFactory
            .getLogger(SLARulesGenerator.class);
    
    public MonitoringRules generateMonitoringRules(Module module) {

        MonitoringRules toReturn = factory.createMonitoringRules();
        if(!CustomizedApplications.isCustomized(module.getModuleName())){
            if (module.existResponseTimeRequirement()) {
                
                logger.info("Module "+module.getModuleName()+" has a condition over the response time metric; generating SLA rule.");
                toReturn.getMonitoringRules().addAll(
                        generateResponseTimeSlaRule(module).getMonitoringRules());
            }

            if (module.existAvailabilityRequirement() & module.getDeploymentType().equals("compute")) {
                
                logger.info("Module "+module.getModuleName()+" has a condition over the availability metric; generating SLA rule.");
                toReturn.getMonitoringRules().addAll(
                        generateAvaialabilitySlaRule(module).getMonitoringRules());
            }

           
        }
        
        return toReturn;

    }

    private MonitoringRules generateResponseTimeSlaRule(
            Module module) {
        
        Map<String,String> parameters=new HashMap<String,String>();
        parameters.put("samplingProbability", "1");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("respTimeSLARule_" + module.getModuleName(),
                "10", 
                "10", 
                "InternalComponent",
                module.getModuleName(),
                "AvarageResponseTimeInternalComponent",
                parameters,
                "Average",
                "InternalComponent",
                "METRIC > " + module.getResponseTime(),
                "AvarageResponseTime_" + module.getModuleName()+"_Violation");

    }

    private MonitoringRules generateAvaialabilitySlaRule(
            Module module) {
        
        
        Map<String,String> parameters=new HashMap<String,String>();
        parameters.put("samplingTime", "10");
        parameters.put("port", "8080");
        parameters.put("path", "/index.html");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("appAvailableSLARule_" + module.getModuleName(),
                "10", 
                "10", 
                "InternalComponent",
                module.getModuleName(),
                "AppAvailable",
                parameters,
                "Average",
                "InternalComponent",
                "METRIC < " + module.getAvailability(),
                "AvarageAppAvailability_" + module.getModuleName()+ "_Violation");
        
    }

}
