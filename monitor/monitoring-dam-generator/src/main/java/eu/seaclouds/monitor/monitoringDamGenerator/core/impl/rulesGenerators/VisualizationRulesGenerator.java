package eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringDamGenerator.core.interfaces.RulesGenerator;
import it.polimi.tower4clouds.rules.*;
import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.util.CustomizedApplications;
import eu.seaclouds.monitor.monitoringDamGenerator.util.RuleSchemaGenerator;

public class VisualizationRulesGenerator implements RulesGenerator {

    private static final Logger logger = LoggerFactory
            .getLogger(VisualizationRulesGenerator.class);

    public MonitoringRules generateMonitoringRules(Module module) {

        MonitoringRules toReturn = factory.createMonitoringRules();
        if (!CustomizedApplications.isCustomized(module.getModuleName())) {

            if (module.getDeploymentType().equals("compute")) {

                logger.info("Module"
                        + module.getModuleName()
                        + " will be deployed on IaaS. Generating infrastructural level monitoring rules.");
                toReturn.getMonitoringRules().addAll(
                        this.generateCpuUtilizationRules(module)
                                .getMonitoringRules());
                toReturn.getMonitoringRules().addAll(
                        this.generateRamUtilizationRules(module)
                                .getMonitoringRules());
            }

            toReturn.getMonitoringRules().addAll(
                    this.generateModulesResponseTimeRules(module)
                            .getMonitoringRules());

        }

        return toReturn;
    }

    private MonitoringRules generateCpuUtilizationRules(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        parameters.put("samplingProbability", "1");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "cpuRule_" + module.getModuleName(), "10", "10", "VM",
                module.getModuleName() + "_VM", "CPUUtilization", parameters,
                "Average", "VM", null,
                "AverageCpuUtilization_" + module.getModuleName());

    }

    private MonitoringRules generateRamUtilizationRules(Module module) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        parameters.put("samplingProbability", "1");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "ramRule_" + module.getModuleName(), "10", "10", "VM",
                module.getModuleName() + "_VM", "MemUsed", parameters,
                "Average", "VM", null,
                "AverageRamUtilization_" + module.getModuleName());

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
