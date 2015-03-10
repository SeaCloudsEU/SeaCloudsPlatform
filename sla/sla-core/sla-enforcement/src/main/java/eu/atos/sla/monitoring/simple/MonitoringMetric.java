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

import java.util.Date;

import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Simple implementation of IMonitoringMetric that assumes the value is a double.
 */
public class MonitoringMetric implements IMonitoringMetric {

	private String metricKey;
	private String metricValue;
	private Date date;


	public MonitoringMetric(String metricKey, double metricValue, Date date) {

		this.metricKey = metricKey;
		this.metricValue = String.valueOf(metricValue);
		this.date = date;
	}

	@Override
	public String getMetricKey() {

		return metricKey;
	}

	@Override
	public String getMetricValue() {

		return metricValue;
	}

	@Override
	public Date getDate() {

		return date;
	}

	public void setMetricKey(String metricKey) {
		this.metricKey = metricKey;
	}

	public void setMetricValue(double metricValue) {
		this.metricValue = String.valueOf(metricValue);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	
}
