package eu.seaclouds.monitor.monitoringDamGenerator;

import it.polimi.tower4clouds.rules.Action;
import it.polimi.tower4clouds.rules.MonitoredTarget;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.Parameter;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.seaclouds.monitor.monitoringDamGenerator.core.adpParsing.Module;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.dcScriptGenerators.NuroDcDeploymentScriptGenerator;
import eu.seaclouds.monitor.monitoringDamGenerator.core.impl.rulesGenerators.NuroRulesGenerator;

public class NuroMonitoringGeneratorTest {
    
    
    private static final String test_module_name="NuroApplication";
    private static final double testResponseTimeThreshold=2000.0;
    private static final double testAvailabilityThreshold=0.998;
    
    @Test
    public void nuroTest() throws Exception {
        NuroDcDeploymentScriptGenerator nuroScriptGenerator=new NuroDcDeploymentScriptGenerator();
        NuroRulesGenerator nuroRulesGenerator= new NuroRulesGenerator();
        
        Assert.assertEquals(NuroDcDeploymentScriptTest.EXAMPLE_INPUT, nuroScriptGenerator.generateDataCollectorDeploymentScript(null, "127.0.0.1", "8170"));
        Module module=new Module();
        module.setModuleName("NuroApplication");
        module.setAvailability(testAvailabilityThreshold);
        module.setResponseTimeMillis(testResponseTimeThreshold);
        
        MonitoringRules rules=nuroRulesGenerator.generateMonitoringRules(module);
        
        for(MonitoringRule rule: rules.getMonitoringRules()){
            String ruleId=rule.getId();
            switch(ruleId){
            case "nuroThirtySecondsSlaRuntimeRule": 
                Assert.assertEquals(rule.getTimeStep(), "30");
                Assert.assertEquals(rule.getTimeWindow(), "30");
                Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                    Assert.assertEquals(target.getClazz(), "InternalComponent");
                    Assert.assertEquals(target.getType(), test_module_name);
                }
                Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "NUROServerLastTenSecondsAverageRunTime");
                Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                for(Parameter p: rule.getCollectedMetric().getParameters()){
                    if(p.getName().equals("samplingTime")){
                        Assert.assertEquals(p.getValue(), "5");
                    }else if(p.getName().equals("samplingProbability")){
                        Assert.assertEquals(p.getValue(), "1");
                    }
                }
                Assert.assertEquals(rule.getMetricAggregation().getAggregateFunction(), "Average");
                Assert.assertEquals(rule.getMetricAggregation().getGroupingClass(), "InternalComponent");
                Assert.assertEquals(rule.getActions().getActions().size(), 1);
                for(Action a: rule.getActions().getActions()){
                    Assert.assertEquals(a.getName(), "OutputMetric");
                    for(Parameter p: a.getParameters()){
                        if(p.getName().equals("metric")){
                            Assert.assertEquals(p.getValue(), "NUROServerLastThirtySecondsAverageRunTime_Violation");
                        }
                    }
                }
                break;
            case "nuroThirtySecondsRuntimeRule": 
                Assert.assertEquals(rule.getTimeStep(), "30");
                Assert.assertEquals(rule.getTimeWindow(), "30");
                Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                    Assert.assertEquals(target.getClazz(), "InternalComponent");
                    Assert.assertEquals(target.getType(), test_module_name);
                }
                Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "NUROServerLastTenSecondsAverageRunTime");
                Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                for(Parameter p: rule.getCollectedMetric().getParameters()){
                    if(p.getName().equals("samplingTime")){
                        Assert.assertEquals(p.getValue(), "5");
                    }else if(p.getName().equals("samplingProbability")){
                        Assert.assertEquals(p.getValue(), "1");
                    }
                }
                Assert.assertEquals(rule.getMetricAggregation().getAggregateFunction(), "Average");
                Assert.assertEquals(rule.getMetricAggregation().getGroupingClass(), "InternalComponent");
                Assert.assertEquals(rule.getActions().getActions().size(), 1);
                for(Action a: rule.getActions().getActions()){
                    Assert.assertEquals(a.getName(), "OutputMetric");
                    for(Parameter p: a.getParameters()){
                        if(p.getName().equals("metric")){
                            Assert.assertEquals(p.getValue(), "NUROServerLastThirtySecondsAverageRunTime");
                        }
                    }
                }
                break;
            case "nuroThirtySecondsPlayerCountRule": 
                Assert.assertEquals(rule.getTimeStep(), "30");
                Assert.assertEquals(rule.getTimeWindow(), "30");
                Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                    Assert.assertEquals(target.getClazz(), "InternalComponent");
                    Assert.assertEquals(target.getType(), test_module_name);
                }
                Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "NUROServerLastTenSecondsPlayerCount");
                Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                for(Parameter p: rule.getCollectedMetric().getParameters()){
                    if(p.getName().equals("samplingTime")){
                        Assert.assertEquals(p.getValue(), "10");
                    }else if(p.getName().equals("samplingProbability")){
                        Assert.assertEquals(p.getValue(), "1");
                    }
                }
                Assert.assertEquals(rule.getMetricAggregation().getAggregateFunction(), "Sum");
                Assert.assertEquals(rule.getMetricAggregation().getGroupingClass(), "InternalComponent");
                Assert.assertEquals(rule.getActions().getActions().size(), 1);
                for(Action a: rule.getActions().getActions()){
                    Assert.assertEquals(a.getName(), "OutputMetric");
                    for(Parameter p: a.getParameters()){
                        if(p.getName().equals("metric")){
                            Assert.assertEquals(p.getValue(), "NUROServerLastThirtySecondsPlayerCount");
                        }
                    }
                }
                break;
            case "nuroThirtySecondsRequestCountRule": 
                Assert.assertEquals(rule.getTimeStep(), "30");
                Assert.assertEquals(rule.getTimeWindow(), "30");
                Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                    Assert.assertEquals(target.getClazz(), "InternalComponent");
                    Assert.assertEquals(target.getType(), test_module_name);
                }
                Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "NUROServerLastTenSecondsRequestCount");
                Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                for(Parameter p: rule.getCollectedMetric().getParameters()){
                    if(p.getName().equals("samplingTime")){
                        Assert.assertEquals(p.getValue(), "10");
                    }else if(p.getName().equals("samplingProbability")){
                        Assert.assertEquals(p.getValue(), "1");
                    }
                }
                Assert.assertEquals(rule.getMetricAggregation().getAggregateFunction(), "Sum");
                Assert.assertEquals(rule.getMetricAggregation().getGroupingClass(), "InternalComponent");
                Assert.assertEquals(rule.getActions().getActions().size(), 1);
                for(Action a: rule.getActions().getActions()){
                    Assert.assertEquals(a.getName(), "OutputMetric");
                    for(Parameter p: a.getParameters()){
                        if(p.getName().equals("metric")){
                            Assert.assertEquals(p.getValue(), "NUROServerLastThirtySecondsRequestCount");
                        }
                    }
                }
                break;
            case "nuroThirtySecondsThroughput": 
                Assert.assertEquals(rule.getTimeStep(), "30");
                Assert.assertEquals(rule.getTimeWindow(), "30");
                Assert.assertEquals(rule.getMonitoredTargets().getMonitoredTargets().size(), 1);
                for(MonitoredTarget target: rule.getMonitoredTargets().getMonitoredTargets()){
                    Assert.assertEquals(target.getClazz(), "InternalComponent");
                    Assert.assertEquals(target.getType(), test_module_name);
                }
                Assert.assertEquals(rule.getCollectedMetric().getMetricName(), "NUROServerLastTenSecondsAverageThroughput");
                Assert.assertEquals(rule.getCollectedMetric().getParameters().size(), 1);
                Assert.assertEquals(rule.getMetricAggregation().getAggregateFunction(), "Average");
                Assert.assertEquals(rule.getMetricAggregation().getGroupingClass(), "InternalComponent");
                Assert.assertEquals(rule.getActions().getActions().size(), 1);
                for(Action a: rule.getActions().getActions()){
                    Assert.assertEquals(a.getName(), "OutputMetric");
                    for(Parameter p: a.getParameters()){
                        if(p.getName().equals("metric")){
                            Assert.assertEquals(p.getValue(), "NUROServerLastThirtySecondsAverageThroughput");
                        }
                    }
                }
                break;
            default: 
                throw new Exception("Test failure: monitoring rule with not valid id found.");
        }
    }
    }
}
