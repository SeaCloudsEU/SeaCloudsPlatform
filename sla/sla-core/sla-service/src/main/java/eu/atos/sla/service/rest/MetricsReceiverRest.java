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

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.atos.sla.monitoring.simple.SimpleMetricsTranslator;
import eu.atos.sla.monitoring.simple.SimpleMetricsTranslator.SimpleMetricsReceiverData;
import eu.atos.sla.parser.data.MonitoringMetric;

@Path("/enforcement-test")
@Component
@Scope("request")
@Transactional
/**
 * Example REST class that receives metrics and enforces them.
 * 
 * This is intended to be used as an example of how to implement actual metrics receivers. 
 * To use this class, the environment variable ENFORCEMENT_TEST must exist. This property
 * is used in the appendix generation.
 * 
 * You can find a usage example in /samples/metricsreceiver.*
 * 
 * To adapt to your implementation, create a new class that define an appropriate MetricsTranslator 
 * in your sla-personalization/applicationContext and rewrite receiveMetrics accordingly.
 * 
 *
 */
public class MetricsReceiverRest extends AbstractSLARest {
    private static final String ENFORCEMENT_TEST = "ENFORCEMENT_TEST";

    private static Logger logger = LoggerFactory.getLogger(MetricsReceiverRest.class);

    @Autowired
    private IEnforcementService enforcementService;
    
    @Autowired
    private IAgreementDAO agreementDao;
    
    @Autowired
    private SimpleMetricsTranslator translator;
    
    @GET
    public Response getRoot() {
    
        return buildResponse(HttpStatus.NOT_FOUND, "Valid method is POST /{agreementId}");
    }
    
    @POST
    @Path("{agreementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response receiveMetrics(
            @PathParam("agreementId") String agreementId, 
            final MonitoringMetric metric) {

        if (System.getenv(ENFORCEMENT_TEST) == null) {
            return buildResponse(HttpStatus.NOT_FOUND, 
                    "Verify server is running with " + ENFORCEMENT_TEST + " env var");
        }
        logger.debug("receiveMetrics(agreementId={} data={}", agreementId, metric.toString());
        IAgreement agreement = agreementDao.getByAgreementId(agreementId);
        
        if (agreement == null) {
            return buildResponse(HttpStatus.NOT_FOUND, "agreement " + agreementId + " not found");
        }
        SimpleMetricsReceiverData data = new SimpleMetricsReceiverData(
                metric.getMetricKey(), 
                new SimpleMetricsReceiverData.SimpleMetricValue(metric.getMetricValue(), metric.getDate()));
        
        Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap = translator.translate(agreement, data);
        enforcementService.doEnforcement(agreement, metricsMap);
        return buildResponse(HttpStatus.ACCEPTED, "Metrics received");
    }
    
}
