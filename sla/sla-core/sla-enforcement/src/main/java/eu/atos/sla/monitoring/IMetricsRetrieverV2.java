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
import java.util.Map;

import eu.atos.sla.datamodel.IGuaranteeTerm;

/**
 * Get variables (metrics) from a metric repository between a time interval.
 * 
 * Every service provider that wants to use the SLA module has to provide a MetricsRetriever, 
 * in order to use the enforcement feature. 
 * 
 * The implementer needs to translate the terms from the SLA domain (agreementId, serviceScope) to
 * the Provider domain, connect to the repository (e.g. REST, db), and return the proper metrics.
 *  
 * Deprecates IMetricsRetriever: this interface is intended to minimize network usage, as queries all the
 * metrics in a single call.
 * 
 */
public interface IMetricsRetrieverV2 extends IMetricsRetriever {
	
	/**
	 * Englobes parameters for a single metrics retrieval.
	 * 
	 * The meaning of each property is the same as the equivalent parameter of 
	 * {@link IMetricsRetriever#getMetrics(String, String, String, Date, Date, int)}
	 *
	 * @see IMetricsRetriever#getMetrics(String, String, String, Date, Date, int)
	 */
	public interface RetrievalItem {
		IGuaranteeTerm getGuaranteeTerm();
		String getVariable();
		Date getBegin();
		Date getEnd();
	}

	/**
	 * Get variables (metrics) from a metric repository between a time interval.
	 * 
	 * @param agreementId
	 * @param retrievalItems list of {@link RetrievalItem} that specify the metrics to retrieve.
	 * @param maxResults are returned as much. If there are more than maxResults samples
	 *   in the interval, the results must be sorted using as distance function f(t) = end - t
	 *   and returning the first maxResults samples. 
	 * @return map of metrics per guarantee term.
	 */
	public Map<IGuaranteeTerm, List<IMonitoringMetric>> 
			getMetrics(String agreementId, List<RetrievalItem> retrievalItems, int maxResults);
}