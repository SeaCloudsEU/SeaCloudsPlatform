package eu.seaclouds.monitor.monitoringDamGenerator.rulesGenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.ObjectFactory;
import eu.seaclouds.monitor.monitoringDamGenerator.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.util.RuleSchemaGenerator;

public class NuroRulesGenerator {

    private static final Logger logger = LoggerFactory
            .getLogger(NuroRulesGenerator.class);
    
    public static final ObjectFactory factory = new ObjectFactory();

    public MonitoringRules generateMonitoringRules(Module module) {

        MonitoringRules toReturn = factory.createMonitoringRules();

        if (module.getModuleName().equals("NuroApplication")) {

            logger.info("NuroApplication module found. Generating all the custom monitoring rules");

            toReturn.getMonitoringRules().addAll(
                    this.generateThirtySecondsRuntimeRule(module)
                            .getMonitoringRules());
            toReturn.getMonitoringRules().addAll(
                    this.generateThirtySecondsPlayerCountRule(module)
                            .getMonitoringRules());
            toReturn.getMonitoringRules().addAll(
                    this.generateThirtySecondsRequestCountRule(module)
                            .getMonitoringRules());
            toReturn.getMonitoringRules().addAll(
                    this.generateThirtySecondsThroughputRule(module)
                            .getMonitoringRules());
            toReturn.getMonitoringRules().addAll(
                    this.generateNuroSlaRules(module).getMonitoringRules());
        }

        return toReturn;
    }

    private MonitoringRules generateNuroSlaRules(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "5");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "nuroThirtySecondsSlaRuntimeRule", "30", "30",
                "InternalComponent", module.getModuleName(),
                "NUROServerLastTenSecondsAverageRunTime", parameters,
                "Average", "InternalComponent",
                "METRIC > " + module.getResponseTime() / 1000,
                "NUROServerLastThirtySecondsAverageRunTime_Violation");

    }

    private MonitoringRules generateThirtySecondsRuntimeRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "5");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "nuroThirtySecondsRuntimeRule", "30", "30",
                "InternalComponent", module.getModuleName(),
                "NUROServerLastTenSecondsAverageRunTime", parameters,
                "Average", "InternalComponent", null,
                "NUROServerLastThirtySecondsAverageRunTime");

    }

    private MonitoringRules generateThirtySecondsPlayerCountRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "nuroThirtySecondsPlayerCountRule", "30", "30",
                "InternalComponent", module.getModuleName(),
                "NUROServerLastTenSecondsPlayerCount", parameters, "Sum",
                "InternalComponent", null,
                "NUROServerLastThirtySecondsPlayerCount");

    }

    private MonitoringRules generateThirtySecondsRequestCountRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "nuroThirtySecondsRequestCountRule", "30", "30",
                "InternalComponent", module.getModuleName(),
                "NUROServerLastTenSecondsRequestCount", parameters, "Sum",
                "InternalComponent", null,
                "NUROServerLastThirtySecondsRequestCount");

    }

    private MonitoringRules generateThirtySecondsThroughputRule(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "5");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "nuroThirtySecondsThroughput", "30", "30", "InternalComponent",
                module.getModuleName(), "NUROServerLastTenSecondsAverageThroughput",
                parameters, "Average", "InternalComponent", null,
                "NUROServerLastThirtySecondsAverageThroughput");
    }

}
