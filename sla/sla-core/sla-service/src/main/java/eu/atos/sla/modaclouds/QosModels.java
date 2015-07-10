/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.sla.modaclouds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.polimi.modaclouds.qos_models.schema.Action;
import it.polimi.modaclouds.qos_models.schema.Actions;
import it.polimi.modaclouds.qos_models.schema.Constraint;
import it.polimi.modaclouds.qos_models.schema.MonitoringRule;
import it.polimi.modaclouds.qos_models.schema.MonitoringRules;
import it.polimi.modaclouds.qos_models.schema.Parameter;

/**
 * Functions and vocabulary related to QosConstraints and MonitoringRules
 * (qos-models)
 */
public class QosModels {
    public static final String OUTPUT_METRIC_ACTION = "OutputMetric";
    public static final String METRIC_PARAM_NAME = "metric";

    public static final String BUSINESS_ACTION = "Business";

    public static Logger logger = LoggerFactory.getLogger(QosModels.class);

    public static MonitoringRule NOT_FOUND_RULE = new MonitoringRule();
    public static Parameter NOT_FOUND_PARAMETER = new Parameter();
    static {
        NOT_FOUND_RULE.setActions(new Actions());
        NOT_FOUND_PARAMETER.setName("");
        NOT_FOUND_PARAMETER.setValue("");
    }

    /**
     * Get the first OutputMetric name of a rule (i.e., the name of stream to
     * subscribe to) if defined; empty string otherwise.
     */
    public static String getOutputMetric(MonitoringRule rule) {
        String paramName = METRIC_PARAM_NAME;

        for (Action action : rule.getActions().getActions()) {
            if (OUTPUT_METRIC_ACTION.equalsIgnoreCase(action.getName())) {
                Parameter param = getActionParameter(action, paramName);
                return param.getValue();
            }
        }
        return "";
    }

    /**
     * Returns the first parameter with attribute "name" equals to paramName; if
     * not found, NOT_FOUND_PARAMETER.
     */
    public static Parameter getActionParameter(Action action, String paramName) {
        for (Parameter param : action.getParameters()) {
            if (paramName.equals(param.getName())) {
                return param;
            }
        }
        return NOT_FOUND_PARAMETER;
    }

    /**
     * Get the actions of a rule filtered by its name
     */
    public static List<Action> getActions(MonitoringRule rule, String nameFilter) {
        List<Action> result = new ArrayList<Action>();

        if (nameFilter == null) {
            throw new NullPointerException("nameFilter cannot be null");
        }
        for (Action action : getActions(rule)) {
            if (nameFilter.equalsIgnoreCase(action.getName())) {
                result.add(action);
            }
        }
        return result;
    }

    /**
     * Wrapper to avoid a NPE
     */
    public static List<Action> getActions(MonitoringRule rule) {
        List<Action> result;

        if (rule.getActions() != null && rule.getActions().getActions() != null) {
            result = rule.getActions().getActions();
        } else {
            result = Collections.<Action> emptyList();
        }
        return result;
    }

    /**
     * Returns the rule associated to a constraint; NOT_FOUND_RULE if not found.
     */
    public static MonitoringRule getRelatedRule(Constraint constraint,
            MonitoringRules rules) {
        MonitoringRule result = NOT_FOUND_RULE;

        String constraintId = constraint.getId();
        if (constraintId == null) {
            logger.warn("Not valid constraint: id is null");
        } else {
            for (MonitoringRule rule : rules.getMonitoringRules()) {
                if (constraintId.equals(rule.getRelatedQosConstraintId())) {
                    result = rule;
                    break;
                }
            }
        }
        return result;
    }

}
