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
package eu.atos.sla.evaluation.constraint.simple;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import eu.atos.sla.evaluation.constraint.simple.Operator;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintEvaluator.SimpleAverageValidatorIter;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintEvaluator.SimpleValidatorIter;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.atos.sla.monitoring.simple.MonitoringMetric;

public class SimpleValidatorsIterTest {

    private Collection<IMonitoringMetric> generateMetricsCollection(double[] array) {

        ArrayList<IMonitoringMetric> result = new ArrayList<IMonitoringMetric>();
        
        int t = 0;
        for (double value : array) {
            
            IMonitoringMetric m = new MonitoringMetric("metric", value, new Date(t));
            result.add(m);
            
            t++;
        }
        return result;
    }
    
    
    public void checkMetric(IMonitoringMetric metric, String expectedKey, double expectedValue,
            long expectedTime) {
        
        assertEquals(expectedKey, metric.getMetricKey());
        assertEquals(expectedValue, Double.parseDouble(metric.getMetricValue()), 0);
        assertEquals(new Date(expectedTime), metric.getDate());
        
    }
    
    public void checkMetrics(Iterable<IMonitoringMetric> iterable, String expectedKey,
            double[] expectedValues, long[] expectedTimes) {
        
        int i = 0;
        for (IMonitoringMetric m : iterable) {
            
            if (m == null) {
                continue;
            }
            assertEquals("metric", m.getMetricKey());
            assertEquals(expectedValues[i], Double.parseDouble(m.getMetricValue()), 0);
            assertEquals(new Date(expectedTimes[i]), m.getDate());
            
            i++;
        }
        
    }
    
    @Test
    public void testSimpleValidatorIter() {

        Collection<IMonitoringMetric> metrics = generateMetricsCollection(new double[] {
                1, 0, -1, 0, 2, 0, -2, 0, 3, 0, -3, 0
        });
        
        SimpleValidatorIter it = new SimpleValidatorIter(metrics, Operator.GE, new double[] {0});
        
        Iterable<IMonitoringMetric> iterable = it.iterable();
        
        /*
         * These are the times and values that breaches should have
         */
        long[] times = new long[] { 2, 6, 10 };
        double[] values = new double[] { -1, -2, -3 };
        
        checkMetrics(iterable, "metric", values, times);
    }

    @Test
    public void testSimpleValidatorAverageIter() {
        
        Collection<IMonitoringMetric> metrics = generateMetricsCollection(new double[] {
                1, 0, -1, 0, 2, 0, -2, 0, 3, 0, -3, 0
        });
        
        SimpleAverageValidatorIter it = new SimpleAverageValidatorIter(metrics, Operator.GT, new double[] {0}, 10);
        
        Iterable<IMonitoringMetric> iterable = it.iterable();
        
        /*
         * These are the times and values that breaches should have
         */
        long[] times = new long[] { 11 };
        double[] values = new double[] { 0.0 };
        
        checkMetrics(iterable, "metric", values, times);
    }


}
