package eu.seaclouds.monitor.monitoringDamGenerator.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polimi.tower4clouds.rules.Action;
import it.polimi.tower4clouds.rules.Actions;
import it.polimi.tower4clouds.rules.CollectedMetric;
import it.polimi.tower4clouds.rules.Condition;
import it.polimi.tower4clouds.rules.MonitoredTarget;
import it.polimi.tower4clouds.rules.MonitoredTargets;
import it.polimi.tower4clouds.rules.MonitoringMetricAggregation;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.MonitoringRules;
import it.polimi.tower4clouds.rules.ObjectFactory;
import it.polimi.tower4clouds.rules.Parameter;

public class RuleSchemaGenerator {

    private static final ObjectFactory factory = new ObjectFactory();
    private static final Logger logger = LoggerFactory
            .getLogger(RuleSchemaGenerator.class);


    public static MonitoringRules fillMonitoringRuleSchema(String ruleId,
            String timestep, String timewindow, String targetClass,
            String targetType, String monitoredMetric,
            Map<String, String> parameters, String aggregationFunction,
            String aggregationLevel, String condition, String outputMetric) {

        MonitoringRules toReturn = factory.createMonitoringRules();

        MonitoringRule rule;
        MonitoredTargets monitoredTargets;
        MonitoredTarget monitoredTarget;
        CollectedMetric collectedMetric;
        MonitoringMetricAggregation metricAggregation;
        Condition cond;
        Parameter parameter;
        Actions actions;
        Action action;
        Parameter metric;
        Parameter value;
        Parameter resourceId;

        rule = factory.createMonitoringRule();
        monitoredTargets = factory.createMonitoredTargets();
        monitoredTarget = factory.createMonitoredTarget();
        collectedMetric = factory.createCollectedMetric();
        metricAggregation = factory.createMonitoringMetricAggregation();
        cond= factory.createCondition();
        parameter = factory.createParameter();
        actions = factory.createActions();
        action = factory.createAction();
        metric = factory.createParameter();
        value = factory.createParameter();
        resourceId = factory.createParameter();

        if (ruleId == null) {
            logger.error("A rule must have an Id!");
            throw new RuntimeException("A valid ruleId must be specified");
        } else {
            rule.setId(ruleId);
        }

        if (timestep == null) {
            rule.setTimeStep("10");
        } else {
            rule.setTimeStep(timestep);
        }
        rule.setTimeWindow("30");

        if (timestep == null) {
            rule.setTimeWindow("10");
        } else {
            rule.setTimeWindow(timewindow);
        }

        monitoredTarget = factory.createMonitoredTarget();

        if (targetClass == null) {
            monitoredTarget.setClazz("InternalComponent");
        } else {
            monitoredTarget.setClazz(targetClass);
        }

        if (targetType == null) {
            logger.error("A valid target type must be specified");
            throw new RuntimeException("A valid target type must be specified");
        } else {
            monitoredTarget.setType(targetType);
        }

        monitoredTargets.getMonitoredTargets().add(monitoredTarget);

        if (monitoredMetric == null) {
            logger.error("A valid metric must be specified");
            throw new RuntimeException("A valid metric must be specified");
        } else {
            collectedMetric.setMetricName(monitoredMetric);
        }

        if(parameters!=null){
            for (String key : parameters.keySet()) {
                parameter.setName(key);
                parameter.setValue(parameters.get(key));
                collectedMetric.getParameters().add(parameter);
            }
        }

        if (aggregationFunction != null & aggregationLevel != null) {
            metricAggregation.setAggregateFunction(aggregationFunction);
            metricAggregation.setGroupingClass(aggregationLevel);
            rule.setMetricAggregation(metricAggregation);
        } else if (aggregationFunction == null & aggregationLevel != null) {
            logger.warn("Aggregation level specified but no aggregation function found; any aggregation will be applied for rule"
                    + ruleId);
        } else if (aggregationFunction != null & aggregationLevel == null) {
            logger.warn("Aggregation fucntion specified but no aggregation level found; any aggregation will be applied for rule"
                    + ruleId);
        }
        
        if(condition!= null){
            cond.setValue(condition);
            rule.setCondition(cond);
        }

        action.setName("OutputMetric");
        metric.setName("metric");

        if (outputMetric != null) {
            metric.setValue(outputMetric);
        } else {
            metric.setValue(collectedMetric.getMetricName() + "_Output");
        }
        value.setName("value");
        value.setValue("METRIC");
        resourceId.setName("resourceId");
        resourceId.setValue("ID");
        action.getParameters().add(metric);
        action.getParameters().add(value);
        action.getParameters().add(resourceId);
        actions.getActions().add(action);

        rule.setMonitoredTargets(monitoredTargets);
        rule.setCollectedMetric(collectedMetric);
        rule.setActions(actions);
        toReturn.getMonitoringRules().add(rule);

        return toReturn;
    }
}
