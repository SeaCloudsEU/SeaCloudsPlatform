/**
 * Copyright 2015 SeaCloudsEU
 * Contact: Michele Guerriero <michele.guerriero@mail.polimi.it>
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
package eu.seaclouds.monitor.reconfigurationDataCollector.dataCollector;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import brooklyn.rest.domain.ApplicationSummary;
import eu.seaclouds.monitor.reconfigurationDataCollector.dataCollector.Metrics;
import eu.seaclouds.monitor.reconfigurationDataCollector.exception.MetricNotAvailableException;
import eu.seaclouds.monitor.reconfigurationDataCollector.dataCollector.MetricManager;

public class MetricManager {
    private Logger logger = LoggerFactory.getLogger(MetricManager.class);

    public static Set<String> getApplicationMetrics() {

        Set<String> metrics = new HashSet<String>();

        metrics.add(Metrics.IS_APP_ON_FIRE);

        return metrics;
    }

    public Object getMetricValue(String metric, ApplicationSummary application)
            throws MetricNotAvailableException {

        logger.info("Collecting current value for metric {}", metric);

        switch (metric) {
        case Metrics.IS_APP_ON_FIRE:
            return (application.getStatus().equals("ON_FIRE")) ? 0 : 1;
        default:
            throw new MetricNotAvailableException(
                    "The specified metric is not available from NURO sensors");

        }

    }
}
