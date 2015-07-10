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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IBreach;
import eu.atos.sla.datamodel.bean.Breach;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintEvaluator;
import eu.atos.sla.evaluation.guarantee.IBreachRepository;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Breach repository that takes a list of metrics and auto calculates which are breaches.  
 * 
 * Useful for testing
 */
public class DummyBreachRepository implements IBreachRepository {

    private List<IMonitoringMetric> metrics;
    private IConstraintEvaluator constraintEvaluator;
    private String constraint;
    
    public DummyBreachRepository() {
        constraintEvaluator = new SimpleConstraintEvaluator();
    }
    
    public DummyBreachRepository(String constraint) {
        this.metrics = null;
        constraintEvaluator = new SimpleConstraintEvaluator();
        this.constraint = constraint;
    }

    public DummyBreachRepository(String constraint, List<IMonitoringMetric> metrics) {
        this.metrics = metrics;
        constraintEvaluator = new SimpleConstraintEvaluator();
        this.constraint = constraint;
    }
    
    public void init(String constraint, List<IMonitoringMetric> metrics) {
        this.metrics = metrics;
        this.constraint = constraint;
    }
    
    @Override
    public List<IBreach> getBreachesByTimeRange(IAgreement agreement, String kpiName,
            Date begin, Date end) {

        if (metrics == null) {
            return Collections.<IBreach>emptyList();
        }
        
        List<IBreach> result = new ArrayList<IBreach>();
        for (IMonitoringMetric metric : metrics) {
            Date metricDate = metric.getDate();
            String metricValue = metric.getMetricValue();
            if (begin.before(metricDate) && end.after(metricDate) && isBreach(metric)) {
                result.add(newBreach(kpiName, metricValue, metricDate));
            }
        }
        
        return result;
    }

    @Override
    public void saveBreaches(List<IBreach> breaches) {

        System.out.println("Saving list of breaches: " + breaches.size());
    }
    
    private Breach newBreach(String kpiName, String value, Date date) {
        Breach b = new Breach();
        
        b.setKpiName(kpiName);
        b.setValue(value);
        b.setDatetime(date);

        return b;
    }

    /**
     * Fast and dirty function to know if the value is a breach (to don't setup a ConstraintEvaluator).
     */
    private boolean isBreach(IMonitoringMetric metric) {
        List<IMonitoringMetric> breaches = constraintEvaluator.evaluate(
                metric.getMetricKey(), 
                constraint, 
                Collections.singletonList(metric));
        
        return breaches.size() > 0;
    }
    
}