package eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.polimi.tower4clouds.rules.MonitoringRules;
import eu.seaclouds.monitor.monitoringDamGenerator.core.interfaces.RulesGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.util.RuleSchemaGenerator;

public class ReconfigurationRulesGenerator implements RulesGenerator {

    private static final Logger logger = LoggerFactory
            .getLogger(ReconfigurationRulesGenerator.class);
    
    @Override
    public MonitoringRules generateMonitoringRules(Module module) {
        return generateCheckStatusRule(module);
    }

    private MonitoringRules generateCheckStatusRule(Module module) {
        
        logger.info("Generating rules required for the replanning process for module "+module.getModuleName());
        
        Map<String,String> parameters=new HashMap<String,String>();
        parameters.put("samplingTime", "10");
        return RuleSchemaGenerator.fillMonitoringRuleSchema("checkStatusRule_" + module.getModuleName(),
                "10", 
                "10", 
                "InternalComponent",
                module.getModuleName(),
                "isAppOnFire",
                parameters,
                null,
                null,
                null,
                "ApplicationStatus_" + module.getModuleName());
        
    }

}
