package eu.seaclouds.monitor.monitoringDamGenerator;

import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.Parameter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class TestUtils {
    
    private static Logger logger = LoggerFactory.getLogger(TestUtils.class);


    public static void testRule(MonitoringRule toTest, String timestepTest,
            String timewindowTest, String targetClassTest,
            String targetTypeTest, String monitoredMetricTest,
            Map<String, String> parametersTest, String aggregationFunctionTest,
            String aggregationLevelTest, String conditionTest,
            String outputMetricTest) {

        Assert.assertEquals(toTest.getTimeStep(), timestepTest);
        Assert.assertEquals(toTest.getTimeWindow(), timewindowTest);

        if (targetTypeTest != null) {
            Assert.assertEquals(toTest.getMonitoredTargets()
                    .getMonitoredTargets().get(0).getType(), targetTypeTest);
        } else {
            logger.error("The test must specify a valid monitored target type test.");
            throw new RuntimeException(
                    "The test must specify a valid monitored target type test.");
        }

        if (targetClassTest != null) {
            Assert.assertEquals(toTest.getMonitoredTargets()
                    .getMonitoredTargets().get(0).getClazz(), targetClassTest);

        } else {
            Assert.assertEquals(toTest.getMonitoredTargets()
                    .getMonitoredTargets().get(0).getClazz(),
                    "InternalComponent");
        }

        if (monitoredMetricTest != null) {
            Assert.assertEquals(toTest.getCollectedMetric().getMetricName(),
                    monitoredMetricTest);
        } else {
            logger.error("The test must specify a valid metric name test.");
            throw new RuntimeException(
                    "The test must specify a valid metric name test.");
        }

        if (parametersTest != null) {
            Assert.assertEquals(toTest.getCollectedMetric().getParameters()
                    .size(), parametersTest.size());
            for (Parameter p : toTest.getCollectedMetric().getParameters()) {
                for (String key : parametersTest.keySet()) {
                    if (p.getName().equals(key)) {
                        Assert.assertEquals(p.getValue(),
                                parametersTest.get(key));
                    }
                }
            }
        }

        if (aggregationFunctionTest != null & aggregationLevelTest != null) {
            Assert.assertEquals(toTest.getMetricAggregation()
                    .getAggregateFunction(), aggregationFunctionTest);
            Assert.assertEquals(toTest.getMetricAggregation()
                    .getGroupingClass(), aggregationLevelTest);
        } else if (aggregationFunctionTest == null
                & aggregationLevelTest != null) {
            Assert.assertNull(toTest.getMetricAggregation());
        } else if (aggregationFunctionTest != null
                & aggregationLevelTest == null) {
            Assert.assertNull(toTest.getMetricAggregation());
        }

        if (conditionTest != null) {
            Assert.assertEquals(toTest.getCondition().getValue(), conditionTest);
        }

        if (outputMetricTest != null) {
            for (Parameter p : toTest.getActions().getActions().get(0)
                    .getParameters()) {
                if (p.getName().equals("metric")) {
                    Assert.assertEquals(p.getValue(), outputMetricTest);
                }
            }
        } else {
            logger.error("The test must specify a valid output metric test.");
            throw new RuntimeException(
                    "The test must specify a valid output metric test.");
        }

    }
    
    
    public static void testDcDeploymentScript(String moduleName, Map<String,String> deploymentScript) throws Exception{
        
        String MODACLOUDS_DC_DEPLOYMENT_SCRIPT_TEST = ""
                + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_IP=127.0.0.1\n"
                + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT=8170\n"
                + "export MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD=10\n"
                + "export MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD=25\n"
                + "export MODACLOUDS_TOWER4CLOUDS_VM_TYPE="+moduleName+"_VM\n"
                + "export MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE="+moduleName+"\n"
                + "export MODACLOUDS_TOWER4CLOUDS_VM_ID=$brooklyn:formatString(\"%s%s\", "+moduleName+"_VM,$brooklyn:component(\""+moduleName+"\").attributeWhenReady(\"host.address\"))export MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID=$brooklyn:formatString(\"%s%s\", "+moduleName+",$brooklyn:component(\""+moduleName+"\").attributeWhenReady(\"host.address\"))wget -O modaclouds-data-collector.jar https://github.com/imperial-modaclouds/modaclouds-data-collectors/releases/download/2.0.4/data-collector-22.0.4.jar\n"
                + "wget -O hyperic-sigar-1.6.4.zip http://sourceforge.net/projects/sigar/files/sigar/1.6/hyperic-sigar-1.6.4.zip/download?use_mirror=switch\n"
                + "unzip hyperic-sigar-1.6.4.zip\n"
                + "nohup java -Djava.library.path=./hyperic-sigar-1.6.4/sigar-bin/lib/ -jar modaclouds-data-collector.jar tower4clouds > dc.out 2>&1 &\n"
                + "echo $! > $PID_FILE\n";

        String JAVA_DC_DEPLOYMENT_SCRIPT_TEST = ""
                + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_IP=127.0.0.1\n"
                + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT=8170\n";
        
        String NURO_DC_DEPLOYMENT_SCRIPT_TEST = ""
                + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_IP=127.0.0.1\n"
                + "export MODACLOUDS_TOWER4CLOUDS_MANAGER_PORT=8170\n"
                + "export MODACLOUDS_TOWER4CLOUDS_DC_SYNC_PERIOD=10\n"
                + "export MODACLOUDS_TOWER4CLOUDS_RESOURCES_KEEP_ALIVE_PERIOD=25\n"
                + "export MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_TYPE="+moduleName+"\n"
                + "export MODACLOUDS_TOWER4CLOUDS_INTERNAL_COMPONENT_ID="+moduleName+"_ID\n"
                + "wget -O nuro-data-collector.jar https://www.dropbox.com/s/vq7g8btiolslbge/nuro-data-collector-0.1.0-SNAPSHOT-jar-with-dependencies.jar\n"
                + "nohup java -jar nuro-data-collector.jar > nuro_dc.out 2>&1 &\n"
                + "echo $! > $PID_FILE\n";

        Assert.assertEquals(deploymentScript.keySet().size(), 1);
        
        for(String key: deploymentScript.keySet()){
            switch (key) {
            case "modacloudsDeploymentScript":
                Assert.assertEquals(deploymentScript.get(key),
                        MODACLOUDS_DC_DEPLOYMENT_SCRIPT_TEST);
                break;                             
            case "javaAppLevelDcDeploymentScript":
                Assert.assertEquals(deploymentScript.get(key),
                        JAVA_DC_DEPLOYMENT_SCRIPT_TEST);
                break;
                
            case "nuroDcDeploymentScript":
                Assert.assertEquals(deploymentScript.get(key),
                        NURO_DC_DEPLOYMENT_SCRIPT_TEST);
                break;
            default:
                throw new Exception(
                        "Test failure: deployment script with not valid id found.");
        }
        }
    }
}
