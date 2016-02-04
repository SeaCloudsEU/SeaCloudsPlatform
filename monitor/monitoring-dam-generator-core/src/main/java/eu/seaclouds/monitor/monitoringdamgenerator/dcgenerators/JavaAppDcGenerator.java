package eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.DataCollectorGenerator;

public class JavaAppDcGenerator implements DataCollectorGenerator {

    private static Logger logger = LoggerFactory
            .getLogger(JavaAppDcGenerator.class);

    private static final String JAVA_APP_DC = "javaAppDc";
    private static final String START_SCRIPT_URL = "https://s3-eu-west-1.amazonaws.com/java-app-dc-start-script/installJavaAppDc.sh";

    public void addDataCollector(Module module, String monitoringManagerIp,
            int monitoringManagerPort, String influxdbIp, int influxdbPort) {

        if (module.isJavaApp()) {
            logger.info("Generating required deployment script for the java-app-level Data Collector.");

            Map<String, Object> dataCollector = this.generateDcNodeTemplate(this
                    .getRequiredEnvVars(module, monitoringManagerIp,
                            monitoringManagerPort, influxdbIp, influxdbPort), module);

            module.addDataCollector(dataCollector);           
        }


    }

    private Map<String, String> getRequiredEnvVars(Module module,
            String monitoringManagerIp, int monitoringManagerPort,
            String influxdbIp, int influxdbPort) {

        Map<String, String> toReturn = new HashMap<String, String>();

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_MANAGER_IP, monitoringManagerIp);

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT,
                String.valueOf(monitoringManagerPort));
        
        toReturn.put(MODACLOUDS_TOWER4CLOUDS_INFLUXDB_IP, monitoringManagerIp);

        toReturn.put(MODACLOUDS_TOWER4CLOUDS_INFLUXDB_PORT,
                String.valueOf(monitoringManagerPort));
        
        toReturn.put("MODULE_ID",
                module.getModuleName());

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
        properties.put("install.latch",
                "$brooklyn:component(\"" + module.getModuleName()
                        + "\").attributeWhenReady(\"service.isUp\")");

        properties.put("env", requiredEnvVars);

        dataCollector.put("type", "seaclouds.nodes.Datacollector");
        dataCollector.put("properties", properties);
        dataCollector.put("requirements", requirements);
        dataCollector.put("interfaces", interfaces);

        toSet.put(JAVA_APP_DC+"_"+module.getModuleName(), dataCollector);

        return toSet;
    }
}
