package eu.seaclouds.monitor.monitoringDamGenerator.rulesGenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.ObjectFactory;
import eu.seaclouds.monitor.monitoringDamGenerator.util.RuleSchemaGenerator;

public class InfrastructuralRulesGenerator{
    
    private static final Logger logger = LoggerFactory
            .getLogger(NuroRulesGenerator.class);
    
    public static final ObjectFactory factory = new ObjectFactory();
    
    public MonitoringRules generateMonitoringRules(String host) {
        
        logger.info("Generating infrastructural level monitoring rules for host: " + host);

        MonitoringRules toReturn = factory.createMonitoringRules();

        toReturn.getMonitoringRules().addAll(
                this.generateCpuUtilizationRules(host)
                        .getMonitoringRules());
        toReturn.getMonitoringRules().addAll(
                this.generateRamUtilizationRules(host)
                        .getMonitoringRules());
        

        return toReturn;
    }
    
    private MonitoringRules generateCpuUtilizationRules(String host) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "cpuRule_" + host, "10", "10", "VM",
                host , "CPUUtilization", parameters,
                "Average", "VM", null,
                "AverageCpuUtilization_" + host);

    }

    private MonitoringRules generateRamUtilizationRules(String host) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema(
                "ramRule_" + host, "10", "10", "VM",
                host , "MemUsed", parameters,
                "Average", "VM", null,
                "AverageRamUtilization_" + host);

    }
}
