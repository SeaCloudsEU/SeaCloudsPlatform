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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.service.rest.helpers.EnforcementJobHelper;
import eu.atos.sla.service.rest.helpers.exception.HelperException;

/**
 * Rest Service that exposes stored information from SLA enforcement
 * 
 */
@Deprecated
@Path("/enforcementso")
@Component
@Scope("request")
@Transactional
public class EnforcementJobRest extends AbstractSLARest{
    private static Logger logger = LoggerFactory.getLogger(EnforcementJobRest.class);

    @Autowired
    private EnforcementJobHelper helper;

    @Context
    private UriInfo _uriInfo;
    
    public EnforcementJobRest() {
    }



    /**
     * Get the list of available enforcements
     * 
     * <pre>
     *   GET /enforcements 
     *   
     *   Request:
     *       GET /enforcements HTTP/1.1
     *       Accept: application/xml
     *   
     *   Response:
     *   
     *  {@code
     *  <?xml version="1.0" encoding="UTF-8"?>
     *  <collection href="/enforcements">
     *  <items offset="0" total="1">
     *  
     * <enforcement_job>
     *    <agreement_id>agreement04</agreement_id>
     *     <enabled>false</enabled>
     * </enforcement_job>
     * 
     *  </items>
     *  </collection>
     *  }
     * 
     * </pre>
     * 
     * Example: <li>curl http://localhost:8080/sla-service/enforcements</li>
     * 
     * @return XML information with the different details of the different
     *         enforcements
     * 
     * @throws Exception
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getEnforcements() {
        logger.debug("StartOf getEnforcements - REQUEST for /enforcements");

        EnforcementJobHelper enforcementJobService = getHelper();
        String serializedEnforcements = null;
        
        try{
            serializedEnforcements = enforcementJobService.getEnforcements();
        } catch (HelperException e) {
            logger.info("getEnforcements exception:"+e.getMessage());
            return buildResponse(e);
        }
        logger.debug("EndOf getEnforcements");
        return buildResponse(200, serializedEnforcements);
    }

    /**
     * Gets an specific enforcements given a agreementId If the enforcementJob
     * it is not in the database, it returns 404 with empty payload
     * 
     * 
     * <pre>
     *   GET /enforcements/{agreementId}
     *   
     *   Request:
     *       GET /enforcements HTTP/1.1
     *   
     *   Response:
     *   
     *  {@code
     *  <?xml version="1.0" encoding="UTF-8"?>
     * 
     * <enforcement_job>
     *    <agreement_id>agreement04</agreement_id>
     *     <enabled>false</enabled>
     * </enforcement_job>
     * 
     *  }
     * 
     * </pre>
     * 
     * Example: <li>curl
     * http://localhost:8080/sla-service/enforcements/agreement04</li>
     * 
     * @param agreementId
     *            of the enforcementJob
     * @return XML information with the different details of the enforcementJob
     */
    @GET
    @Path("{agreementId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getEnforcementJobByAgreementId(@PathParam("agreementId") String agreementId){
        logger.debug("StartOf getEnforcementJobByAgreementId - REQUEST for /enforcementJobs/" + agreementId);

        try {
            EnforcementJobHelper enforcementJobService = getHelper();
            String serializedEnforcement = enforcementJobService.getEnforcementJobByUUID(agreementId);
            
            if (serializedEnforcement!=null){
                logger.debug("EndOf getEnforcementJobByAgreementId");
                return buildResponse(200, serializedEnforcement);
            }else{
                logger.debug("EndOf getEnforcementJobByAgreementId");
                return buildResponse(404, printError(404, "There is no enforcement with uuid " + agreementId
                        + " in the SLA Repository Database"));        
            }
        } catch (HelperException e) {
            logger.info("getEnforcementJobByAgreementId exception:"+e.getMessage());
            return buildResponse(e);
        }
    }

