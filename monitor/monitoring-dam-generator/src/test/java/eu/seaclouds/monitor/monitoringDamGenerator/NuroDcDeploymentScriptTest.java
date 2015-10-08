package eu.seaclouds.monitor.monitoringDamGenerator;

public class NuroDcDeploymentScriptTest {
    public static final String EXAMPLE_INPUT=""
            + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_IP=127.0.0.1\n"
            + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT=8170\n"
            + "export MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD=10\n"
            + "export MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD=25\n"
            + "export MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE=NuroApplication\n"
            + "wget -O nuro-data-collector.jar https://www.dropbox.com/s/vq7g8btiolslbge/nuro-data-collector-0.1.0-SNAPSHOT-jar-with-dependencies.jar\n"
            + "nohup java -jar nuro-data-collector.jar > nuro_dc.out 2>&1 &\n"
            + "echo $! > $PID_FILE\n";
}
