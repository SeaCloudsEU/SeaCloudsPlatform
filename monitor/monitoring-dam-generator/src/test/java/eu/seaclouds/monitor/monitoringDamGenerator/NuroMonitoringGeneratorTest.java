package eu.seaclouds.monitor.monitoringDamGenerator;

import java.util.HashMap;
import java.util.Map;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.testng.annotations.Test;
import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.dcScriptGenerators.NuroDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators.NuroRulesGenerator;

public class NuroMonitoringGeneratorTest {

    private static final String test_module_name = "NuroApplication";
    private static final double testResponseTimeThreshold = 2000.0;
    private static final double testAvailabilityThreshold = 0.998;


    @Test
    public void nuroTest() throws Exception {
        NuroDcDeploymentScriptGenerator nuroScriptGenerator = new NuroDcDeploymentScriptGenerator();
        NuroRulesGenerator nuroRulesGenerator = new NuroRulesGenerator();
        MonitoringRules rules;
        Module module;
        Map<String, String> parametersTest;
        Map<String,String> nuroDcDeploymentScript = new HashMap<String, String>();
        
        nuroDcDeploymentScript.put("nuroDcDeploymentScript", nuroScriptGenerator
                .generateDataCollectorDeploymentScript(null, "127.0.0.1",
                        "8170"));
        TestUtils.testDcDeploymentScript(test_module_name,nuroDcDeploymentScript );

        module = new Module();
        module.setModuleName("NuroApplication");
        module.setAvailability(testAvailabilityThreshold);
        module.setResponseTimeMillis(testResponseTimeThreshold);
        
        rules = nuroRulesGenerator.generateMonitoringRules(module);

        for (MonitoringRule rule : rules.getMonitoringRules()) {

            parametersTest = new HashMap<String, String>();

            switch (rule.getId()) {
            case "nuroThirtySecondsSlaRuntimeRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "5");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        test_module_name,
                        "NUROServerLastTenSecondsAverageRunTime",
                        parametersTest, "Average", "InternalComponent",
                        "METRIC > " + testResponseTimeThreshold / 1000,
                        "NUROServerLastThirtySecondsAverageRunTime_Violation");
                break;
            case "nuroThirtySecondsRuntimeRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "5");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        test_module_name,
                        "NUROServerLastTenSecondsAverageRunTime",
                        parametersTest, "Average", "InternalComponent", null,
                        "NUROServerLastThirtySecondsAverageRunTime");
                break;
            case "nuroThirtySecondsPlayerCountRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "10");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        test_module_name,
                        "NUROServerLastTenSecondsPlayerCount", parametersTest,
                        "Sum", "InternalComponent", null,
                        "NUROServerLastThirtySecondsPlayerCount");
                break;
            case "nuroThirtySecondsRequestCountRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "10");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        test_module_name,
                        "NUROServerLastTenSecondsRequestCount", parametersTest,
                        "Sum", "InternalComponent", null,
                        "NUROServerLastThirtySecondsRequestCount");
                break;
            case "nuroThirtySecondsThroughput":
                parametersTest.clear();
                parametersTest.put("samplingTime", "5");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        test_module_name,
                        "NUROServerLastTenSecondsAverageThroughput",
                        parametersTest, "Average", "InternalComponent", null,
                        "NUROServerLastThirtySecondsAverageThroughput");
                break;
            default:
                throw new Exception(
                        "Test failure: monitoring rule with not valid id found.");
            }
        }
    }
}
