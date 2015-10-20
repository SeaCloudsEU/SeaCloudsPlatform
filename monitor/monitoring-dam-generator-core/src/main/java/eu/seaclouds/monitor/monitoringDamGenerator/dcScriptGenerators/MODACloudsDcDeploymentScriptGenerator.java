package eu.seaclouds.monitor.monitoringDamGenerator.dcScriptGenerators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringDamGenerator.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.dcScriptGenerators.DataCollectorDeploymentScriptGenerator;

public class MODACloudsDcDeploymentScriptGenerator implements
        DataCollectorDeploymentScriptGenerator {

    private static Logger logger = LoggerFactory
            .getLogger(MODACloudsDcDeploymentScriptGenerator.class);

    private static final String DATA_COLLECTOR_VERSION = "2.0.4";
    private static final String DATA_COLLECTOR_ARTIFACT_URL = "https://github.com/imperial-modaclouds/modaclouds-data-collectors/releases/download/"
            + DATA_COLLECTOR_VERSION
            + "/data-collector-2"
            + DATA_COLLECTOR_VERSION
            + ".jar";
    private static final String sigarDownloadUrl = "http://sourceforge.net/projects/sigar/files/sigar/1.6/hyperic-sigar-1.6.4.zip/download?use_mirror=switch";

    public String generateDataCollectorDeploymentScript(Module module,
            String monitoringManagerIp, String monitoringManagerPort) {

        logger.info("Generating required deployment script for the MODAClouds Data Collector.");

        StringBuilder sb = new StringBuilder();

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_MANAGER_IP + "="
                + monitoringManagerIp + "\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT + "="
                + monitoringManagerPort + "\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD + "=10\n");
        sb.append("export "
                + MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD + "=25\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_VM_TYPE + "="
                + module.getHost().getHostName() + "\n");
        
        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE
                + "=" + module.getModuleName() + "\n");

        //ID should be replaced with something that is machine/component instance dependent (the IP could work for both)
        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_VM_ID + "="
                + module.getHost().getHostName() + "_ID\n");
        
        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID
                + "=" + module.getModuleName() + "_ID\n");
        
        sb.append("wget -O modaclouds-data-collector.jar "
                + DATA_COLLECTOR_ARTIFACT_URL + "\n");
        sb.append("wget -O hyperic-sigar-1.6.4.zip " + sigarDownloadUrl + "\n");
        sb.append("unzip hyperic-sigar-1.6.4.zip\n");
        sb.append("nohup java -Djava.library.path=./hyperic-sigar-1.6.4/sigar-bin/lib/ -jar modaclouds-data-collector.jar tower4clouds > dc.out 2>&1 &\n");
        sb.append("echo $! > $PID_FILE\n");

        return sb.toString();

    }

}
