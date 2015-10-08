package eu.seaclouds.monitor.monitoringDamGenerator;

import it.polimi.tower4clouds.rules.Action;
import it.polimi.tower4clouds.rules.MonitoredTarget;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.Parameter;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.monitor.monitoringDamGenerator.service.MonitoringDamGenerator;

import org.json.JSONArray;


public class MainTest 
{
    private Logger logger = LoggerFactory.getLogger(MainTest.class);    
    
    private static final String test_module_name="Chat";
    private static final double testResponseTimeThreshold=2000.0;
    private static final double testAvailabilityThreshold=0.998;

	
	
    @SuppressWarnings("unchecked")
    @Test
    public void mainTest() throws Exception {
        InputStream is;

        try {
            is = new FileInputStream("resources/webchatADP.yml");

            MonitoringDamGenerator service = new MonitoringDamGenerator();

            String response = service.receiveData(is);

            is.close();

            JSONArray jsonArray = new JSONArray(response);
            Yaml yaml = new Yaml();

            Assert.assertEquals(jsonArray.length(), 2);
            
            Map<String, Object> monitoringInformation = (Map<String, Object>) yaml
                    .load(jsonArray.getString(0));                          

            Map<String, Object> fields = (Map<String, Object>) monitoringInformation
                    .get("monitoringInformation_"+test_module_name);

            for (String field : fields.keySet()) {
                if (field.equals("members")) {
                    List<String> members = (List<String>) fields
                            .get(field);

                    for (String member : members) {
                        Assert.assertEquals(member, test_module_name);
                    }
                } else if (field.equals("policies")) {
                    List<Map<String, Object>> policies = (List<Map<String, Object>>) fields
                            .get(field);

                    for (Map<String, Object> policy : policies) {
                        for (String key : policy.keySet()) {

                            if(key.equals("monitoringRules")){
                                JAXBContext context;
                                context = JAXBContext
                                        .newInstance("it.polimi.tower4clouds.rules");
                                Unmarshaller unmarshaller = context.createUnmarshaller();
                                InputStream stream = new ByteArrayInputStream(policy.get(key).toString().getBytes());
                                MonitoringRules rules=(MonitoringRules) unmarshaller.unmarshal(stream);
                                for(MonitoringRule rule: rules.getMonitoringRules()){
                                    String ruleId=rule.getId();
                                    switch(ruleId){
                                   
                                    case "respTimeRule_"+test_module_name: 
                                        Assert.assertEquals(rule.getTimeStep(), "10");
                                        Assert.assertEquals(rule.getTimeWindow(), "10");
                                        Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                                        for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                                            Assert.assertEquals(target.getClazz(), "InternalComponent");
                                            Assert.assertEquals(target.getType(), test_module_name);
                                        }
                                        Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "AvarageResponseTimeInternalComponent");
                                        Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                                        Assert.assertEquals(rule.getActions().getActions().size(), 1);
                                        for(Action a: rule.getActions().getActions()){
                                            Assert.assertEquals(a.getName(), "OutputMetric");
                                            for(Parameter p: a.getParameters()){
                                                if(p.getName().equals("metric")){
                                                    Assert.assertEquals(p.getValue(), "AvarageResponseTime_"+test_module_name);
                                                }
                                            }
                                        }
                                        break;
                                    case "respTimeSLARule_"+test_module_name: 
                                        Assert.assertEquals(rule.getTimeStep(), "10");
                                        Assert.assertEquals(rule.getTimeWindow(), "10");
                                        Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                                        for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                                            Assert.assertEquals(target.getClazz(), "InternalComponent");
                                            Assert.assertEquals(target.getType(), test_module_name);
                                        }
                                        Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "AvarageResponseTimeInternalComponent");
                                        Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                                        Assert.assertEquals(rule.getCondition().getValue(), "METRIC > " + testResponseTimeThreshold);
                                        Assert.assertEquals(rule.getActions().getActions().size(), 1);
                                        for(Action a: rule.getActions().getActions()){
                                            Assert.assertEquals(a.getName(), "OutputMetric");
                                            for(Parameter p: a.getParameters()){
                                                if(p.getName().equals("metric")){
                                                    Assert.assertEquals(p.getValue(), "AvarageResponseTime_"+test_module_name+ "_Violation");
                                                }
                                            }
                                        }
                                        break;
                                    case "checkStatusRule_"+test_module_name: 
                                        Assert.assertEquals(rule.getTimeStep(), "10");
                                        Assert.assertEquals(rule.getTimeWindow(), "10");
                                        Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                                        for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                                            Assert.assertEquals(target.getClazz(), "InternalComponent");
                                            Assert.assertEquals(target.getType(), test_module_name);
                                        }
                                        Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "isAppOnFire");
                                        Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                                        for(Parameter p: rule.getCollectedMetric().getParameters()){
                                            if(p.getName().equals("samplingTime")){
                                                Assert.assertEquals(p.getValue(), "10");
                                            }
                                        }
                                        Assert.assertEquals(rule.getActions().getActions().size(), 1);
                                        for(Action a: rule.getActions().getActions()){
                                            Assert.assertEquals(a.getName(), "OutputMetric");
                                            for(Parameter p: a.getParameters()){
                                                if(p.getName().equals("metric")){
                                                    Assert.assertEquals(p.getValue(), "ApplicationStatus_"+test_module_name);
                                                }
                                            }
                                        }
                                        break;
                                        default: 
                                            throw new Exception("Test failure: monitoring rule with not valid id found.");
                                    }
                                    


                                }
                            } else if(key.equals("dataCollectorDeploymentScript")){
                                Assert.assertEquals(policy.get(key), MODACloudsDeploymentScriptTest.EXAMPLE_INPUT);
                            }
                        }
                    }
                }
            }

        

            

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

 		
 		
    }
    
}
