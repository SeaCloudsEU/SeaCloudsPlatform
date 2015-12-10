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

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.modaclouds.ViolationSubscriber;
import eu.atos.sla.parser.IParser;
import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Template;
import eu.atos.sla.service.rest.exception.InternalException;
import eu.atos.sla.service.rest.helpers.AgreementHelperE;
import eu.atos.sla.service.rest.helpers.TemplateHelperE;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;
import eu.seaclouds.platform.sla.generator.AgreementGenerator;
import eu.seaclouds.platform.sla.generator.JaxbUtils;
import eu.seaclouds.platform.sla.generator.RulesExtractor;
import eu.seaclouds.platform.sla.generator.SlaGeneratorException;
import eu.seaclouds.platform.sla.generator.SlaInfo;
import eu.seaclouds.platform.sla.generator.TemplateGenerator;
import eu.seaclouds.platform.sla.generator.SlaInfo.SlaInfoBuilder;

@Path("seaclouds")
@Component
@Transactional
public class SeacloudsRest extends AbstractSLARest {

    private static Logger logger = LoggerFactory.getLogger(SeacloudsRest.class);

    private SlaInfoBuilder slaInfoBuilder = new SlaInfoBuilder(new RulesExtractor());
    
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
    private TemplateHelperE templateHelper;
    
    @Autowired
    private IProviderDAO providerDAO;
    
    @Autowired
    private IAgreementDAO agreementDAO;
    
    @Autowired
    private ITemplateDAO templateDAO;

    @Resource(name="agreementXmlParser")
    IParser<Agreement> agreementParser;
    
