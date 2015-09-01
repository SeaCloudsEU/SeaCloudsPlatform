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
package eu.atos.sla.evaluation.guarantee;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.atos.sla.evaluation.guarantee.PoliciedServiceLevelEvaluatorTest.MonitoringMetric;
import eu.atos.sla.monitoring.IMonitoringMetric;

public class TestMetricsGenerator {
    Date now;
    Date[] dates;
    String[] values;
    
    public TestMetricsGenerator(Date now, String[] values) {
        this.now = now;
        this.values = values;
        this.dates = getDates(values);
    }
    
    /**
     * One sample per second
     */
    private Date[] getDates(String[] values) {
        
        Date[] result = new Date[values.length];
        
        for (int i = 0; i < values.length; i++) {
            long ms = now.getTime() - (values.length - i) * 1000;
            result[i] = new Date(ms);
        }
        return result;
    }
    
    public List<IMonitoringMetric> getMetrics(String metricKey) {
        ArrayList<IMonitoringMetric> result = new ArrayList<IMonitoringMetric>();
        
        for (int i = 0; i < values.length; i++) {
            result.add(new MonitoringMetric(metricKey, values[i], dates[i]));
        }
        return result;
    }

}