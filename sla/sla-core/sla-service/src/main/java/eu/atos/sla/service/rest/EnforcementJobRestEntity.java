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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import eu.atos.sla.parser.data.EnforcementJob;
import eu.atos.sla.service.rest.exception.ConflictException;
import eu.atos.sla.service.rest.exception.InternalException;
import eu.atos.sla.service.rest.exception.NotFoundException;
import eu.atos.sla.service.rest.helpers.EnforcementJobHelperE;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;

/**
 * Rest Service that exposes stored information from SLA enforcement
 * 
 */
@Path("/enforcements")
@Component
@Scope("request")
@Transactional
public class EnforcementJobRestEntity extends AbstractSLARest{
    private static Logger logger = LoggerFactory.getLogger(EnforcementJobRestEntity.class);

    @Autowired
    private EnforcementJobHelperE helper;

    
    public EnforcementJobRestEntity() {
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
    public List<EnforcementJob> getEnforcements() {
        logger.debug("StartOf getEnforcements - REQUEST for /enforcements");
        EnforcementJobHelperE enforcementJobHelper = getHelper();
        List<EnforcementJob> enforcementJobList = enforcementJobHelper.getEnforcements();
        logger.debug("EndOf getEnforcements");
        return enforcementJobList;
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
    public EnforcementJob getEnforcementJobByAgreementId(@PathParam("agreementId") String agreementUUID) throws NotFoundException{
        logger.debug("StartOf getEnforcementJobByAgreementId - REQUEST for /enforcements/{}", agreementUUID);
        EnforcementJobHelperE enforcementJobService = getHelper();
        EnforcementJob enforcementJob= enforcementJobService.getEnforcementJobByUUID(agreementUUID);
        if (enforcementJob==null){
            logger.info("getEnforcementJobByAgreementId NotFoundException: There is no agreement with uuid " + agreementUUID + " in the SLA Repository Database");                                    
            throw new NotFoundException("There is no enforcement job associated to the agreement with uuid " + agreementUUID + " in the SLA Repository Database");
        }
        logger.debug("EndOf getEnforcementJobByAgreementId");
        return enforcementJob;
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
     *       Accpets application/xml  or application/json
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
    public Response startEnforcementJob(@PathParam("agreementId") String agreementId) {
        logger.debug("StartOf startEnforcementJob - Start /enforcementJobs");

        EnforcementJobHelperE enforcementJobHelper = getHelper();
        if (enforcementJobHelper.startEnforcementJob(agreementId))
            return buildResponse(HttpStatus.ACCEPTED,
                    "The enforcement job with agreement-uuid " + agreementId
                            + " has started");
        else{
            logger.info("startEnforcementJob ForbiddenException: There has not been possible to start the enforcementJob with agreementId : "
                    + agreementId + " in the SLA Repository Database");                                    
            return buildResponse(
                    HttpStatus.FORBIDDEN,
                    printError(HttpStatus.FORBIDDEN,
                            "There has not been possible to start the enforcementJob with agreementId : "
                                    + agreementId
                                    + " in the SLA Repository Database"));
        }

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
     *    
     *    Response:
     *       Accepts: application/xml or application/json
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
    public Response stopEnforcementJob(@PathParam("agreementId") String agreementId)  {
        logger.debug("Stop /enforcements");

        EnforcementJobHelperE enforcementJobHelper = getHelper();
        
        if (enforcementJobHelper.stopEnforcementJob(agreementId))
            return buildResponse(HttpStatus.OK,
                    "The enforcement job with agreement-uuid " + agreementId
                            + " has stopped");
        else{
            logger.info("stopEnforcementJob ForbiddenException: There has not been possible to stop the enforcementJob with agreementId : "
                    + agreementId + " in the SLA Repository Database");                                    
            return buildResponse(
                    HttpStatus.FORBIDDEN,
                    printError(HttpStatus.FORBIDDEN,
                            "There has not been possible to stop the enforcementJob with uuid : "
                                    + agreementId
                                    + " in the SLA Repository Database"));

        }
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
     *      Accept: application/xml  or application/json
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
    public Response createEnforcementJob(@Context UriInfo uriInfo, @RequestBody EnforcementJob enforcementJob) throws ConflictException, InternalException, NotFoundException{
        logger.debug("StartOf createEnforcementJob - REQUEST Insert /enforcement");
        
        EnforcementJobHelperE enforcementJobHelper = getHelper();
        String id, location = null;
        try {
            id = enforcementJobHelper.createEnforcementJob(enforcementJob);
            location = buildResourceLocation(uriInfo.getAbsolutePath().toString(), id);
        } catch (DBExistsHelperException e) {
            logger.info("createEnforcementJob ConflictException:"+ e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (InternalHelperException e) {
            logger.info("createEnforcementJob InternalException:", e);
            throw new InternalException(e.getMessage());
        } catch (DBMissingHelperException e){
            logger.info("createEnforcementJob DBMissingHelperException:"+ e.getMessage());
            throw new NotFoundException(e.getMessage());            
        }
        logger.debug("EndOf createEnforcementJob");
        return buildResponsePOST(
                HttpStatus.CREATED,
                createMessage(
                        HttpStatus.CREATED, id,
                        "The enforcementJob has been stored successfully in the SLA Repository Database"),
                location);
    }

    private EnforcementJobHelperE getHelper() {
        return helper;
    }

}
