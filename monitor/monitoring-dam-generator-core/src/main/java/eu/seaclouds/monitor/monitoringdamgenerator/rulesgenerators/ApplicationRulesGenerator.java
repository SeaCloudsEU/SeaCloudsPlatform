package eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.ObjectFactory;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;

public class ApplicationRulesGenerator {

    private static final Logger logger = LoggerFactory
            .getLogger(ApplicationRulesGenerator.class);

    public static final ObjectFactory factory = new ObjectFactory();

    public MonitoringRules generateMonitoringRules(Module module) {

        MonitoringRules toReturn = factory.createMonitoringRules();

        logger.info("Generating application level monitoring rules for host: "
                + module.getModuleName());

        if (module.getLanguage().equals("JAVA")) {
            
            toReturn.getMonitoringRules().addAll(
                    generateModulesResponseTimeRules(module)
                            .getMonitoringRules());
            
            if (module.existResponseTimeRequirement()) {

                logger.info("Module "
                        + module.getModuleName()
                        + " has a condition over the response time metric; generating SLA rule.");
                toReturn.getMonitoringRules().addAll(
                        generateResponseTimeSlaRule(module)
                                .getMonitoringRules());
            }
        }
        
        if (module.existAvailabilityRequirement()) {

            logger.info("Module "
                    + module.getModuleName()
                    + " has a condition over the availability metric; generating SLA rule.");
            toReturn.getMonitoringRules().addAll(
                    generateAvailabilitySlaRule(module)
                            .getMonitoringRules());
        }
        
        toReturn.getMonitoringRules().addAll(
                generateAvailabilityRule(module).getMonitoringRules());

        return toReturn;
    }

    private MonitoringRules generateResponseTimeSlaRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingProbability", "1");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("respTimeSLARule___"
                + module.getModuleName(), "10", "10", "InternalComponent",
                module.getModuleName(), "AverageResponseTimeInternalComponent",
                parameters, null, null, "METRIC > "
                        + module.getResponseTime(),
                "ResponseTimeViolation_" + module.getModuleName() + "");

    }

    private MonitoringRules generateAvailabilitySlaRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "appAvailableSLARule___" + module.getModuleName(), "10", "10",
                "InternalComponent", module.getModuleName(), "PaaSModuleAvailability",
                parameters, null, null, "METRIC < "
                        + module.getAvailability(),
                "AppAvailabilityViolation_" + module.getModuleName()
                        + "");

    }

    private MonitoringRules generateModulesResponseTimeRules(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingProbability", "1");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("respTimeRule___"
                + module.getModuleName(), "10", "10", "InternalComponent",
                module.getModuleName(), "AverageResponseTimeInternalComponent",
                parameters, null, null, null,
                "ResponseTime_" + module.getModuleName());

    }

    private MonitoringRules generateAvailabilityRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "appAvailableRule___" + module.getModuleName(), "10", "10",
                "InternalComponent", module.getModuleName(), "PaaSModuleAvailability",
                parameters, null, null, null,
                "AppAvailability_" + module.getModuleName());

    }
}
