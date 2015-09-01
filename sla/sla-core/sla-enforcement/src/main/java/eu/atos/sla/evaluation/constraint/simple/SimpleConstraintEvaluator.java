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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.evaluation.IMetricsValidator;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.monitoring.IMonitoringMetric;

@SuppressWarnings("deprecation")
public class SimpleConstraintEvaluator implements IMetricsValidator, IConstraintEvaluator {
    private static Logger logger = LoggerFactory.getLogger(SimpleConstraintEvaluator.class);
    
    private static final int MIN_SAMPLES = 100;
    private SimpleConstraintParser parser = new SimpleConstraintParser();
    
    @Override
    public String getConstraintVariable(String constraint) {
        SimpleConstraintParser.SimpleConstraintElements elems = parse(constraint);
        return elems.getLeft();
    }

    @Override
    public List<IMonitoringMetric> evaluate(String kpiName, String constraint,
            List<IMonitoringMetric> metrics) {
        logger.debug("evaluate(kpi={}, constraint={}", kpiName, constraint);
        List<IMonitoringMetric> result = new ArrayList<IMonitoringMetric>();
        for (IMonitoringMetric metric : getBreaches(null /*don't care */, kpiName, metrics, constraint)) {
            
            if (metric == null) {
                continue;
            }
            
            result.add(metric);
        }
        return result;
    }
        
    @Override
    public Iterable<IMonitoringMetric> getBreaches(IAgreement agreement, String kpiName,
            Collection<IMonitoringMetric> metrics, String constraint) {

        if (kpiName == null) {
            throw new NullPointerException("kpiName is null");
        }
        /*
         * TODO rsosa: This could be cached in some way.
         */
        SimpleConstraintParser.SimpleConstraintElements elems = parse(constraint);
        Operator operator = elems.getOperator();
        double[] threshold = elems.getRightArray();
        
        if (kpiName.startsWith("avg")) {
            return new SimpleAverageValidatorIter(
                    metrics, operator, threshold, MIN_SAMPLES).iterable();
        }
        else {
            return new SimpleValidatorIter(metrics, operator, threshold).iterable();
        }
    }

    private SimpleConstraintParser.SimpleConstraintElements parse(
            String constraint) {
        SimpleConstraintParser.SimpleConstraintElements elems = parser.parse(constraint);
        return elems;
    }
    
    static class SimpleValidator implements Iterable<IMonitoringMetric> {

        SimpleValidatorIter iter;
        
        public SimpleValidator(Collection<IMonitoringMetric> metrics, Operator operator, double[] operand) {

            iter = new SimpleValidatorIter(metrics, operator, operand);
        }
        
        @Override
        public Iterator<IMonitoringMetric> iterator() {
            
            return iter;
        }
        
    }
    
    /**
     * Iterator over breaches in a list of metrics.
     * 
     * A metric is a breach if does not fullfill <code>metric OPERATOR operand</code>
     * 
     * To ease implementation, this iterator return <code>null</code> on any non-breach metric,
     * so nulls have to be skipped in client call.
     */
    static class SimpleValidatorIter implements
            Iterator<IMonitoringMetric> {
        private static Logger logger = LoggerFactory.getLogger(SimpleValidatorIter.class);
        
//        private Collection<IMonitoringMetric> metrics;
        private Iterator<IMonitoringMetric> it;
        private Operator operator;
        private double[] operand;

        public SimpleValidatorIter(Collection<IMonitoringMetric> metrics,
                Operator operator, double[] operand) {
//            this.metrics = metrics;
            this.it = metrics.iterator();
            this.operator = operator;
            this.operand = operand;
        }
        
        public Iterable<IMonitoringMetric> iterable() {
            
            return new Iterable<IMonitoringMetric>() {
                
                @Override
                public Iterator<IMonitoringMetric> iterator() {
                    
                    return SimpleValidatorIter.this;
                }
            };
        }

        @Override
        public boolean hasNext() {

            return it.hasNext();
        }

        @Override
        public IMonitoringMetric next() {

            IMonitoringMetric metric = it.next();

            double metricValue = Double.parseDouble(metric.getMetricValue());
            
            boolean eval = operator.eval(metricValue, operand);
            logger.debug("eval metric(value = " + metricValue + ") = " + eval);
            return eval? null : metric;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    this.getClass().getCanonicalName() + " does not support remove");
        }
    }
    
    /**
     * Iterator over the breaches in a list of metrics.
     * 
     * A list of metrics causes an breach if average of values does not fulfill
     * <code>avg OPERATOR operand</code>. There MUST be more or equal than
     * <code>minSamples</code> samples.
     * 
     * To ease implementation, this iterator can return <code>null</code>, so
     * nulls have to be skipped in client call.
     */
    static class SimpleAverageValidatorIter implements Iterator<IMonitoringMetric> {

//        private Collection<IMonitoringMetric> metrics;
        private Iterator<IMonitoringMetric> it;
        private Operator operator;
        private double operand[];
        private int minSamples;
        /*
         * Stores last metric (in date) of collection
         */
        private IMonitoringMetric lastMetric;

        public SimpleAverageValidatorIter(Collection<IMonitoringMetric> metrics,
                Operator operator, double[] operand, int minSamples) {
//            this.metrics = metrics;
            this.it = metrics.iterator();
            this.operator = operator;
            this.operand = operand;
            this.minSamples = minSamples;
        }

        public Iterable<IMonitoringMetric> iterable() {
            
            return new Iterable<IMonitoringMetric>() {
                
                @Override
                public Iterator<IMonitoringMetric> iterator() {
                    
                    return SimpleAverageValidatorIter.this;
                }
            };
        }

        
        @Override
        public boolean hasNext() {

            return it.hasNext();
        }

        @Override
        public IMonitoringMetric next() {
            int sum = 0;
            int size = 0;

            if (!it.hasNext()) {
                throw new NoSuchElementException();
            }
            IMonitoringMetric metric = null;
            while (it.hasNext()) {
                metric = it.next();
                
                double metricValue = Double.parseDouble(metric.getMetricValue());
                sum += metricValue;
                
                if (lastMetric == null || metric.getDate().after(lastMetric.getDate())) {
                    lastMetric = metric;
                }
                size++;
            }

            final double avg = sum / size;
            boolean satisfied = operator.eval(avg,  operand);
            if (size >= minSamples && !satisfied) {

                /*
                 * Return onthefly IMonitoringMetric.
                 */
                final String metricKey = lastMetric.getMetricKey();
                final Date metricDate = lastMetric.getDate();

                IMonitoringMetric result = new IMonitoringMetric() {

                    @Override
                    public String getMetricKey() {
                        return metricKey;
                    }

                    @Override
                    public String getMetricValue() {
                        return String.valueOf(avg);
                    }

                    @Override
                    public Date getDate() {
                        return metricDate;
                    }
                };
                return result;
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "CheckerIterator does not support remove");
        }
    }
}