/**
 * Copyright 2014 SeaClouds
 * Contact: Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
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

package core.modaCloudsMonitoring.modaCloudsMetrics;

import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatum;
import it.polimi.modaclouds.monitoring.metrics_observer.MonitoringDatumHandler;

import java.util.Date;
import java.util.List;

public class CVSResultHandler extends MonitoringDatumHandler {

	@Override
	public void getData(List<MonitoringDatum> monitoringData) {

		if (monitoringData == null
				|| (monitoringData != null && monitoringData.isEmpty()))
			System.out
					.println("ObserverTimestamp,ResourceId,Metric,Value,Timestamp");

		else {

			System.out
					.println("ObserverTimestamp, ResourceId, Metric, Value, Timestamp");

			String observerTimestamp = Long.toString(new Date().getTime());

			for (MonitoringDatum monitoringDatum : monitoringData) {

				System.out.println(observerTimestamp + ","
						+ monitoringDatum.getResourceId() + ","
						+ monitoringDatum.getMetric() + ","
						+ monitoringDatum.getValue() + ","
						+ monitoringDatum.getTimestamp());
			}
		}
	}

}
