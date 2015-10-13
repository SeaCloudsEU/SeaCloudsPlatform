package eu.seaclouds.monitor.monitoringDamGenerator;

import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;
import eu.seaclouds.monitor.monitoringDamGenerator.service.MonitoringDamGenerator;
import org.json.JSONArray;

public class MainTest {

    private static final String[] modulesTest = { "Chat", "MessageDatabase" };

    private static final double testResponseTimeThreshold = 2000.0;

    @SuppressWarnings("unchecked")
    @Test
    public void mainTest() throws Exception {

        Map<String, Object> monitoringInformation;
        Map<String, Object> fields;
        MonitoringRules rules;
        Map<String, String> parametersTest;
        JAXBContext context = JAXBContext
                .newInstance("it.polimi.tower4clouds.rules");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Yaml yaml = new Yaml();
        MonitoringDamGenerator service = new MonitoringDamGenerator();

        JSONArray jsonArray = new JSONArray(
                service.receiveData(new FileInputStream(
                        "resources/webchatADP.yml")));
        

        Assert.assertEquals(jsonArray.length(), 2);

        for (int i = 0; i < jsonArray.length(); i++) {

            monitoringInformation = (Map<String, Object>) yaml.load(jsonArray
                    .getString(i));

            fields = (Map<String, Object>) monitoringInformation
                    .get("monitoringInformation_" + modulesTest[i]);

            for (String field : fields.keySet()) {
                if (field.equals("members")) {

                    for (String member : (List<String>) fields.get(field)) {
                        Assert.assertEquals(member, modulesTest[i]);
                    }

                } else if (field.equals("policies")) {

                    for (Map<String, Object> policy : (List<Map<String, Object>>) fields
                            .get(field)) {

                        for (String key : policy.keySet()) {

                            if (key.equals("monitoringRules")) {

                                rules = (MonitoringRules) unmarshaller
                                        .unmarshal(new ByteArrayInputStream(
                                                policy.get(key).toString()
                                                        .getBytes()));

                                parametersTest = new HashMap<String, String>();

                                for (MonitoringRule rule : rules
                                        .getMonitoringRules()) {
                                    switch (rule.getId().split("_")[0]) {

                                    case "respTimeRule":
                                        parametersTest.clear();
                                        parametersTest.put(
                                                "samplingProbability", "1");
                                        TestUtils.testRule(
                                                rule,
                                                "10",
                                                "10",
                                                "InternalComponent",
                                                modulesTest[i],
                                                "AvarageResponseTimeInternalComponent",
                                                parametersTest, null, null,
                                                null, "AvarageResponseTime_"
                                                        + modulesTest[i]);
                                        break;
                                        
                                    case "respTimeSLARule":
                                        parametersTest.clear();
                                        parametersTest.put(
                                                "samplingProbability", "1");
                                        TestUtils.testRule(
                                                rule,
                                                "10",
                                                "10",
                                                "InternalComponent",
                                                modulesTest[i],
                                                "AvarageResponseTimeInternalComponent",
                                                parametersTest,
                                                null,
                                                null,
                                                "METRIC > "
                                                        + testResponseTimeThreshold,
                                                "AvarageResponseTime_"
                                                        + modulesTest[i]
                                                        + "_Violation");
                                        break;
                                        
                                    case "checkStatusRule":
                                        parametersTest.clear();
                                        parametersTest
                                                .put("samplingTime", "10");
                                        TestUtils.testRule(rule, "10", "10",
                                                "InternalComponent",
                                                modulesTest[i], "isAppOnFire",
                                                parametersTest, null, null,
                                                null, "ApplicationStatus_"
                                                        + modulesTest[i]);
                                        break;
                                        
                                    case "cpuRule":
                                        parametersTest.clear();
                                        parametersTest
                                                .put("samplingTime", "10");
                                        parametersTest.put(
                                                "samplingProbability", "1");
                                        TestUtils.testRule(rule, "10", "10", "VM",
                                                modulesTest[i] + "_VM",
                                                "CPUUtilization",
                                                parametersTest, "Average",
                                                "VM", null,
                                                "AverageCpuUtilization_"
                                                        + modulesTest[i]);
                                        break;
                                        
                                    case "ramRule":
                                        parametersTest.clear();
                                        parametersTest
                                                .put("samplingTime", "10");
                                        parametersTest.put(
                                                "samplingProbability", "1");
                                        TestUtils.testRule(rule, "10", "10", "VM",
                                                modulesTest[i] + "_VM",
                                                "MemUsed", parametersTest,
                                                "Average", "VM", null,
                                                "AverageRamUtilization_"
                                                        + modulesTest[i]);
                                        break;
                                        
                                    default:
                                        throw new Exception(
                                                "Test failure: monitoring rule with not valid id found.");
                                    }
                                }
                            } else if (key
                                    .equals("dataCollectorsDeploymentScripts")) {
                                
                                List<Map<String,String>> deploymentScripts = (List<Map<String, String>>) policy.get(key);
                                
                                for(Map<String,String> script: deploymentScripts){
                                    TestUtils.testDcDeploymentScript(modulesTest[i], script);
                                }

                            }
                        }
                    }
                }
            }
        }
    }

}
