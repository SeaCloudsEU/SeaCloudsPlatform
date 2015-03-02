/**
 * Copyright 2015 SeaClouds
 * Contact: SeaClouds
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
package eu.atos.sla.evaluation.constraint;

import java.util.List;

import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Evaluates if a list of metrics fulfill the constraint of a service level.
 * 
 * The constraint evaluator needs to parse the service level.
 * 
 * @author rsosa
 *
 */
public interface IConstraintEvaluator {
	
	/**
	 * Evaluate what metrics are considered breaches.
	 * @param kpiName KPI of the Service Level.
	 * @param constraint to fulfill.
	 * @param metrics to evaluate
	 * @return input metrics that do not fulfill the constraint.
	 * 
	 * @see eu.atos.sla.datamodel.IGuaranteeTerm
	 */
	List<IMonitoringMetric> evaluate(String kpiName, String constraint, List<IMonitoringMetric> metrics);

	/**
	 * Given a constraint, returns the variable (service property) that has to be retrieved from the monitoring
	 * module.
	 * 
	 * It is a restriction of the core that only one variable can be retrieved from monitoring per constraint
	 * (although some hacks could be performed to overcome this limitation).
	 * 
	 * Example:
	 * getConstraintVariable("uptime GT 99") -> "uptime"
	 */
	String getConstraintVariable(String constraint);

	
}
