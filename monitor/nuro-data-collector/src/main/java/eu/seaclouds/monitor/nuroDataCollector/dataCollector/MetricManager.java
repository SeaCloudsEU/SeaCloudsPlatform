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
package eu.seaclouds.monitor.nuroDataCollector.dataCollector;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.monitor.nuroDataCollector.exception.MetricNotAvailableException;

public class MetricManager {

      private Logger logger = LoggerFactory.getLogger(NuroApplicationDC.class);
      private static final int TEN_SECONDS = 10;
      private static final int MINUTE = 60;
      
      public Set<String> getApplicationMetrics() {

            Set<String> metrics = new HashSet<String>();
            
            metrics.add(Metrics.NURO_LAST_TEN_SECONDS_AVERAGE_RESPONSE_TIME);
            metrics.add(Metrics.NURO_LAST_MINUTE_AVERAGE_RESPONSE_TIME);
            metrics.add(Metrics.NURO_LAST_TEN_SECONDS_PLAYER_COUNT);
            metrics.add(Metrics.NURO_LAST_MINUTE_PLAYER_COUNT);
            metrics.add(Metrics.NURO_LAST_TEN_SECONDS_REQUEST_COUNT);
            metrics.add(Metrics.NURO_LAST_MINUTE_REQUEST_COUNT);
            metrics.add(Metrics.NURO_LAST_TEN_SECONDS_THROUGHPUT);
            metrics.add(Metrics.NURO_LAST_MINUTE_THROUGHPUT);

            return metrics;
      }

      public Object getMetricValue(String metric, JsonNode actualObj)
                  throws MetricNotAvailableException {

            String value;
            logger.info("Collecting current value for metric {}",metric);


            switch (metric) {
            case Metrics.NURO_LAST_TEN_SECONDS_AVERAGE_RESPONSE_TIME:
                  value = actualObj.get("request_analytics").get("10seconds")
                              .get("avg_run_time").toString();
                  return Double.parseDouble(value.substring(1, value.length() - 1));
            case Metrics.NURO_LAST_MINUTE_AVERAGE_RESPONSE_TIME:
                  value = actualObj.get("request_analytics").get("minute")
                              .get("avg_run_time").toString();
                  return Double.parseDouble(value.substring(1, value.length() - 1));
            case Metrics.NURO_LAST_TEN_SECONDS_PLAYER_COUNT:
                  value = actualObj.get("request_analytics").get("10seconds")
                              .get("player_count").toString();
                  return Integer.parseInt(value.substring(1, value.length() - 1));
            case Metrics.NURO_LAST_MINUTE_PLAYER_COUNT:
                  value = actualObj.get("request_analytics").get("minute")
                              .get("player_count").toString();
                  return Integer.parseInt(value.substring(1, value.length() - 1));
            case Metrics.NURO_LAST_TEN_SECONDS_REQUEST_COUNT:
                  value = actualObj.get("request_analytics").get("10seconds")
                              .get("request_count").toString();
                  return Integer.parseInt(value.substring(1, value.length() - 1));
            case Metrics.NURO_LAST_MINUTE_REQUEST_COUNT:
                  value = actualObj.get("request_analytics").get("minute")
                              .get("request_count").toString();
                  return Integer.parseInt(value.substring(1, value.length() - 1));
            case Metrics.NURO_LAST_TEN_SECONDS_THROUGHPUT:
                  value = actualObj.get("request_analytics").get("10seconds")
                              .get("request_count").toString();
                  return Double.parseDouble(value.substring(1, value.length() - 1))
                              / TEN_SECONDS;
            case Metrics.NURO_LAST_MINUTE_THROUGHPUT:
                  value = actualObj.get("request_analytics").get("minute")
                              .get("request_count").toString();
                  return Double.parseDouble(value.substring(1, value.length() - 1))
                              / MINUTE;
            default:
                  throw new MetricNotAvailableException(
                              "The specified metric is not available from NURO sensors");

            }

      }
}
