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
package eu.atos.sla.evaluation.guarantee;

import java.util.Date;
import java.util.List;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Interface for the evaluator of a Service Level Objective.
 * @see IGuaranteeTerm#getServiceLevel() 
 * @author rsosa
 *
 */
public interface IServiceLevelEvaluator {
	
	/**
	 * Evaluate if a list of metrics fulfill the service level of a guarantee term.
	 * 
	 * @param agreement Agreement that contains the guarantee term to evaluate
	 * @param term Guarantee term to evaluate
	 * @param metrics List of metrics to evaluate.
	 * @param now The metrics have been retrieved until now.
	 * @return Detected violations.
	 */
	List<IViolation> evaluate(IAgreement agreement, IGuaranteeTerm term, List<IMonitoringMetric> metrics, Date now);

}
