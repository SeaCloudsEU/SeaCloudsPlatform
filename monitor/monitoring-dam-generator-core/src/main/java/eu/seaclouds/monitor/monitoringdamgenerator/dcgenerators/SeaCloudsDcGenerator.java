package eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Metrics;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeaCloudsDcGenerator implements DataCollectorGenerator {
    
    private static Logger logger = LoggerFactory
            .getLogger(SeaCloudsDcGenerator.class);
    
    private static final String SEACLOUDS_DC_ID = "seacloudsDc";
    private static final String PHP_LANGUAGE = "PHP";
    private static final String START_SCRIPT_URL = "https://s3-eu-west-1.amazonaws.com/seaclouds-dc/installSeaCloudsDc.sh";
    private static final String MODULE_IP ="MODULE_IP";
    private static final String MODULE_PORT ="MODULE_PORT";
    private Metrics metrics;

    public SeaCloudsDcGenerator() {
        List<String> toAdd = new ArrayList<String>();
        
        toAdd.add("NUROServerLastThirtySecondsAverageRunTime");
        toAdd.add("NUROServerLastThirtySecondsPlayerCount");
        toAdd.add("NUROServerLastThirtySecondsRequestCount");
        toAdd.add("NUROServerLastThirtySecondsAverageThroughput");
        toAdd.add("AppAvailability");

        this.metrics = new Metrics(toAdd);
        
    }

    @Override
    public void addDataCollector(Module module, String monitoringManagerIp, int monitoringManagerPort,
            String influxdbIp, int influxdbPort) {

        logger.info("Generating required deployment script for the MODAClouds Data Collector.");
    
        Map<String, Object> dataCollector = this.generateDcNodeTemplate(this
                .getRequiredEnvVars(module, monitoringManagerIp,
                        monitoringManagerPort, influxdbIp, influxdbPort), module);
    
        module.addDataCollector(dataCollector);
    }
    
    private Map<String, String> getRequiredEnvVars(Module module,
            String monitoringManagerIp, int monitoringManagerPort,
            String influxdbIp, int influxdbPort) {

        Map<String, String> toReturn = new HashMap<String, String>();

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_MANAGER_IP, monitoringManagerIp);

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT,
                String.valueOf(monitoringManagerPort));
        
        toReturn.put(MODACLOUDS_TOWER4CLOUDS_INFLUXDB_IP, influxdbIp);

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_INFLUXDB_PORT,
                String.valueOf(influxdbPort));

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE,
                module.getModuleName());

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID,
                module.getModuleName() + "_ID");

        if(module.getLanguage().equals(PHP_LANGUAGE)){
            toReturn.put(METRICS,
                    metrics.toString());           
        } else{
            toReturn.put(METRICS,
                    new String("AppAvailability"));           
        }

        
        toReturn.put(MODULE_IP,
                "$brooklyn:component(\"" + module.getModuleName()
                + "\").attributeWhenReady(\"host.address\")");
        
        toReturn.put(MODULE_PORT,
                module.getPort());

        return toReturn;

    }

    private Map<String, Object> generateDcNodeTemplate(
            Map<String, String> requiredEnvVars, Module module) {

        Map<String, Object> toSet;
        Map<String, Object> dataCollector;
        Map<String, Object> properties;
        List<Map<String, Object>> requirements;
        Map<String, Object> requirement;
        Map<String, Object> interfaces;
        Map<String, Object> startCommand;

        toSet = new HashMap<String, Object>();
        dataCollector = new HashMap<String, Object>();
        properties = new HashMap<String, Object>();
        requirements = new ArrayList<Map<String, Object>>();
        requirement = new HashMap<String, Object>();
        interfaces = new HashMap<String, Object>();
        startCommand = new HashMap<String, Object>();

        startCommand.put("start", START_SCRIPT_URL);
        
        interfaces.put("Standard", startCommand);
        requirement.put("host", module.getHost().getHostName());
        requirements.add(requirement);
        properties.put("installLatch",
                "$brooklyn:component(\"" + module.getModuleName()
                        + "\").attributeWhenReady(\"service.isUp\")");

        properties.put("shell.env", requiredEnvVars);

        dataCollector.put("type", "seaclouds.nodes.Datacollector");
        dataCollector.put("properties", properties);
        dataCollector.put("requirements", requirements);
        dataCollector.put("interfaces", interfaces);

        toSet.put(SEACLOUDS_DC_ID+"_"+module.getModuleName(), dataCollector);

        return toSet;
    }
        
}
