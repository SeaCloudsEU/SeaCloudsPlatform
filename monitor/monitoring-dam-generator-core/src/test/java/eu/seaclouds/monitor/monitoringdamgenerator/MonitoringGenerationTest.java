
package eu.seaclouds.monitor.monitoringdamgenerator;

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
    private static final double EXPECTED_AVAILABILITY_THRESHOLD = 0.98;
    private static final String TEST_PORT = "8080";


    @Test
    public void monitoringInformationGenerationTest() throws Exception {

        Map<String, String> parametersTest;
        
        MonitoringDamGenerator service = new MonitoringDamGenerator(new URL("http://127.0.0.1:8170"), new URL("http://127.0.0.1:8083"));

        MonitoringInfo returned = service
                .generateMonitoringInfo(readFile("resources/adp_example.yml",
                        Charset.defaultCharset()));

        parametersTest = new HashMap<String, String>();

        for (MonitoringRule rule : returned.getApplicationMonitoringRules().getMonitoringRules()) {
            
            String ruleType = rule.getId().split("___")[0];
            String nodeTemplateId = rule.getId().split("___")[1];
            
            if(nodeTemplateId.equals("www")){
    
                switch (ruleType) {
    
                case "respTimeRule":
                    parametersTest.clear();
                    parametersTest.put("samplingProbability", "1");
                    TestUtils.testRule(rule, "10", "10", "InternalComponent",
                            nodeTemplateId,
                            "AverageResponseTimeInternalComponent",
                            parametersTest, null, null, null,
                            "ResponseTime_" + nodeTemplateId);
                    break;
    
                case "respTimeSLARule":
                    parametersTest.clear();
                    parametersTest.put("samplingProbability", "1");
                    TestUtils.testRule(rule, "10", "10", "InternalComponent",
                            nodeTemplateId,
                            "AverageResponseTimeInternalComponent",
                            parametersTest, null, null, "METRIC > "
                                    + EXPECTED_RESPONSE_TIME_THRESHOLD,
                            "ResponseTimeViolation_" + nodeTemplateId);
                    break;
                    
                case "appAvailableSLARule":
                    parametersTest.clear();
                    parametersTest.put("samplingProbability", "1");
                    TestUtils.testRule(rule, "10", "10", "InternalComponent",
                            nodeTemplateId,
                            "PaaSModuleAvailability",
                            parametersTest, null, null, "METRIC < "
                                    + EXPECTED_AVAILABILITY_THRESHOLD,
                            "AppAvailabilityViolation_" + nodeTemplateId);
                    break;
    
                case "cpuRule":
                    parametersTest.clear();
                    parametersTest.put("samplingTime", "10");
                    parametersTest.put("samplingProbability", "1");
                    TestUtils.testRule(rule, "10", "10", "VM",
                            nodeTemplateId, "CPUUtilization",
                            parametersTest, "Average", "VM", null,
                            "AverageCpuUtilization_" + nodeTemplateId);
                    break;
    
                case "ramRule":
                    parametersTest.clear();
                    parametersTest.put("samplingTime", "10");
                    parametersTest.put("samplingProbability", "1");
                    TestUtils.testRule(rule, "10", "10", "VM",
                            nodeTemplateId, "MemUsed",
                            parametersTest, "Average", "VM", null,
                            "AverageRamUtilization_" + nodeTemplateId);
                    break;
                }
            }
        }
        
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
