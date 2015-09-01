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
package eu.seaclouds.sla.retriever;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.UnmodifiableMultivaluedMap;

import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.monitoring.IMetricsRetrieverV2;
import eu.atos.sla.monitoring.IMonitoringMetric;

public class BrooklynMetricsRetrieverV1 implements IMetricsRetrieverV2 {
    
    private final SensorClient client;

    public BrooklynMetricsRetrieverV1(SensorClient client) {
        super();
        this.client = client;
    }

    @Override
    public List<IMonitoringMetric> getMetrics(String agreementId,
            String serviceScope, String variable, Date begin, Date end,
            int maxResults) {
        
        String[] parsedScope = parseScope(serviceScope);
        String applicationId = parsedScope[0];
        String entityId = parsedScope[1];

        String metricValue = client.getSensorValue(applicationId, entityId, variable);
        
        IMonitoringMetric metric = new MonitoringMetric(variable, metricValue, new Date());
        
        List<IMonitoringMetric> result = Collections.singletonList(metric);
        return result;
    }

    @Override
    public Map<IGuaranteeTerm, List<IMonitoringMetric>> getMetrics(
            String agreementId, List<RetrievalItem> retrievalItems,
            int maxResults) {
        
        Map<IGuaranteeTerm, List<IMonitoringMetric>> result = new HashMap<IGuaranteeTerm, List<IMonitoringMetric>>();
        for (RetrievalItem item : retrievalItems) {
            
            IGuaranteeTerm term = item.getGuaranteeTerm();
            List<IMonitoringMetric> metrics = getMetrics(
                    agreementId, 
                    term.getServiceScope(), 
                    item.getVariable(), 
                    item.getBegin(), 
                    item.getEnd(), 
                    maxResults);
            
            result.put(term, metrics);
        }
        return result;
    }
    
    private String[] parseScope(String serviceScope) {

        String[] result = serviceScope.split("/", 2);
        
        if (result.length != 2) {
            result = new String[] { "", "" };
        }
        return result;
    }

    public static class SensorClient {
        public static final MultivaluedMap<String, String> EMPTY_MAP =
                new UnmodifiableMultivaluedMap<String, String>(new MultivaluedMapImpl());
        
        private final Logger logger = LoggerFactory.getLogger(SensorClient.class);
        
        private final String brooklynUrl;

        private final WebResource rootResource;
        
        /*
         * Just an utility constructor
         */
        public SensorClient(String brooklynUrl) {
            this(buildJerseyClient(), brooklynUrl);
        }

        public SensorClient(Client client, String brooklynUrl) {
            this.brooklynUrl = brooklynUrl;
            
            this.rootResource = client.resource(brooklynUrl);
        }
        
        public ClientResponse method(
                String method,
                String relativeUrl,
                Object data,
                MultivaluedMap<String, String> queryParams,
                MultivaluedMap<String, String> headers) {
            
            WebResource resource = rootResource.path(relativeUrl).queryParams(queryParams);
            
            WebResource.Builder builder = resource.getRequestBuilder();
            
            builder = applyHeaders(builder, headers);
            
            ClientResponse response = builder.method(method, ClientResponse.class, data);
            
            logger.debug(String.format("%s %s %s %s", 
                    response.getStatus(),
                    method,
                    resource.getURI(),
                    "")
            );
            
            return response;
        }
        
        public String getSensorValue(String applicationId, String entityId, String sensorId) {
            
            String relativeUrl = buildUrl(applicationId, entityId, sensorId);
            
            ClientResponse response = this.method(HttpMethod.GET, relativeUrl, null, EMPTY_MAP, EMPTY_MAP);
            if (response.getStatus() != 200) {
                return "";
            }
            String result = response.getEntity(String.class);
            return result;
        }

        private static Client buildJerseyClient() {
            ClientConfig config = new DefaultClientConfig();
            Client jerseyClient = Client.create(config);
            return jerseyClient;
        }
        
        private WebResource.Builder applyHeaders(WebResource.Builder builder,
                MultivaluedMap<String, String> headers) {

            for (String key : headers.keySet()) {
                for (String value : headers.get(key)) {
                    builder = builder.header(key, value);
                }
            }
            return builder;
        }        

        private String buildUrl(String applicationId, String entityId, String metricKey) {
            
            String path = "v1/applications/{application}/entities/{entity}/sensors/{sensor}";
            String result = UriBuilder.fromPath(path)
                    .build(applicationId, entityId, metricKey)
                    .toString();
            return result;
        }
        
        public String getBrooklynUrl() {
            return brooklynUrl;
        }
    }
    
    private static class MonitoringMetric implements IMonitoringMetric {

        private final String metricKey;
        private final String metricValue;
        private final Date date;
        
        public MonitoringMetric(String metricKey, String metricValue, Date date) {
            super();
            this.metricKey = metricKey;
            this.metricValue = metricValue;
            this.date = date;
        }

        public String getMetricKey() {
            return metricKey;
        }

        public String getMetricValue() {
            return metricValue;
        }

        public Date getDate() {
            return date;
        }
        
    }
}
