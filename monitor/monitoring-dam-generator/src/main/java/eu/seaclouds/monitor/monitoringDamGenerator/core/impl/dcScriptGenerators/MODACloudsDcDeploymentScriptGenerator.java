package eu.seaclouds.monitor.monitoringDamGenerator.core.impl.dcScriptGenerators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringDamGenerator.core.interfaces.DataCollectorDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;

public class MODACloudsDcDeploymentScriptGenerator implements
        DataCollectorDeploymentScriptGenerator {

    private static Logger logger = LoggerFactory
            .getLogger(MODACloudsDcDeploymentScriptGenerator.class);

    private static final String dataCollectorVersion = "2.0.4";
    private static final String dataCollectorArtifactURL = "https://github.com/imperial-modaclouds/modaclouds-data-collectors/releases/download/"
            + dataCollectorVersion
            + "/data-collector-2"
            + dataCollectorVersion
            + ".jar";
    private static final String sigarDownloadUrl = "http://sourceforge.net/projects/sigar/files/sigar/1.6/hyperic-sigar-1.6.4.zip/download?use_mirror=switch";

    public String generateDataCollectorDeploymentScript(Module moduleInfo,
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
                + moduleInfo.getModuleName() + "_VM\n");
        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE
                + "=" + moduleInfo.getModuleName() + "\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_VM_ID
                + "=$brooklyn:formatString(\"%s%s\", "
                + moduleInfo.getModuleName() + "_VM,"
                + "$brooklyn:component(\"" + moduleInfo.getModuleName()
                + "\").attributeWhenReady(\"host.address\"))");
        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID
                + "=$brooklyn:formatString(\"%s%s\", "
                + moduleInfo.getModuleName() + "," + "$brooklyn:component(\""
                + moduleInfo.getModuleName()
                + "\").attributeWhenReady(\"host.address\"))");

        sb.append("wget -O modaclouds-data-collector.jar "
                + dataCollectorArtifactURL + "\n");
        sb.append("wget -O hyperic-sigar-1.6.4.zip " + sigarDownloadUrl + "\n");
        sb.append("unzip hyperic-sigar-1.6.4.zip\n");
        sb.append("nohup java -Djava.library.path=./hyperic-sigar-1.6.4/sigar-bin/lib/ -jar modaclouds-data-collector.jar tower4clouds > dc.out 2>&1 &\n");
        sb.append("echo $! > $PID_FILE\n");

        return sb.toString();

    }

}
