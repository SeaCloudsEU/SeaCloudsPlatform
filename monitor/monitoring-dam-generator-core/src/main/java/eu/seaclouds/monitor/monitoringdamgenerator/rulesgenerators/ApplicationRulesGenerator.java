package eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.ObjectFactory;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.util.RuleSchemaGenerator;

public class ApplicationRulesGenerator {
    
    private static final Logger logger = LoggerFactory
            .getLogger(ApplicationRulesGenerator.class);
    
    public static final ObjectFactory factory = new ObjectFactory();

    public MonitoringRules generateMonitoringRules(Module module) {
        
        MonitoringRules toReturn = factory.createMonitoringRules();
        
        logger.info("Generating application level monitoring rules for host: " + module.getModuleName());

        if(module.isJavaApp()){
            if (module.existResponseTimeRequirement()) {

                logger.info("Module "
                        + module.getModuleName()
                        + " has a condition over the response time metric; generating SLA rule.");
                toReturn.getMonitoringRules().addAll(
                        generateResponseTimeSlaRule(module)
                                .getMonitoringRules());
            }

            if (module.existAvailabilityRequirement()
                    & module.getHost().getDeploymentType().equals("IaaS")) {

                logger.info("Module "
                        + module.getModuleName()
                        + " has a condition over the availability metric; generating SLA rule.");
                toReturn.getMonitoringRules().addAll(
                        generateAvailabilitySlaRule(module)
                                .getMonitoringRules());
            }
            
            toReturn.getMonitoringRules().addAll(
                    generateModulesResponseTimeRules(module).getMonitoringRules());
        }
        
        
        
        toReturn.getMonitoringRules().addAll(
                generateCheckStatusRule(module).getMonitoringRules());
        
        return toReturn;

        
    }
    
    private MonitoringRules generateCheckStatusRule(Module module) {

        logger.info("Generating rules required for the replanning process for module "
                + module.getModuleName());

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("checkStatusRule_"
                + module.getModuleName(), "10", "10", "InternalComponent",
                module.getModuleName(), "isAppOnFire", parameters, null, null,
                null, "ApplicationStatus_" + module.getModuleName());

    }
    
    private MonitoringRules generateResponseTimeSlaRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingProbability", "1");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("respTimeSLARule_"
                + module.getModuleName(), "10", "10", "InternalComponent",
                module.getModuleName(), "AvarageResponseTimeInternalComponent",
                parameters, "Average", "InternalComponent", "METRIC > "
                        + module.getResponseTime(), "AvarageResponseTimeViolation_"
                        + module.getModuleName() + "");

    }

    private MonitoringRules generateAvailabilitySlaRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        //from where to get the next two parameters?
        parameters.put("port", "8080");
        parameters.put("path", "/index.html");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "appAvailableSLARule_" + module.getModuleName(), "10", "10",
                "InternalComponent", module.getModuleName(), "AppAvailable",
                parameters, "Average", "InternalComponent", "METRIC < "
                        + module.getAvailability(), "AvarageAppAvailabilityViolation_"
                        + module.getModuleName() + "");

    }
    
    private MonitoringRules generateModulesResponseTimeRules(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingProbability", "1");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("respTimeRule_"
                + module.getModuleName(), "10", "10", "InternalComponent",
                module.getModuleName(), "AvarageResponseTimeInternalComponent",
                parameters, null, null, null,
                "AvarageResponseTime_" + module.getModuleName());

    }


}