    @Resource(name="templateXmlParser")
    IParser<Template> templateParser;
    
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
    @Path("templates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTemplate(@Context UriInfo uriInfo, @RequestBody String dam) throws InternalException {
        try {
            
            logger.info("\nPOST /seaclouds/templates\n{}", dam);
            
            SlaInfo slaInfo = slaInfoBuilder.build(dam);
            
            String id = createTemplate(slaInfo, true);
            String location = buildResourceLocation(uriInfo.getAbsolutePath().toString(), id);
            
            Map<String, String> map = Collections.singletonMap("id", id);
            
            ResponseBuilderImpl builder = new ResponseBuilderImpl();
            builder.header("location", location);
            builder.status(HttpStatus.CREATED.value());
            builder.entity(map);
            return builder.build();
            
        } catch (DBMissingHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (DBExistsHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (InternalHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (ParserHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (JAXBException e) {
            throw new InternalException(e.getMessage(), e);
        }
    }

    @POST
    @Path("templates")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTemplateFromDamAndRules(@Context UriInfo uriInfo, FormDataMultiPart form) throws InternalException {
        try {
            
            FormDataBodyPart damPart = form.getField("dam");
            FormDataBodyPart rulesPart = form.getField("rules");

            if (damPart == null || rulesPart == null) {
                String entity = ((damPart == null)? "DAM not found in multipart. " : "") + 
                                ((rulesPart == null)? "Monitoring rules not found in multipart. " : "");
                Response response = Response.
                        status(Response.Status.BAD_REQUEST).
                        entity(entity).
                        build();
                return response;
            }
            String rules = rulesPart.getValueAs(String.class);
            String dam = damPart.getValueAs(String.class);

            logger.info("\nPOST /seaclouds/templates\n{}\n{}", dam, rules);

            SlaInfo slaInfo = slaInfoBuilder.build(dam, rules);
            
            String id = createTemplate(slaInfo, true);
            String location = buildResourceLocation(uriInfo.getAbsolutePath().toString(), id);
            
            Map<String, String> map = Collections.singletonMap("id", id);
            
            ResponseBuilderImpl builder = new ResponseBuilderImpl();
            builder.header("location", location);
            builder.status(HttpStatus.CREATED.value());
            builder.entity(map);
            return builder.build();
            
        } catch (DBMissingHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (DBExistsHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (InternalHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (ParserHelperException e) {
            throw new InternalException(e.getMessage(), e);
        } catch (JAXBException e) {
            throw new InternalException(e.getMessage(), e);
        }
    }

    public String createTemplate(SlaInfo slaInfo, boolean persist) throws JAXBException,
            DBMissingHelperException, DBExistsHelperException,
            InternalHelperException, ParserHelperException {
        
        TemplateGenerator g = new TemplateGenerator(slaInfo);
        Template wsagTemplate = g.generate();

        String providerUuid = wsagTemplate.getContext().getAgreementResponder();
        
        String wsagSerialized = JaxbUtils.toString(wsagTemplate);
        String id = "<random-uuid>";

        if (persist) {
            
            getOrCreateProvider(providerUuid);
            id = templateHelper.createTemplate(wsagTemplate, wsagSerialized);
        }
        return id;
    }
    
    @POST
    @Path("agreements")
    public Response createAgreement(
            @Context UriInfo uriInfo, 
            @QueryParam("agreementId") String agreementId,
            @RequestBody String slaPayload) 
                    throws ParserException, InternalException {
        
        String id = createAgreementImpl(agreementId, slaPayload);
        String location = buildResourceLocation(uriInfo.getAbsolutePath().toString() ,id);
        logger.debug("EndOf createAgreement");
        return buildResponsePOST(
                HttpStatus.CREATED,
                createMessage(HttpStatus.CREATED, id, 
                        "The agreement has been stored successfully in the SLA Repository Database. "
                        + "It has location " + location), location);
    }

    /*
     * Maintained for backward compatibility
     */
    @Deprecated
    @POST
    @Path("agreements")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createAgreementMultipart(@Context UriInfo uriInfo, FormDataMultiPart form,
            @QueryParam("agreementId") String agreementId) 
            throws ParserException, InternalException {
        
        FormDataBodyPart slaPart = form.getField("sla");
        String slaPayload = slaPart.getValueAs(String.class);

        String id = createAgreementImpl(agreementId, slaPayload);
        String location = buildResourceLocation(uriInfo.getAbsolutePath().toString() ,id);
        logger.debug("EndOf createAgreement");
        return buildResponsePOST(
                HttpStatus.CREATED,
                createMessage(HttpStatus.CREATED, id, 
                        "The agreement has been stored successfully in the SLA Repository Database. "
                        + "It has location " + location), location);
    }

    private String createAgreementImpl(String agreementId, String slaPayload)
            throws ParserException, InternalException {
        String id;
        Agreement a = agreementParser.getWsagObject(slaPayload);
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
        } catch (DBMissingHelperException e) {
            throw new InternalException(e.getMessage());
        } catch (DBExistsHelperException e) {
            throw new InternalException(e.getMessage());
        } catch (InternalHelperException e) {
            throw new InternalException(e.getMessage());
        } catch (ParserHelperException e) {
            throw new InternalException(e.getMessage());
        }
        return id;
    }
    
    @POST
    @Path("commands/rulesready")
    public String rulesReady(@Context UriInfo uriInfo, @QueryParam("agreementId") String agreementId) {
        String slaUrl = getSlaUrl(this.slaUrl, uriInfo);
        String metricsUrl = getMetricsBaseUrl("", this.metricsUrl);
        /*
         * Endpoint of the metrics receiver. Something like http://localhost:8080/metrics
         */
        String slaMetricsUrl = getSlaMetricsUrl(slaUrl);
        
        if (agreementId == null) {
            agreementId = "";
        }
        
        List<IAgreement> agreements = "".equals(agreementId)? agreementDAO.getAll() :
            Collections.singletonList(agreementDAO.getByAgreementId(agreementId));

        ViolationSubscriber subscriber = new ViolationSubscriber(metricsUrl, slaMetricsUrl);
        for (IAgreement agreement : agreements) {
            subscriber.subscribeObserver(agreement);
            enforcementService.startEnforcement(agreement.getAgreementId());
        }
        return "";
    }
    
    @GET
    @Path("commands/fromtemplate")
    @Produces(MediaType.APPLICATION_XML)
    public Response generateAgreementFromTemplate(@QueryParam("templateId") String templateId) throws JAXBException {
        ITemplate template = templateDAO.getByUuid(templateId);

        Template wsagTemplate;
        try {
            wsagTemplate = templateParser.getWsagObject(template.getText());
        } catch (ParserException e) {
            throw new SlaGeneratorException(e.getMessage(), e);
        }
        Agreement wsagAgreement = new AgreementGenerator(wsagTemplate).generate();
        logger.debug(JaxbUtils.toString(wsagAgreement));
        
        ResponseBuilderImpl builder = new ResponseBuilderImpl();
        builder.status(HttpStatus.OK.value());
        builder.entity(wsagAgreement);
        return builder.build();
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
    
    private IProvider getOrCreateProvider(String providerUuid) {
        
        IProvider provider = providerDAO.getByUUID(providerUuid);
        if (provider == null) {
            provider = new Provider();
            provider.setUuid(providerUuid);
            provider.setName(providerUuid);
            provider = providerDAO.save(provider);
        }
        return provider;
    }
}
