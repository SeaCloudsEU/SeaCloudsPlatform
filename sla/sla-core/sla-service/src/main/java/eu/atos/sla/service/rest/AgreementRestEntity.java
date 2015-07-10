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

import java.util.Date;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.atos.sla.service.rest.exception.ConflictException;
import eu.atos.sla.service.rest.exception.InternalException;
import eu.atos.sla.service.rest.exception.NotFoundException;
import eu.atos.sla.service.rest.helpers.AgreementHelperE;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;
import eu.atos.sla.service.types.AgreementParam;
import eu.atos.sla.service.types.BooleanParam;

/**
 * Rest Service that exposes all the stored information of the SLA core
 * 
 * 
 * An agreement is serialized as a ws-agreement xml. An example is:
 * 
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement"
 *   AgreementId="agreement03">
 * 
 *   <wsag:Name>ExampleAgreement</wsag:Name>
 *   <wsag:Context>
 *     <wsag:AgreementInitiator>customer-test</wsag:AgreementInitiator>
 *     <wsag:AgreementResponder>provider-test</wsag:AgreementResponder>
 *     <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
 *     <wsag:ExpirationTime>2014-03-07T12:00:00</wsag:ExpirationTime>
 *     <wsag:TemplateId>template02</wsag:TemplateId>
 *   </wsag:Context>
 *   <wsag:Terms>
 *     <wsag:All>
 *       <wsag:ServiceProperties Name="ServiceProperties" ServiceName="ServiceName">
 *         <wsag:VariableSet>
 *           <wsag:Variable Name="ResponseTime" Metric="xs:double">
 *             <wsag:Location>service-prueba/ResponseTime</wsag:Location>
 *           </wsag:Variable>
 *         </wsag:VariableSet>
 *       </wsag:ServiceProperties>
 *       <wsag:GuaranteeTerm Name="GT_ResponseTime">
 *         <wsag:ServiceScope ServiceName="ServiceName"/>
 *         <wsag:ServiceLevelObjective>
 *           <wsag:KPITarget>
 *             <wsag:KPIName>ResponseTime</wsag:KPIName>
 *             <wsag:CustomServiceLevel>
 *               "constraint" : "ResponseTime BETWEEN (0, 200)"
 *             </wsag:CustomServiceLevel>
 *           </wsag:KPITarget>
 *         </wsag:ServiceLevelObjective>
 *       </wsag:GuaranteeTerm>
 *     </wsag:All>
 *   </wsag:Terms>
 * </wsag:Agreement>
 * }</pre>
 * 
 */
@Path("/agreements")
@Component
@Scope("request")
public class AgreementRestEntity extends AbstractSLARest{
    private static Logger logger = LoggerFactory.getLogger(AgreementRestEntity.class);

    @Autowired
    private AgreementHelperE helper;

    
    public AgreementRestEntity() {
    }

    /**
     * Gets a the list of available agreements from where we can get metrics,
     * host information, etc.
     * 
     * <pre>
     *  GET /agreements{?providerId,consumerId,active}
     *  
     *  Request:
     *   GET /agreements HTTP/1.1
     *  
     *  Response:
     *   HTTP/1.1 200 OK
     *   Content-type: application/xml
     *   {@code
     *   <?xml version="1.0" encoding="UTF-8"?>
     *   <collection href="/agreements">
     *   <items offset="0" total="1">
     *   <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement"
     *     AgreementId="d25eea60-7cfe-11e3-baa7-0800200c9a66">
     *     ...
     *   </wsag:Agreement>
     *   </items>
     *   </collection>
     *   }
     * 
     * </pre>
     * 
     * Example: 
     * <li>curl http://localhost:8080/sla-service/agreements</li>
     * <li>curl http://localhost:8080/sla-service/agreements?consumerId=user-10343</li>
     * @throws NotFoundException 
     * 
     * @throws JAXBException
     */
    @GET
    public List<IAgreement> getAgreements(
            @QueryParam("consumerId") String consumerId,
            @QueryParam("providerId") String providerId,
            @QueryParam("templateId") String templateId,
            @QueryParam("active") BooleanParam active) {
        logger.debug("StartOf getAgreements - REQUEST for /agreements");
        AgreementHelperE agreementRestHelper = getAgreementHelper();
        List<IAgreement> agreements = agreementRestHelper.getAgreements(
                consumerId, providerId, templateId, BooleanParam.getValue(active));
            
        return agreements;
    }

