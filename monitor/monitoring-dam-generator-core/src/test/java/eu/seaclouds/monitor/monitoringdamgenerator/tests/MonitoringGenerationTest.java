
package eu.seaclouds.monitor.monitoringdamgenerator.tests;

import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringDamGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;

public class MonitoringGenerationTest {


    private static final double EXPECTED_RESPONSE_TIME_THRESHOLD = 2000.0;

    @Test
    public void mainTest() throws Exception {

        MonitoringRules rules;
        Map<String, String> parametersTest;
        
        MonitoringDamGenerator service = new MonitoringDamGenerator(
                "127.0.0.1", "8170");

        List<Module> returned = service
                .generateMonitoringInfo(readFile("resources/currentAtosAdpFromOptimizer.yml",
                        Charset.defaultCharset()));

        for (Module i : returned) {
            rules = i.getRules();
            
            if(i.getHost().getRules()!=null){
                rules.getMonitoringRules().addAll(i.getHost().getRules().getMonitoringRules());
            }

            parametersTest = new HashMap<String, String>();

            for (MonitoringRule rule : rules.getMonitoringRules()) {
                switch (rule.getId().split("_")[0]) {

                case "respTimeRule":
                    parametersTest.clear();
                    parametersTest.put("samplingProbability", "1");
                    TestUtils.testRule(rule, "10", "10", "InternalComponent",
                            i.getModuleName(),
                            "AvarageResponseTimeInternalComponent",
                            parametersTest, null, null, null,
                            "AvarageResponseTime_" + i.getModuleName());
                    break;

                case "respTimeSLARule":
                    parametersTest.clear();
                    parametersTest.put("samplingProbability", "1");
                    TestUtils.testRule(rule, "10", "10", "InternalComponent",
                            i.getModuleName(),
                            "AvarageResponseTimeInternalComponent",
                            parametersTest, null, null, "METRIC > "
                                    + EXPECTED_RESPONSE_TIME_THRESHOLD,
                            "AvarageResponseTimeViolation_" + i.getModuleName());
                    break;

                case "checkStatusRule":
                    parametersTest.clear();
                    parametersTest.put("samplingTime", "10");
                    TestUtils.testRule(rule, "10", "10", "InternalComponent",
                            i.getModuleName(), "isAppOnFire", parametersTest,
                            null, null, null,
                            "ApplicationStatus_" + i.getModuleName());
                    break;

                case "cpuRule":
                    parametersTest.clear();
                    parametersTest.put("samplingTime", "10");
                    TestUtils.testRule(rule, "10", "10", "VM",
                            i.getHost().getHostName(), "CPUUtilization",
                            parametersTest, "Average", "VM", null,
                            "AverageCpuUtilization_" + i.getHost().getHostName());
                    break;

                case "ramRule":
                    parametersTest.clear();
                    parametersTest.put("samplingTime", "10");
                    TestUtils.testRule(rule, "10", "10", "VM",
                            i.getHost().getHostName(), "MemUsed",
                            parametersTest, "Average", "VM", null,
                            "AverageRamUtilization_" + i.getHost().getHostName());
                    break;

                default:
                    throw new Exception(
                            "Test failure: monitoring rule with not valid id found.");
                }
            }
        }
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
