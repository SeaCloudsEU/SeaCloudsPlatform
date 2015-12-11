package eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.DataCollectorGenerator;

public class MODACloudsDcGenerator implements DataCollectorGenerator {

    private static Logger logger = LoggerFactory
            .getLogger(MODACloudsDcGenerator.class);

    private static final String MODACLOUDS_DC_ID = "modacloudsDc";
    private static final String START_SCRIPT_URL = "http://start.sh/";

    public void addDataCollector(Module module, String monitoringManagerIp,
            int monitoringManagerPort) {

        if(module.getHost().getDeploymentType().equals("IaaS")){

            logger.info("Generating required deployment script for the MODAClouds Data Collector.");

            Map<String, Object> dataCollector = this.generateDcNodeTemplate(this
                    .getRequiredEnvVars(module, monitoringManagerIp,
                            monitoringManagerPort), module);

            module.addDataCollector(dataCollector);
        }

    }

    private List<Map<String, String>> getRequiredEnvVars(Module module,
            String monitoringManagerIp, int monitoringManagerPort) {

        List<Map<String, String>> toReturn = new ArrayList<Map<String, String>>();
        Map<String, String> temp;

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_MANAGER_IP, monitoringManagerIp);
        toReturn.add(temp);

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT,
                String.valueOf(monitoringManagerPort));
        toReturn.add(temp);

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD, "10");
        toReturn.add(temp);

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD, "25");
        toReturn.add(temp);

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE,
                module.getModuleName());
        toReturn.add(temp);

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID,
                module.getModuleName() + "_ID");
        toReturn.add(temp);

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_VM_TYPE, module.getHost()
                .getHostName());
        toReturn.add(temp);

        temp = new HashMap<String, String>();
        temp.put(MODACLOUDS_TOWER4CLOUDS_VM_ID, module.getHost().getHostName()
                + "_ID");
        toReturn.add(temp);

        return toReturn;

    }

    private Map<String, Object> generateDcNodeTemplate(
            List<Map<String, String>> requiredEnvVars, Module module) {

        Map<String, Object> toSet;
        Map<String, Object> dataCollector;
        Map<String, Object> properties;
        Map<String, Object> requirements;
        Map<String, Object> interfaces;
        Map<String, Object> startCommand;

        toSet = new HashMap<String, Object>();
        dataCollector = new HashMap<String, Object>();
        properties = new HashMap<String, Object>();
        requirements = new HashMap<String, Object>();
        interfaces = new HashMap<String, Object>();
        startCommand = new HashMap<String, Object>();

        startCommand.put("start", START_SCRIPT_URL);
        interfaces.put("Standard", startCommand);
        requirements.put("host", module.getHost().getHostName());
        properties.put("install.latch",
                "$brooklyn:component(\"" + module.getModuleName()
                        + "\").attributeWhenReady(\"service.isUp\")");

        properties.put("env", requiredEnvVars);

        dataCollector.put("type", "seaclouds.nodes.Datacollector");
        dataCollector.put("properties", properties);
        dataCollector.put("requirements", requirements);
        dataCollector.put("interfaces", interfaces);

        toSet.put(MODACLOUDS_DC_ID+"_"+module.getModuleName(), dataCollector);

        return toSet;
    }

}
