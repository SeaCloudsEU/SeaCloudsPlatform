package eu.seaclouds.monitor.monitoringdamgenerator.tests;

import java.util.HashMap;
import java.util.Map;

import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;

import org.testng.annotations.Test;

import eu.seaclouds.monitor.monitoringdamgenerator.adpparsing.Module;
import eu.seaclouds.monitor.monitoringdamgenerator.dcgenerators.NuroDcGenerator;
import eu.seaclouds.monitor.monitoringdamgenerator.rulesgenerators.NuroRulesGenerator;

public class NuroMonitoringGeneratorTest {

    private static final String NURO_MODULE_NAME = "NuroApplication";
    private static final double EXPECTED_RESPONSE_TIME_THRESHOLD = 2000.0;


    @Test
    public void nuroTest() throws Exception {
        NuroDcGenerator nuroScriptGenerator = new NuroDcGenerator();
        NuroRulesGenerator nuroRulesGenerator = new NuroRulesGenerator();
        MonitoringRules rules;
        Map<String, String> parametersTest;
        Map<String,String> nuroDcDeploymentScript = new HashMap<String, String>();
        
        Module nuroModule = new Module();
        nuroModule.setModuleName(NURO_MODULE_NAME);
        nuroModule.setResponseTimeMillis(EXPECTED_RESPONSE_TIME_THRESHOLD);
        
        /*
        nuroDcDeploymentScript.put("nuroDcDeploymentScript", nuroScriptGenerator
                .addDataCollector(nuroModule, "127.0.0.1",
                        8170));
        TestUtils.testDcDeploymentScript(nuroModule.getModuleName(),nuroDcDeploymentScript );
        */
        rules = nuroRulesGenerator.generateMonitoringRules(nuroModule);

        for (MonitoringRule rule : rules.getMonitoringRules()) {

            parametersTest = new HashMap<String, String>();

            switch (rule.getId()) {
            case "nuroThirtySecondsSlaRuntimeRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "5");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        nuroModule.getModuleName(),
                        "NUROServerLastTenSecondsAverageRunTime",
                        parametersTest, "Average", "InternalComponent",
                        "METRIC > " + nuroModule.getResponseTime() / 1000,
                        "NUROServerLastThirtySecondsAverageRunTime_Violation");
                break;
            case "nuroThirtySecondsRuntimeRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "5");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        nuroModule.getModuleName(),
                        "NUROServerLastTenSecondsAverageRunTime",
                        parametersTest, "Average", "InternalComponent", null,
                        "NUROServerLastThirtySecondsAverageRunTime");
                break;
            case "nuroThirtySecondsPlayerCountRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "10");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        nuroModule.getModuleName(),
                        "NUROServerLastTenSecondsPlayerCount", parametersTest,
                        "Sum", "InternalComponent", null,
                        "NUROServerLastThirtySecondsPlayerCount");
                break;
            case "nuroThirtySecondsRequestCountRule":
                parametersTest.clear();
                parametersTest.put("samplingTime", "10");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        nuroModule.getModuleName(),
                        "NUROServerLastTenSecondsRequestCount", parametersTest,
                        "Sum", "InternalComponent", null,
                        "NUROServerLastThirtySecondsRequestCount");
                break;
            case "nuroThirtySecondsThroughput":
                parametersTest.clear();
                parametersTest.put("samplingTime", "5");
                TestUtils.testRule(rule, "30", "30", "InternalComponent",
                        nuroModule.getModuleName(),
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
