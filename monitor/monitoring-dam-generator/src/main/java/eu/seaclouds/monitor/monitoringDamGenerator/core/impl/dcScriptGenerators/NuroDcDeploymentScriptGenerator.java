package eu.seaclouds.monitor.monitoringDamGenerator.core.impl.dcScriptGenerators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.core.interfaces.DataCollectorDeploymentScriptGenerator;

public class NuroDcDeploymentScriptGenerator implements DataCollectorDeploymentScriptGenerator  {

    private static Logger logger = LoggerFactory.getLogger(NuroDcDeploymentScriptGenerator.class);

    private static final String dataCollectorVersion = "0.1.0-SNAPSHOT";
    private static final String dataCollectorArtifactURL = "https://www.dropbox.com/s/vq7g8btiolslbge/nuro-data-collector-"+dataCollectorVersion+"-jar-with-dependencies.jar";
    
    @Override
    public String generateDataCollectorDeploymentScript(
            Module moduleInfo, String monitoringManagerIp,
            String monitoringManagerPort) {

        logger.info("Generating required deployment script for the NURO Data Collector.");
        
        StringBuilder sb = new StringBuilder();

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_MANAGER_IP + "="
                + monitoringManagerIp + "\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT + "="
                + monitoringManagerPort + "\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD + "=10\n");
        sb.append("export "
                + MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD + "=25\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE
                + "=NuroApplication\n");
        
        sb.append("wget -O nuro-data-collector.jar "
                + dataCollectorArtifactURL + "\n");
        sb.append("nohup java -jar nuro-data-collector.jar > nuro_dc.out 2>&1 &\n");
        sb.append("echo $! > $PID_FILE\n");

        return sb.toString();
    }

}
