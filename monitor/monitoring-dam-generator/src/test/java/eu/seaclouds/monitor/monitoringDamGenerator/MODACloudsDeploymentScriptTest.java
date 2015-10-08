package eu.seaclouds.monitor.monitoringDamGenerator;

public class MODACloudsDeploymentScriptTest {

    public static final String EXAMPLE_INPUT=""
      + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_IP=127.0.0.1\n"
      + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT=8170\n"
      + "export MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD=10\n"
      + "export MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD=25\n"
      + "export MODACLOUDS_TOWER4CLOUDS_VM_TYPE=Chat_VM\n"
      + "export MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE=Chat\n"
      + "export MODACLOUDS_TOWER4CLOUDS_VM_ID=$brooklyn:formatString(\"%s%s\", Chat_VM,$brooklyn:component(\"Chat\").attributeWhenReady(\"host.address\"))export MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID=$brooklyn:formatString(\"%s%s\", Chat,$brooklyn:component(\"Chat\").attributeWhenReady(\"host.address\"))wget -O modaclouds-data-collector.jar https://github.com/imperial-modaclouds/modaclouds-data-collectors/releases/download/2.0.4/data-collector-22.0.4.jar\n"
      + "wget -O hyperic-sigar-1.6.4.zip http://sourceforge.net/projects/sigar/files/sigar/1.6/hyperic-sigar-1.6.4.zip/download?use_mirror=switch\n"
      + "unzip hyperic-sigar-1.6.4.zip\n"
      + "nohup java -Djava.library.path=./hyperic-sigar-1.6.4/sigar-bin/lib/ -jar modaclouds-data-collector.jar tower4clouds > dc.out 2>&1 &\n"
      + "echo $! > $PID_FILE\n";
}
