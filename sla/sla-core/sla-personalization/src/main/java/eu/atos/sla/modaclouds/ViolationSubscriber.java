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
package eu.atos.sla.modaclouds;

import it.polimi.tower4clouds.rules.Action;
import it.polimi.tower4clouds.rules.Actions;
import it.polimi.tower4clouds.rules.MonitoringRule;
import it.polimi.tower4clouds.rules.Parameter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;

public class ViolationSubscriber {
    
    public static class Factory {
        
        private String metricsBaseUrl;
        private String callbackBaseUrl;

        /**
         * Factory for ViolationSubscriber's.
         * 
         * @param metricsBaseUrl url where to POST a new metric observer. It should be something like 
         * http://MONITORING_MANAGER_ADDRESS:MONITORING_MANAGER_LISTENING_PORT/v1/metrics. The final url 
         * is $baseurl/$action
         * @param callbackBaseUrl is the url that the Monitoring Platform will use to communicate the 
         * metric violations. The final url $baseurl/$agreement_id/$term_name
         */
        public Factory(String metricsBaseUrl, String callbackBaseUrl) {

            this.metricsBaseUrl = metricsBaseUrl;
            this.callbackBaseUrl = callbackBaseUrl;
        }
        
        public ViolationSubscriber getSubscriber(IAgreement agreement) {
            return new ViolationSubscriber(metricsBaseUrl, callbackBaseUrl);
        }
    }
    
    public class ViolationSubscriberException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ViolationSubscriberException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private Logger logger = LoggerFactory.getLogger(ViolationSubscriber.class);

    private static MonitoringRule NOT_FOUND_RULE = new MonitoringRule();

    private final String metricsBaseUrl;
    private final String callbackBaseUrl;

    private Client client;
    
    public ViolationSubscriber(
            String metricsBaseUrl, String callbackBaseUrl) {
        this.metricsBaseUrl = metricsBaseUrl;
        this.callbackBaseUrl = callbackBaseUrl;
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
    }
    
    public void subscribeObserver(IAgreement agreement) {
        logger.debug("Subscribing {}", agreement.getAgreementId());
        
        for (IGuaranteeTerm gt : agreement.getGuaranteeTerms()) {
            MonitoringRule rule = buildRelatedRule(gt);
            
            if (rule == NOT_FOUND_RULE) {
                
                logger.warn("Rule not found for {}.GuaranteeTerm[name={}] ",
                        agreement.getAgreementId(), gt.getName());
                continue;
            }
            String outputMetric = QosModels.getOutputMetric(rule);
            process(agreement, gt, rule, outputMetric);
        }
        
    }
    
    /**
     * Build a MonitoringRule given a guarantee term
     */
    private MonitoringRule buildRelatedRule(IGuaranteeTerm guarantee) {
        
        MonitoringRule result = new MonitoringRule();
        result.setId(guarantee.getName());

        String violation = extractOutputMetric(guarantee);
        Parameter param = new Parameter();
        param.setName(QosModels.METRIC_PARAM_NAME);
        param.setValue(violation);
        
        Action action = new Action();
        action.setName(QosModels.OUTPUT_METRIC_ACTION);
        action.getParameters().add(param);
        
        Actions actions = new Actions();
        actions.getActions().add(action);
        
        result.setActions(actions);
        
        return result;
    }
    
    private String extractOutputMetric(IGuaranteeTerm guarantee) {
        
        String constraint = guarantee.getServiceLevel();
        int pos = constraint.indexOf(' ');
        String violation = constraint.substring(0, pos == -1? constraint.length() : pos);
        return violation;
    }
    
    private void process(IAgreement agreement, IGuaranteeTerm term, MonitoringRule rule, String outputMetric) {
        
        logger.debug("Subscribing to rule[id='{}', outputmetric='{}']", rule.getId(), outputMetric);

        String url = getPostUrl(outputMetric);
        logger.debug("POST {}", url);
        
        WebResource service = client.resource(UriBuilder.fromUri(url).build());
            
        String callbackUrl = getCallbackUrl(this.callbackBaseUrl, agreement, term);
        
        String body = String.format("{ \"callbackUrl\" : \"%s\", \"format\" : \"RDF/JSON\" }", callbackUrl);
        ClientResponse response = 
                service.type(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON).
                post(ClientResponse.class, body);
        
        logger.debug("{} POST {}\n{}", response.getStatus(), url, response.getEntity(String.class));
        if (!isOk(response)) {
            logger.warn("Could not attach observer to {}", outputMetric);
        }
        else {
            logger.info("Successful attached callback={} to {}", callbackUrl, outputMetric);
        }
    }
        
    private static String getCallbackUrl(String callbackBaseUrl, IAgreement agreement, IGuaranteeTerm term) {

        String path = "{base}/{agreement}";
        String result = UriBuilder.fromPath(path).build(
                callbackBaseUrl, agreement.getAgreementId()
        ).toString();
        
        return result;
    }
    
    private boolean isOk(ClientResponse response) {
        
        return response.getStatus() == Response.Status.CREATED.getStatusCode() ||
                response.getStatus() == Response.Status.OK.getStatusCode();
    }

    private String getPostUrl(String outputMetric) {
        return String.format("%s/%s/observers", metricsBaseUrl, outputMetric);
    }

    public String getMetricsBaseUrl() {
        return metricsBaseUrl;
    }

    public String getCallbackUrl() {
        return callbackBaseUrl;
    }
}
