package eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.ObjectFactory;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;

public class InfrastructuralRulesGenerator {

    private static final Logger logger = LoggerFactory
            .getLogger(NuroRulesGenerator.class);

    public static final ObjectFactory factory = new ObjectFactory();

    public void generateMonitoringRules(Module module) {

        logger.info("Generating infrastructural level monitoring rules for host: "
                + module.getHost().getHostName());
        
        if(module.getHost().getDeploymentType().equals("IaaS")){
            MonitoringRules toAdd = factory.createMonitoringRules();

            toAdd.getMonitoringRules().addAll(
                    this.generateCpuUtilizationRules(module.getHost().getHostName()).getMonitoringRules());
            toAdd.getMonitoringRules().addAll(
                    this.generateRamUtilizationRules(module.getHost().getHostName()).getMonitoringRules());

            module.addApplicationMonitoringRules(toAdd);
        }

        
    }

    private MonitoringRules generateCpuUtilizationRules(String host) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("cpuRule___" + host,
                "10", "10", "VM", host, "CPUUtilization", parameters,
                "Average", "VM", null, "AverageCpuUtilization_" + host);

    }

    private MonitoringRules generateRamUtilizationRules(String host) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("ramRule___" + host,
                "10", "10", "VM", host, "MemUsed", parameters, "Average", "VM",
                null, "AverageRamUtilization_" + host);

    }
}