    /**
     * Gets a the list of available agreements from where we can get metrics,
     * host information, etc.
     * 
     * </pre>
     * 
     * Example: 
     * <li>curl http://localhost:8080/sla-service/agreements</li>
     * <li>curl http://localhost:8080/sla-service/agreements?consumerId=user-10343</li>
     * @throws NotFoundException 
     * 
     * @throws JAXBException
     */
    @GET
    @Path("agreementsPerTemplateAndConsumer")
    @Deprecated
    public List<IAgreement> getAgreementsPerTemplateAndConsumer(
            @QueryParam("consumerId") String consumerId,
            @QueryParam("templateUUID") String templateUUID) {
        logger.debug("StartOf getAgreementsPerTemplateAndConsumer - REQUEST for /agreementsPerTemplateAndConsumer");
        AgreementHelperE agreementRestHelper = getAgreementHelper();
        List<IAgreement> agreements =  agreementRestHelper.getAgreementsPerTemplateAndConsumer(consumerId, templateUUID);
            
        return agreements;
    }
    
    
    /**
     * Gets the information of an specific agreement. If the agreement it is not
     * in the database, it returns 404 with empty payload
     * 
     * 
     * <pre>
     *  GET /agreements/{id}
     *  
     *  Request:
     *    GET /agreements HTTP/1.1
     *  
     *  Response:
     *    HTTP/1.1 200 OK
     *    Content-type: application/xml
     * 
     *    <?xml version="1.0" encoding="UTF-8"?>
     *    <wsag:Agreement>...</wsag:Agreement>
     *   
     *  
     * 
     * Example: <li>curl
     * http://localhost:8080/sla-service/agreements/agreement04</li>
     * 
     * 
     * @param id
     *            of the agreement
     * @return XML information with the different details of the agreement
     * @throws NotFoundException 
     */
    @GET
    @Path("{id}")
    public IAgreement  getAgreementById(@PathParam("id") String agreementId) throws NotFoundException {
        logger.debug("StartOf getAgreementById REQUEST for /agreements/" + agreementId);

        AgreementHelperE agreementRestHelper = getAgreementHelper();
        IAgreement agreement =  agreementRestHelper.getAgreementByID(agreementId);
        if (agreement==null){
            logger.info("getAgreementById NotFoundException: There is no agreement with id " + agreementId + " in the SLA Repository Database");            
            throw new NotFoundException("There is no agreement with id " + agreementId + " in the SLA Repository Database");        
        }
        logger.debug("EndOf getAgreementById"); 
        return agreement;

    }
    
    
    /**
     * Gets the context information of an specific agreement. 
     * 
     * 
     * <pre>
     *  GET /agreements/{id}
     *  
     *  Request:
     *    GET /agreements HTTP/1.1
     *  
     *  Response:
     *    HTTP/1.1 200 OK
     *    Content-type: application/xml
     * 
     *  
     * 
     * Example: <li>curl
     * http://localhost:8080/sla-service/agreements/context/agreement04</li>
     * 
     * 
     * @param id of the agreement
     * @return XML information with the different details of the context
     * @throws NotFoundException 
     * @throws InternalException 
     */    
    @GET
    @Path("{id}/context")
    public eu.atos.sla.parser.data.wsag.Context  getAgreementContextById(@PathParam("id") String agreementId) throws NotFoundException, InternalException {
        logger.debug("StartOf getAgreementContextById REQUEST for /agreements/{}/context", agreementId);

        AgreementHelperE agreementRestHelper = getAgreementHelper();
        eu.atos.sla.parser.data.wsag.Context context;
        try {
            context = agreementRestHelper.getAgreementContextByID(agreementId);
        } catch (InternalHelperException e) {
            logger.error("getAgreementContextById InternalException", e);             
            throw new InternalException(e.getMessage());
        }
        if (context==null){
            logger.info("getAgreementContextById NotFoundException: There is no agreement with id " + agreementId + " in the SLA Repository Database");                        
            throw new NotFoundException("There is no agreement with id " + agreementId + " in the SLA Repository Database");
        }
        logger.debug("EndOf getAgreementContextById"); 
        return context;
    }

    
    /**
     * Creates a new agreement
     * 
     * 
     * <pre>
     *  POST /agreements
     * 
     *  Request:
     *    POST /agreements HTTP/1.1
     *    Accept: application/xml
     *  
     *  Response:
     *    HTTP/1.1 201 Created
     *    Content-type: application/xml
     *    Location: http://.../agreements/$uuid
     * 
     *  {@code
     *    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     *    <message code="201" message= "The agreement has been stored successfully in the SLA Repository Database"/>      
     *  }
     * 
     * </pre>
     * 
     * Example: 
     * <li>curl -H "Content-type: application/xml" -d@agreement02.xml
     * localhost:8080/sla-service/agreements -X POST</li>
     * 
     * @return XML information with the different details of the agreement
     * @throws NotFoundException 
     * @throws ConflictException 
     * @throws InternalException 
     */
    @POST
    public Response createAgreement(@Context UriInfo uriInfo,  AgreementParam agrementParam) throws NotFoundException, ConflictException, InternalException{
        logger.debug("StartOf createAgreement - Insert /agreements");
        String id, location = null;
        try{
            AgreementHelperE agreementRestHelper = getAgreementHelper();
            id = agreementRestHelper.createAgreement(agrementParam.getAgreement(), agrementParam.getOriginalSerialzedAgreement());
            location = buildResourceLocation(uriInfo.getAbsolutePath().toString() ,id);
            logger.debug("EndOf createAgreement");
        } catch (DBMissingHelperException e) {
            logger.info("createAgreement ConflictException"+ e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (DBExistsHelperException e) {
            logger.info("createAgreement ConflictException"+ e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (InternalHelperException e) {
            logger.info("createAgreement InternalException", e);
            throw new InternalException(e.getMessage());
        } catch (ParserHelperException e) {
            logger.info("createAgreement InternalException", e);
            throw new InternalException(e.getMessage());
        }
        logger.debug("EndOf createAgreement");
        return buildResponsePOST(
                HttpStatus.CREATED,
                createMessage(HttpStatus.CREATED, id, 
                        "The agreement has been stored successfully in the SLA Repository Database. It has location "+location), location);
    }

    @GET
    @Path("active")
    public List<IAgreement> getActiveAgreements() throws NotFoundException  {
        logger.debug("StartOf getActiveAgreements - Get active agreements");
        long actualDate = new Date().getTime();
        AgreementHelperE agreementRestHelper = getAgreementHelper();
        List<IAgreement> agreements = agreementRestHelper.getActiveAgreements(actualDate);
        logger.debug("EndOf getActiveAgreements");
        return agreements;
    }

    /**
     * Deletes an agreement, passing the corresponding agreement_id as
     * parameter.
     * 
     * 
     * <pre>
     *  DELETE /agreements/{agreement_id}
     * 
     *  Request:
     *      DELETE /agreements HTTP/1.1
     *  
     *  Response:
     *    HTTP/1.1 200 Ok
     *    Content-type: application/xml
     *  
     *  {@code
     *    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     *    <message code="201" message= "The agreement has been deleted successfully in the SLA Repository Database"/>      
     *  }
     * 
     * </pre>
     * 
     * Example: 
     * <li>curl -X DELETE
     * localhost:8080/sla-service/agreements/agreement04</li>
     * 
     * @throws Exception
     */

    @DELETE
    @Path("{agreementId}")
    public Response deleteAgreement(@PathParam("agreementId") String agreementId){
        logger.debug("DELETE /agreements/{}", agreementId);

        AgreementHelperE agreementRestHelper = getAgreementHelper();
        boolean deleted = agreementRestHelper.deleteByAgreementId(agreementId);
        if (deleted)
            return buildResponse(HttpStatus.OK,
                    "The agreement id " + agreementId + "with it's enforcement job was successfully deleted");
        else{
            logger.info("getAgreementContextById NotFoundException: There is no agreement with id " + agreementId + " in the SLA Repository Database");                        
            return buildResponse(HttpStatus.NOT_FOUND, 
                    printError(HttpStatus.NOT_FOUND, "There is no agreement with id "
                            + agreementId + " in the SLA Repository Database"));
        }
        
    }




    /**
     * Gets the information of the status of the different Guarantee Terms of an
     * agreement. *
     * 
     * <pre>
     * GET /agreements/{agreementId}/guaranteestatus
     *   
     * Request:
     *   GET /agreements HTTP/1.1
     *    
     * Response:
     *   HTTP/1.1 200 Ok
     *   Content-type: application/xml or application/json 
     *  
     *  In case of application/xml
     * {@code
     *   <GuaranteeStatus agreementId="$agreementId" value="FULFILLED|VIOLATED|NON_DETERMINED">
     *     <GuaranteeTermStatus name="$gt_name1" value="FULFILLED|VIOLATED|NON_DETERMINED"/>
     *    ...
     *     <GuaranteeTermStatus name="$gt_nameN" value="FULFILLED|VIOLATED|NON_DETERMINED"/>
     *   </GuaranteeStatus>
     * }
     * 
     *  In case of application/json
     * {@code
     * {"agreementId":"{agreementId}","value":"FULFILLED|VIOLATED|NON_DETERMINED",
     * "GuaranteeTermStatus":
     * [{"name":"{gt_name1}","value":"FULFILLED|VIOLATED|NON_DETERMINED"},
     * {"name":"{gt_name2}","value":"FULFILLED|VIOLATED|NON_DETERMINED"}]}
     * }
     *  
     * </pre>
     * 
     * Example: 
     * <li>curl -H "Content-type: application/xml" http://localhost:8080/sla-service/agreements/{agreementId}/guaranteestatus</li>
     * 
     * @return Json information with Guarantee Status
     */
    @GET
    @Path("{id}/guaranteestatus")
    public GuaranteeTermsStatus  getStatusAgreement(@PathParam("id") String agreementId) throws NotFoundException{
        logger.debug("StartOf getStatusAgreement - REQUEST for /agreements/" + agreementId
                + "/guaranteestatus");
        GuaranteeTermsStatus guaranteeTermsStatus = null;
        try{
            AgreementHelperE agreementRestHelper = getAgreementHelper();
            guaranteeTermsStatus =  agreementRestHelper.getAgreementStatus(agreementId);
        } catch (DBMissingHelperException e) {
            logger.info("getStatusAgreement NotFoundException:"+e.getMessage());
            throw new NotFoundException(e.getMessage()); 
        }
        logger.debug("EndOf getStatusAgreement");
        return guaranteeTermsStatus;
    }

    private AgreementHelperE getAgreementHelper() {
        return helper;
    }
    
}
