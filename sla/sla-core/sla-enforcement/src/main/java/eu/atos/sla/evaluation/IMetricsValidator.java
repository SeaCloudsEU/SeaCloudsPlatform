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
package eu.atos.sla.evaluation;

import java.util.Collection;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.monitoring.IMonitoringMetric;


/**
 * Interface that any sla provider has to implement in order to evaluate if any given metrics
 * are breaches or not.
 *  
 * @deprecated
 */
public interface IMetricsValidator {

    /**
     * Given a constraint, returns the variable (service property) that has to be retrieved from the monitoring
     * module.
     * 
     * It is a restriction of the core that only one variable can be retrieved from monitoring per constraint.
     * 
     * Example:
     * getConstraintVariable("uptime GT 99") -> "uptime"
     */
    String getConstraintVariable(String constraint);
    
    /**
     * Check that a constraint is being fulfilled.
     * @param agrement: Agreement being enforced. The value of a variable may be in the ServiceDescriptionTerms.
     * @param kpiName: Name of the KPI.
     * @param metrics: List of metrics retrieved from the monitoring module.
     * @param constraint: constraint to be enforced.
     * @return Metrics that do not fulfill the constraint.
     * 
     * @see eu.atos.sla.datamodel.IGuaranteeTerm
     */
    Iterable<IMonitoringMetric> getBreaches(
            IAgreement agrement, String kpiName, Collection<IMonitoringMetric> metrics, String constraint);
}