    /**
     * Enables an enforcement job
     * 
     * *
     * 
     * <pre>
     *    GET /enforcements/{agreementId}
     *    
     *    Request:
     *        GET /enforcements HTTP/1.1
     *    
     *    Response:
     *    
     *   {@code
     * 
     * The enforcement job with agreement-uuid e3bc4f6a-5f58-453b-9f59-ac3eeaee45b2has started
     * 
     *   }
     * 
     * </pre>
     * 
     * Example: <li>curl -X PUT localhost:8080/sla-service/enforcements/e3bc4f6a-5f58-453b-9f59-ac3eeaee45b2/start</li>
     * 
     * @param agreementId of the enforcementJob
     * @return information that the enforcementJob has been started
     */
    @PUT
    @Path("{agreementId}/start")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response startEnforcementJob(@PathParam("agreementId") String agreementId) {
        logger.debug("StartOf Start /enforcementJobs");

        EnforcementJobHelper enforcementJobService = getHelper();
        if (enforcementJobService.startEnforcementJob(agreementId))
            return buildResponse(202,
                    "The enforcement job with agreement-uuid " + agreementId
                            + "has started");
        else
            return buildResponse(
                    404,
                    printError(404,
                            "There has not been possible to start the enforcementJob with agreementId : "
                                    + agreementId
                                    + " in the SLA Repository Database"));

    }

    /**
     * Disables an enforcement job /** Enables an enforcement job
     * 
     * *
     * 
     * <pre>
     *    GET /enforcements/{agreementId}
     *    
     *    Request:
     *        GET /enforcements HTTP/1.1
     *    
     *    Response:
     *    
     *   {@code
     * 
     * The enforcement job with agreement-uuid e3bc4f6a-5f58-453b-9f59-ac3eeaee45b2has started
     * 
     *   }
     * 
     * </pre>
     * 
     * Example: <li>curl -X PUT localhost:8080/sla-service/enforcements/e3bc4f6a-5f58-453b-9f59-ac3eeaee45b2/stop</li>
     * 
     * @param agreementId
     *            of the enforcementJob
     * @return information that the enforcementJob has been stopped
     */
    @PUT
    @Path("{agreementId}/stop")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response stopEnforcementJob(
            @PathParam("agreementId") String agreementId)  {
        logger.debug("Stop /enforcementJobs");

        EnforcementJobHelper enforcementJobService = getHelper();
        
        if (enforcementJobService.stopEnforcementJob(agreementId))
            return buildResponse(200,
                    "The enforcement job with agreement-uuid " + agreementId
                            + "has stopped");
        else
            return buildResponse(
                    404,
                    printError(404,
                            "There has not been possible to start the enforcementJob with uuid : "
                                    + agreementId
                                    + " in the SLA Repository Database"));

    }

    /**
     * Creates a new enforcement
     * 
     * 
     * <pre>
     *  POST /enforcements
     *  
     * 
     *  Request:
     *      POST /agreements HTTP/1.1
     *      Accept: application/xml
     *  
     *  Response:
     * 
     *  {@code
     * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * <message code="201" message= "The enforcementJob has been stored successfully in the SLA Repository Database"/>      
     *  }
     * 
     * </pre>
     * 
     * Example: <li>curl -H "Content-type: application/xml" -X POST -d @enforcement.xml  localhost:8080/sla-service/enforcements</li>
     * 
     * @param id of the agreement
     * @return XML information with the different details of the agreement
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createEnforcementJob(@Context HttpHeaders hh, String payload){
        logger.debug("StartOf createEnforcementJob - REQUEST Insert /enforcement");
        
        EnforcementJobHelper enforcementJobService = getHelper();
        String location;
        try {
            location = enforcementJobService.createEnforcementJob(
                    hh, _uriInfo.getAbsolutePath().toString(), payload);
        } catch (HelperException e) {
            logger.info("createEnforcementJob exception", e);
            return buildResponse(e);
        }
        logger.debug("EndOf createEnforcementJob");
        return buildResponsePOST(
                HttpStatus.CREATED,
                printMessage(
                        HttpStatus.CREATED,
                        "The enforcementJob has been stored successfully in the SLA Repository Database"),
                location);
    }

    private EnforcementJobHelper getHelper() {
        return helper;
    }

}
