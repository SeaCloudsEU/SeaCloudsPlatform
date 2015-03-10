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
package eu.atos.sla.monitoring.simple;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import eu.atos.sla.monitoring.IMetricsRetriever;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Dummy retriver that returns metric values in [0,1)
 * 
 * @author rsosa
 *
 */
public class DummyMetricsRetriever implements IMetricsRetriever {

	private final Random rnd = new Random();
	
	public DummyMetricsRetriever() {
	}
	
	@Override
	public List<IMonitoringMetric> getMetrics(String serviceId, String serviceScope,
			final String variable, Date begin, final Date end, int maxResults) {

		if (begin == null) {
			
			begin = new Date(end.getTime() - 1000);
		}
		List<IMonitoringMetric> result = new ArrayList<IMonitoringMetric>();
		for (int i = 0; i < 2; i++) {
			result.add(getRandomMetric(variable, i == 0? begin : end));
		}
		return result;
	}
	
	public IMonitoringMetric getRandomMetric(String metricName, Date date) {
		
		return new MonitoringMetric(metricName, rnd.nextDouble(), date);
	}

}
