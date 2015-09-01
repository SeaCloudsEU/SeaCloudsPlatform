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

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.modaclouds.ViolationSubscriber;
import eu.atos.sla.parser.IParser;
import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.service.rest.exception.InternalException;
import eu.atos.sla.service.rest.helpers.AgreementHelperE;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;

@Path("seaclouds")
@Component
@Transactional
public class SeacloudsRest extends AbstractSLARest {

    private static Logger logger = LoggerFactory.getLogger(SeacloudsRest.class);

    /*
     * Base of monitoring manager /metrics endpoint. Something like http://localhost:8170/v1/metrics
     */
    @Value("${MONITOR_METRICS_URL}")
    private String metricsUrl;

    /*
     * Base of SLA Core component. Something like http://localhost:8080/sla-service
     */
    @Value("${SLA_URL}")
    private String slaUrl;
    
    @Autowired
    private AgreementHelperE agreementHelper;
    
    @Autowired
    private IProviderDAO providerDAO;
    
    @Autowired
    private IAgreementDAO agreementDAO;

    @Resource(name="agreementXmlParser")
    IParser<Agreement> xmlParser;
    
    @Autowired
    private IEnforcementService enforcementService;

    @GET
    public String getRoot() {
        return "root";
    }
    
    @GET
    @Path("config")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConfig(@Context UriInfo uriInfo) {
        StringBuilder s = new StringBuilder();

        String slaUrl = getSlaUrl(this.slaUrl, uriInfo);
        String metricsUrl = getMetricsBaseUrl("", this.metricsUrl);

        s.append("SLA_URL=");
        s.append(slaUrl);
        s.append("\n");
        s.append("METRICS_URL=");
        s.append(metricsUrl);
        s.append("\n");
        
        return s.toString();
    }
    
    @POST
    @Path("agreements")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createAgreement(@Context UriInfo uriInfo, FormDataMultiPart form,
            @QueryParam("agreementId") String agreementId) 
            throws ParserException, InternalException {
        
        FormDataBodyPart slaPart = form.getField("sla");
        String slaPayload = slaPart.getValueAs(String.class);

        FormDataBodyPart rulesPart = form.getField("rules");
        
        String id;
        String location = null;
        Agreement a = xmlParser.getWsagObject(slaPayload);
        try {
            String providerUuid = a.getContext().getAgreementResponder();
            IProvider provider = providerDAO.getByUUID(providerUuid);
            if (provider == null) {
                provider = new Provider();
                provider.setUuid(providerUuid);
                provider.setName(providerUuid);
                provider = providerDAO.save(provider);
            }
            id = agreementHelper.createAgreement(a, slaPayload, agreementId != null? agreementId : "");
            location = buildResourceLocation(uriInfo.getAbsolutePath().toString() ,id);
        } catch (DBMissingHelperException e) {
            throw new InternalException(e.getMessage());
        } catch (DBExistsHelperException e) {
            throw new InternalException(e.getMessage());
        } catch (InternalHelperException e) {
            throw new InternalException(e.getMessage());
        } catch (ParserHelperException e) {
            throw new InternalException(e.getMessage());
        }
        logger.debug("EndOf createAgreement");
        return buildResponsePOST(
                HttpStatus.CREATED,
                createMessage(HttpStatus.CREATED, id, 
                        "The agreement has been stored successfully in the SLA Repository Database. "
                        + "It has location " + location), location);
    }
    
    @POST
    @Path("commands/rulesready")
    public String rulesReady(@Context UriInfo uriInfo) {
        String slaUrl = getSlaUrl(this.slaUrl, uriInfo);
        String metricsUrl = getMetricsBaseUrl("", this.metricsUrl);
        /*
         * Endpoint of the metrics receiver. Something like http://localhost:8080/metrics
         */
        String slaMetricsUrl = getSlaMetricsUrl(slaUrl);
        
        List<IAgreement> agreements = agreementDAO.getAll();

        ViolationSubscriber subscriber = new ViolationSubscriber(metricsUrl, slaMetricsUrl);
        for (IAgreement agreement : agreements) {
            subscriber.subscribeObserver(agreement);
            enforcementService.startEnforcement(agreement.getAgreementId());
        }
        return "";
    }
    
    /**
     * Returns base url of the sla core.
     * 
     * If the SLA_URL env var is set, returns that value. Else, get the base url from the
     * context of the current REST call. This second value may be wrong because this base url must
     * be the value that the MonitoringPlatform needs to use to connect to the SLA Core.
     */
    private String getSlaUrl(String envSlaUrl, UriInfo uriInfo) {
        String baseUrl = uriInfo.getBaseUri().toString();
        
        if (envSlaUrl == null) {
            envSlaUrl = "";
        }
        String result = ("".equals(envSlaUrl))? baseUrl : envSlaUrl;
        logger.debug("getSlaUrl(env={}, supplied={}) = {}", envSlaUrl, baseUrl, result);
        
        return result;
    }
    
    /**
     * Return base url of the metrics endpoint of the Monitoring Platform.
     * 
     * If an url is supplied in the request, use that value. Else, use MODACLOUDS_METRICS_URL env var is set.
     */
    private String getMetricsBaseUrl(String suppliedBaseUrl, String envBaseUrl) {
        
        String result = ("".equals(suppliedBaseUrl))? envBaseUrl : suppliedBaseUrl;
        logger.debug("getMetricsBaseUrl(env={}, supplied={}) = {}", envBaseUrl, suppliedBaseUrl, result);
        
        return result;
    }
    
    private String getSlaMetricsUrl(String slaUrl) {
        return slaUrl + "/metrics";
    }
    
}
