package eu.seaclouds.monitor.monitoringDamGenerator.core.interfaces;

import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.ObjectFactory;

public interface RulesGenerator {

    public static final ObjectFactory factory = new ObjectFactory();


    public MonitoringRules generateMonitoringRules(Module module);

}
