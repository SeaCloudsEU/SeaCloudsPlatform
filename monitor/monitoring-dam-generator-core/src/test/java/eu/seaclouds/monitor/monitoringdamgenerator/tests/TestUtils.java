package eu.seaclouds.monitor.monitoringdamgenerator.tests;

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
   
}
