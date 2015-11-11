package eu.seaclouds.monitor.monitoringDamGenerator.dcScriptGenerators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.monitoringDamGenerator.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.dcScriptGenerators.DataCollectorDeploymentScriptGenerator;

public class JavaAppLevelDcDeploymentScriptGenerator implements
        DataCollectorDeploymentScriptGenerator {

    private static Logger logger = LoggerFactory
            .getLogger(JavaAppLevelDcDeploymentScriptGenerator.class);

    public String generateDataCollectorDeploymentScript(Module module,
            String monitoringManagerIp, String monitoringManagerPort) {

        logger.info("Generating required deployment script for the java-app-level Data Collector.");

        StringBuilder sb = new StringBuilder();

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_MANAGER_IP + "="
                + monitoringManagerIp + "\n");

        sb.append("export " + MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT + "="
                + monitoringManagerPort + "\n");

        return sb.toString();

    }
}
