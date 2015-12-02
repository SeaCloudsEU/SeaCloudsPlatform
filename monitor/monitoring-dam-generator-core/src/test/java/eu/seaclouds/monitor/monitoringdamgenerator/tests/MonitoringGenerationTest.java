
package eu.seaclouds.monitor.monitoringdamgenerator.tests;

import it.polimi.tower4clouds.rules.MonitoringRule;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;

public class MonitoringGenerationTest {


    private static final double EXPECTED_RESPONSE_TIME_THRESHOLD = 2000.0;
    private static final double EXPECTED_AVAILABILITY_THRESHOLD = 0.96;
    private static final String TEST_PORT = "8080";


    @Test
    public void monitoringInformationGenerationTest() throws Exception {

        Map<String, String> parametersTest;
        
        MonitoringDamGenerator service = new MonitoringDamGenerator(new URL("http://127.0.0.1:8170"));

        MonitoringInfo returned = service
                .generateMonitoringInfo(readFile("resources/currentAtosAdpFromOptimizer.yml",
                        Charset.defaultCharset()));

        parametersTest = new HashMap<String, String>();

        for (MonitoringRule rule : returned.getApplicationMonitoringRules().getMonitoringRules()) {
            
            String ruleType = rule.getId().split("___")[0];
            String nodeTemplateId = rule.getId().split("___")[1];
            
            switch (ruleType) {

            case "respTimeRule":
                parametersTest.clear();
                parametersTest.put("samplingProbability", "1");
                TestUtils.testRule(rule, "10", "10", "InternalComponent",
                        nodeTemplateId,
                        "AvarageResponseTimeInternalComponent",
                        parametersTest, null, null, null,
                        "AvarageResponseTime_" + nodeTemplateId);
                break;

            case "respTimeSLARule":
                parametersTest.clear();
                parametersTest.put("samplingProbability", "1");
                TestUtils.testRule(rule, "10", "10", "InternalComponent",
                        nodeTemplateId,
                        "AvarageResponseTimeInternalComponent",
                        parametersTest, null, null, "METRIC > "
                                + EXPECTED_RESPONSE_TIME_THRESHOLD,
                        "AvarageResponseTimeViolation_" + nodeTemplateId);
                break;
                
            case "appAvailableSLARule":
                parametersTest.clear();
                parametersTest.put("samplingProbability", "1");
                parametersTest.put("path", "/");
                parametersTest.put("port", TEST_PORT);
                TestUtils.testRule(rule, "10", "10", "InternalComponent",
                        nodeTemplateId,
                        "AppAvailable",
                        parametersTest, "Average", "InternalComponent", "METRIC < "
                                + EXPECTED_AVAILABILITY_THRESHOLD,
                        "AvarageAppAvailabilityViolation_" + nodeTemplateId);
                break;

            case "checkStatusRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "10");
                TestUtils.testRule(rule, "10", "10", "InternalComponent",
                        nodeTemplateId, "isAppOnFire", parametersTest,
                        null, null, null,
                        "ApplicationStatus_" + nodeTemplateId);
                break;

            case "cpuRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "10");
                TestUtils.testRule(rule, "10", "10", "VM",
                        nodeTemplateId, "CPUUtilization",
                        parametersTest, "Average", "VM", null,
                        "AverageCpuUtilization_" + nodeTemplateId);
                break;

            case "ramRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "10");
                TestUtils.testRule(rule, "10", "10", "VM",
                        nodeTemplateId, "MemUsed",
                        parametersTest, "Average", "VM", null,
                        "AverageRamUtilization_" + nodeTemplateId);
                break;

            default:
                throw new Exception(
                        "Test failure: monitoring rule with not valid id found.");
            }
        }
        
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
