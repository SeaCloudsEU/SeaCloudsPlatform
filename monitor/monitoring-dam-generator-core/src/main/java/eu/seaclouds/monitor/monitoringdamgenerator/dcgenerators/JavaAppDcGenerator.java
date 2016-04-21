package eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Metrics;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.DataCollectorGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaAppDcGenerator implements DataCollectorGenerator {

    private static Logger logger = LoggerFactory
            .getLogger(JavaAppDcGenerator.class);

    private static final String MODULE_ID = "MODULE_ID";

    private Metrics metrics;
    
    public JavaAppDcGenerator(){
        List<String> toAdd = new ArrayList<String>();
        
        toAdd.add("ResponseTime");
        
        this.metrics = new Metrics(toAdd);
    }    
    
    public void addDataCollector(Module module, String monitoringManagerIp,
            int monitoringManagerPort, String influxdbIp, int influxdbPort) {

            logger.info("Generating required deployment script for the java-app-level Data Collector.");

            Map<String, String> variables = this
                    .getRequiredEnvVars(module, monitoringManagerIp,
                            monitoringManagerPort, influxdbIp, influxdbPort);
            
            for(String key : variables.keySet()){
                module.addMonitoringEnvVar(key, variables.get(key));
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
        
        toReturn.put(METRICS,
                metrics.toString());
        
        toReturn.put(MODULE_ID,
                module.getModuleName());

        return toReturn;

    }
}
