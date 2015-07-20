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
package eu.atos.sla.monitoring.simple;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.monitoring.IMetricTranslator;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Translator for SimpleMetricsReceiverData type.
 */
public class SimpleMetricsTranslator implements IMetricTranslator<SimpleMetricsTranslator.SimpleMetricsReceiverData> {
    private final static Logger logger = LoggerFactory.getLogger(SimpleMetricsTranslator.class);
    private final IConstraintEvaluator constraintEvaluator;
    
    public SimpleMetricsTranslator(IConstraintEvaluator constraintEvaluator) {
        this.constraintEvaluator = constraintEvaluator;
        logger.debug("Created SimpleMetricsTranslator");
    }
    
    @Override
    public Map<IGuaranteeTerm, List<IMonitoringMetric>> translate(
            final IAgreement agreement, final SimpleMetricsReceiverData data) {
        
        Map<IGuaranteeTerm, List<IMonitoringMetric>> result = 
                new HashMap<IGuaranteeTerm, List<IMonitoringMetric>>();

        List<IMonitoringMetric> list = new ArrayList<IMonitoringMetric>();
        for (IGuaranteeTerm term : agreement.getGuaranteeTerms()) {
            String termVariable = constraintEvaluator.getConstraintVariable(term.getServiceLevel());
            if (termVariable != null && termVariable.equals(data.metricKey)) {
            
                logger.debug("GuaranteeTerm[name=" + term.getName() + "] guarantees " + termVariable + " metric");
                result.put(term, list);
            }
        }
        for (final SimpleMetricsReceiverData.SimpleMetricValue value : data.metricValues) {
            
            IMonitoringMetric m = new IMonitoringMetric() {
                
                @Override
                public String getMetricValue() {
                    return value.value;
                }
                
                @Override
                public String getMetricKey() {
                    return data.metricKey;
                }
                
                @Override
                public Date getDate() {
                    return value.date;
                }
                
                @Override
                public String toString() {
                    return String.format("%s[key='%s',value='%s',date='%s'", 
                            this.getClass().getName(),
                            getMetricKey(),
                            getMetricValue(),
                            getDate());
                }
            };
            list.add(m);
        }
        logger.debug("output = " + result.toString());
        return result;
    }
    
    /**
     * Example of data to receive by the metrics receiver.
     */
    public static class SimpleMetricsReceiverData {
        public static class SimpleMetricValue {
            
            private final String value;
            private final Date date;

            public SimpleMetricValue(String value, Date date) {
                this.value = value;
                this.date = date;
            }

            public String getValue() {
                return value;
            }

            public Date getDate() {
                return date;
            }
        }
        final String metricKey;
        final SimpleMetricValue[] metricValues;
        
        public SimpleMetricsReceiverData(String metricKey,
                SimpleMetricValue[] metricValues) {
            this.metricKey = metricKey;
            this.metricValues = metricValues;
        }
        
        public SimpleMetricsReceiverData(String metricKey, SimpleMetricValue metricValue) {
            this(metricKey, new SimpleMetricValue[] { metricValue});
        }
        
        public String getMetricKey() {
            return metricKey;
        }

        public SimpleMetricValue[] getMetricValues() {
            return metricValues;
        }
    }
    
    
}