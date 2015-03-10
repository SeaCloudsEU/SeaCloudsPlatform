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
package eu.atos.sla.monitoring;

import java.util.Date;
import java.util.List;

/**
 * Extracts metrics from a metric repository.
 * 
 * Every service provider that wants to use the SLA module has to provide a MetricsRetriever, 
 * in order to use the enforcement feature. 
 * 
 * The implementer needs to translate the terms from the SLA domain (agreementId, serviceScope) to
 * the Provider domain, connect to the repository (e.g. REST, db), and return the proper metrics.
 *  
 * @see eu.atos.sla.datamodel.IGuaranteeTerm
 * 
 * @author rsosa
 *
 */
public interface IMetricsRetriever {

	/**
	 * Get variables (metrics) from a metric repository between a time interval.
	 * 
	 * @param agreementId Id of the agreement
	 * @param serviceScope Service scope of the guarantee term being checked. The parameter is 
	 *   defined as wsag:ServiceScope[@serviceName] + "/" + wsag:ServiceScope/text(). 
	 * @param variable What to retrieve from the metric repository. In the reference implementation, 
	 *   the name of the variable in ServiceProperties section. The provider may prefer
	 *   to override this and return the location of the variable (wsag:Variable/wsag:Location)
	 *   @see IMetricsValidator#getConstraintVariable(String)
	 * @param begin Variables are retrieved in the time interval [begin, end]
	 * @param end Variables are retrieved in the time interval [begin, end]
	 * @param maxResults are returned as much. If there are more than maxResults samples
	 *   in the interval, the results must be sorted using as distance function f(t) = end - t
	 *   and returning the first maxResults samples. 
	 * @return List of metrics.
	 */
	public List<IMonitoringMetric> 
			getMetrics(String agreementId, String serviceScope, String variable, Date begin, Date end, int maxResults);
}
