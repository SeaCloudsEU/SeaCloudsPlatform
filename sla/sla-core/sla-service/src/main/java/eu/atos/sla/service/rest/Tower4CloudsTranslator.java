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
package eu.atos.sla.service.rest;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.monitoring.IMetricTranslator;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * Translates a serialized json of metrics (as sent by Tower4Clouds) into 
 * a set of metrics suitable for sla enforcement.
 * 
 */
public class Tower4CloudsTranslator implements IMetricTranslator<String>{
    private static final Logger logger = LoggerFactory.getLogger(Tower4CloudsTranslator.class);
    
    /**
     * Needed to get the guarantee term that evaluates a particular metric key.
     */
    private final IConstraintEvaluator constraintEvaluator;
    
    public Tower4CloudsTranslator(IConstraintEvaluator constraintEvaluator) {
        this.constraintEvaluator = constraintEvaluator;
    }

    @Override
    /**
     * Performs the translation from the data coming from the Monitor and SLA's MonitoringMetric.
     * 
     * The key of each monitor data is the name of the violation (contained in the slo), so each
     * slo containing the key must enforce that metric.
     */
    public Map<IGuaranteeTerm, List<IMonitoringMetric>> translate(IAgreement agreement, String data) {
        MultivaluedMapWrapper<IGuaranteeTerm, IMonitoringMetric> resultWrapper = 
                new MultivaluedMapWrapper<IGuaranteeTerm, IMonitoringMetric>();
        
        Gson gson = new Gson();
        JsonReader reader = null;
        Type type = new TypeToken<Map<String,Map<String,List<Map<String,String>>>>>(){}.getType();
        reader = new JsonReader(new StringReader(data));
        final Map<String,Map<String,List<Map<String,String>>>> json = gson.fromJson(reader, type);
        
        /*
         * key: name of violation
         */
        final MultivaluedMapWrapper<String, IGuaranteeTerm> metric2terms = initTermsMap(agreement);
        
        for (Map<String,List<Map<String,String>>> item : json.values()) {
            BindingHelper var = new BindingHelper(item);
            /*
             * get guarantee terms that evaluate that violation
             */
            List<IGuaranteeTerm> terms = metric2terms.get(var.key);

            if (terms == null) {
                logger.warn("List of terms handling " + var.key + " is empty");
                continue;
            }
            
            resultWrapper.addToKeys(terms, new MonitoringMetric(var));
        }
        logger.debug("Output metricsmap = " + resultWrapper.getMap());
        return resultWrapper.getMap();
    }

    /**
     * Inits a MultivaluedMap with metrickey as key and the terms that evaluate the metrickey as values.
     * @param agreement
     * @return
     */
    private MultivaluedMapWrapper<String, IGuaranteeTerm> initTermsMap(IAgreement agreement) {
        MultivaluedMapWrapper<String, IGuaranteeTerm> result = new MultivaluedMapWrapper<String, IGuaranteeTerm>();
        
        for (IGuaranteeTerm term : agreement.getGuaranteeTerms()) {
            String variable = constraintEvaluator.getConstraintVariable(term.getServiceLevel());
            result.add(variable, term);
        }
        return result;
    }    
    
    private static final class MonitoringMetric implements IMonitoringMetric {
        private final BindingHelper binding;

        private MonitoringMetric(BindingHelper binding) {
            this.binding = binding;
        }

        @Override
        public String getMetricValue() {
            return binding.value;
        }

        @Override
        public String getMetricKey() {
            return binding.key;
        }

        @Override
        public Date getDate() {
            return binding.date;
        }

        @Override
        public String toString() {
            return String.format("%s[key=%s, value=%s, date=%s]",
                    this.getClass().getName(),
                    this.getMetricKey(),
                    this.getMetricValue(),
                    this.getDate().toString());
        }
    }

    /**
     * Wrapper over a Map that implements a MultivaluedMap, i.e, a single key may have more than one value.
     * 
     * Internally, it is a Map<K, List<V>>. This wrapper simply facilitates the adding operations.
     * 
     * After adding all values, you can work with the result using {@link #getMap()}. Remember that 
     * any key that has no value may return <code>null</code> instead of an empty list.
     *  
     * @param <K> type of key
     * @param <V> type value
     * 
     */
    public static class MultivaluedMapWrapper<K, V> {

        private final Map<K, List<V>> map;

        public MultivaluedMapWrapper() {
            this.map = new HashMap<K, List<V>>();
        }
        
        public MultivaluedMapWrapper(Map<K, List<V>> map) {
            if (map == null) {
                throw new IllegalArgumentException("map cannot be null");
            }
            this.map = map;
        }
        
        /**
         * Return the list of values associated to a key, or an empty list. 
         */
        public List<V> get(K key) {
            List<V> list = map.get(key);
            
            return list == null? Collections.<V>emptyList() : list;
        }

        /**
         * Add a value to a key
         */
        public void add(K key, V value) {
        
            List<V> list;
            if (map.containsKey(key)) {
                list = map.get(key);
            }
            else {
                list = new ArrayList<V>();
                map.put(key, list);
            }
            list.add(value);
        }
    
        /**
         * Add a value to the given keys
         */
        public void addToKeys(List<K> keys, V value) {
            
            for (K key : keys) {
                this.add(key, value);
            }
        }
        
        /**
         * Returns an unmodifiable Map over the internal map.
         */
        public Map<K, List<V>> getMap() {
            return Collections.unmodifiableMap(map);
        }
    }
    
    private static class BindingHelper {
        private final String key;
        private final String value;
        private final Date date;
        @SuppressWarnings("unused")
        private final String resourceId;
        
        public BindingHelper(Map<String,List<Map<String,String>>> binding) {
            key = getItem(binding, "http://www.modaclouds.eu/model#metric");
            value = getItem(binding, "http://www.modaclouds.eu/model#value");
            String dateStr = getItem(binding, "http://www.modaclouds.eu/model#timestamp");
            date = new Date(Long.parseLong(dateStr));
            resourceId = getItem(binding, "http://www.modaclouds.eu/model#resourceId");
        }

        private String getItem(Map<String,List<Map<String,String>>> binding, String ns) {
            String item= nullable(binding.get(ns)).get(0).get("value");
            
            return item;
        }
        private List<Map<String, String>> nullable(List<Map<String, String>> list) {
            if (list != null)
                return list;
            else {
                List<Map<String, String>> emptyValueList = new ArrayList<Map<String, String>>();
                Map<String, String> emptyValueMap = new HashMap<String, String>();
                emptyValueMap.put("value", "");
                emptyValueList.add(emptyValueMap);
                return emptyValueList;
            }
        }
        
    }    
}