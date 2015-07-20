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
import javax.ws.rs.DELETE;
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

import eu.atos.sla.service.rest.helpers.TemplateHelper;
import eu.atos.sla.service.rest.helpers.exception.HelperException;

/**
 * Rest Service that exposes all the stored information of the SLA core
 * 
 * A template is serialized as a ws-template xml. An example is:
 * <pre>{@code
 * <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" TemplateId="contract-template-2007-12-04"> 
 *   <wsag:Name>ExampleTemplate</wsag:Name> 
 *   <wsag:Context> 
 *     <wsag:AgreementInitiator>Provider</wsag:AgreementInitiator> 
 *     <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider> 
 *     <wsag:ExpirationTime>2013-12-15-1200</wsag:ExpirationTime> 
 *     <wsag:TemplateId>contract-template-2013-12-15</wsag:TemplateId> 
 *   </wsag:Context> 
 *   <wsag:Terms> 
 *     <wsag:All>
 *       <!-- functional description --> 
 *       <wsag:ServiceDescriptionTerm wsag:Name="General" wsag:ServiceName="Service0001"> A GPS service </wsag:ServiceDescriptionTerm>
 *       <wsag:ServiceDescriptionTerm wsag:Name="GetCoordsOperation" wsag:ServiceName="GPSService0001"> operation to call to get the coords </wsag:ServiceDescriptionTerm>
 *       <!-- domain specific reference to a service (additional or optional to SDT) -->
 *       <wsag:ServiceReference wsag:Name="CoordsRequest" wsag:ServiceName="GPSService0001">
 *         <wsag:EndpointReference>
 *           <wsag:Address>http://www.gps.com/coordsservice/getcoords</wsag:Address>
 *           <wsag:ServiceName>gps:CoordsRequest</wsag:ServiceName>
 *         </wsag:EndpointReference>
 *       </wsag:ServiceReference>
 *       <!-- non-functional properties -->
 *       <wsag:ServiceProperties wsag:Name="AvailabilityProperties" wsag:ServiceName="GPS0001">
 *         <wsag:Variables>
 *           <wsag:Variable wsag:Name="ResponseTime" wsag:Metric="metric:Duration">
 *             <wsag:Location>qos:ResponseTime</wsag:Location>
 *           </wsag:Variable>
 *         </wsag:Variables>
 *       </wsag:ServiceProperties>
 *       <wsag:ServiceProperties wsag:Name="UsabilityProperties" wsag:ServiceName="GPS0001">
 *         <wsag:Variables>
 *           <wsag:Variable wsag:Name="CoordDerivation" wsag:Metric="metric:CoordDerivationMetric">
 *             <wsag:Location>qos:CoordDerivation</wsag:Location>
 *           </wsag:Variable>
 *         </wsag:Variables>
 *       </wsag:ServiceProperties>
 *       <!-- statements to offered service level(s) -->
 *       <wsag:GuaranteeTerm Name="FastReaction" Obligated="ServiceProvider">
 *         <wsag:ServiceScope ServiceName="GPS0001"> http://www.gps.com/coordsservice/getcoords </wsag:ServiceScope>
 *         <wsag:QualifyingCondition> applied when current time in week working hours </wsag:QualifyingCondition>
 *         <wsag:ServiceLevelObjective>
 *           <wsag:KPITarget>
 *             <wsag:KPIName>FastResponseTime</wsag:KPIName>
 *             <wsag:Target> //Variable/@Name="ResponseTime" LOWERTHAN 1 second </wsag:Target>
 *           </wsag:KPITarget>
 *         </wsag:ServiceLevelObjective>
 *       </wsag:GuaranteeTerm>
 *     </wsag:All>
 *   </wsag:Terms>
 * </wsag:Template>
 * }</pre>
 * 
 */
@Deprecated
@Path("/templateso")
@Component
@Scope("request")
@Transactional
public class TemplateRest extends AbstractSLARest{
    private static Logger logger = LoggerFactory.getLogger(TemplateRest.class);

    @Autowired
    private TemplateHelper helper;

    public TemplateRest() {
    }

    
    /**
     * Gets a the list of available templates from where we can get metrics,
     * host information, etc.
     * 
     * <pre>
     *   GET /templates 
     *   
     *   Request:
     *       GET /templates HTTP/1.1
     *   
     *   Response:
     *   
     *   HTTP/1.1 200 Ok
     *   {@code
     * <?xml version="1.0" encoding="UTF-8"?>
     * <collection href="/templates">
     *   <items offset="0" total="1">
     *     <wsag:Template>...</wsag:Template>
     *     ...
     *   </items>
     * </collection>
     * }
     * 
     * </pre>
     * 
     * Example: <li>curl http://localhost:8080/sla-service/templates</li>
     * 
     * @return XML information with the different details and urls of the
     *         templates
     * 
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getTemplates()  {
        logger.debug("StartOf getTemplates - REQUEST for /templates");

        TemplateHelper templateRestService = getTemplateHelper();
        String serializedTemplate = null;
        try {
            serializedTemplate = templateRestService.getTemplates();
        } catch (HelperException e) {
            logger.info("getTemplates exception:"+e.getMessage());
            return buildResponse(e);
        }
        logger.debug("EndOf getTemplates");
        return buildResponse(200, serializedTemplate);
            
    }

    /**
     * Gets the information of an specific template.
     * 
     * <pre>
     *   GET /templates/{template_id} 
     *   
     *   Request:
     *   
     *       GET /templates/{template_id} HTTP/1.1
     *   
     *   Response:
     *   
     *   HTTP/1.1 200 Ok
     *  
     *   {@code
     * <?xml version="1.0" encoding="UTF-8"?>
     * <wsag:Template>...</wsag:Template>
     * 
     * }
     * 
     * </pre>
     * 
     * Example: <li>curl
     * http://localhost:8080/sla-service/templates/contract-template-2007-12-04</li>
     * 
     * @return XML information with the different details of the templates
     * 
     * @throws JAXBException
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getTemplateByUuid(@PathParam("id") String templateId){
        logger.debug("StartOf getTemplateByUuid - REQUEST for /templates/{uuid}" + templateId);
        try{
            TemplateHelper templateRestService = getTemplateHelper();
            String serializedTemplate = templateRestService.getTemplateByUUID(templateId);
            if (serializedTemplate!=null){
                logger.debug("EndOf getTemplateByUuid"); 
                return buildResponse(200, serializedTemplate);
            }else{
                logger.debug("EndOf getTemplateByUuid"); 
                return buildResponse(404, printError(404, "There is no template with uuid " + templateId
                        + " in the SLA Repository Database"));
            }
        } catch (HelperException e) {
            logger.info("getTemplateByUuid exception:"+e.getMessage());
            return buildResponse(e);
        }
            

    }

    /**
     * Deletes a template given the corresponding template_id
     * 
     * 
     * <pre>
     *   DELETE /templates/{template_id}
     *   
     *  
     *   Request:
     *       DELETE /templates HTTP/1.1
     *   
     *   Response:
     *  
     *   {@code
     *  <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * <message code="204" message="The template with uuid:contract-template-2007-12-04 was deleted successfully"/>      
     *   }
     * 
     * </pre>
     * 
     * Example: <li>curl -X DELETE
     * localhost:8080/sla-service/templates/contract-template-2007-12-04</li>
     * 
     * @param id
     *            of the template
     * @throws Exception
     */
    @DELETE
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteTemplate(@PathParam("uuid") String uuid) {
        logger.debug("DELETE /templates/" + uuid);

        TemplateHelper templateRestService = getTemplateHelper();
        boolean deleted;
        try {
            deleted = templateRestService.deleteTemplateByUuid(uuid);
            if (deleted)
                return buildResponse(
                        204,
                        printMessage(204, "The template with uuid:" + uuid
                                + " was deleted successfully"));
            else
                return buildResponse(
                        404,
                        printError(404, "There is no template with uuid " + uuid
                                + " in the SLA Repository Database"));
            
        } catch (HelperException e) {
            // TODO Auto-generated catch block
            return buildResponse(e);
        }
    }

    /**
     * Returns the information of an specific template If the template it is not
     * in the database, it returns 404 with empty payload /** Creates a new
     * agreement
     * 
     * 
     * <pre>
     *  POST /templates
     * 
     *  Request:
     *    POST /templates HTTP/1.1
     *    Accept: application/xml
     *  
     *  Response:
     *    HTTP/1.1 201 Created
     *    Content-type: application/xml
     *    Location: http://.../templates/$uuid
     * 
     *  {@code
     *    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     *    <message code="201" message= "The template has been stored successfully in the SLA Repository Database"/>
     *  }
     * </pre>
     * 
     * Example: <li>curl -H "Content-type: application/xml" -X POST -d @template01.xml localhost:8080/sla-service/templates</li>
     * 
     * @return XML information that the template has been created successfully
     * 
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createTemplate(@Context HttpHeaders hh,@Context UriInfo uriInfo, String payload) {
        logger.debug("StartOf createTemplate - Insert /templates");

        TemplateHelper templateRestHelper = getTemplateHelper();
        try {
            String location = templateRestHelper.createTemplate(hh, uriInfo.getAbsolutePath().toString(), payload);
            
            logger.debug("EndOf createTemplate");
            return buildResponsePOST(
                    HttpStatus.CREATED,
                    printMessage(
                            HttpStatus.CREATED,
                            "The template has been stored successfully in the SLA Repository Database"),
                    location);
        } catch (HelperException e) {
            logger.info("createTemplate exception:"+e.getMessage());
            return buildResponse(e);
        }
    }

    /**
     * Updates the information of an specific template 
     * If the template it is not in the database, it returns 404 with empty payload
     * 
     * <pre>
     *   PUT /templates
     *   
     *  
     *   Request:
     *       POST /templates HTTP/1.1
     *       Accept: application/xml
     *   
     *   Response:
     *  
     *   {@code
     *  <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
     * <message code="204" message="The template with uuid:contract-template-2007-12-04 was updated successfully"/>      
     *   }
     * 
     * </pre>
     * 
     * Example: <li>curl -H "Content-type: application/xml" -X PUT -d @template01.xml localhost:8080/sla-service/templates</li>
     * 
     * @return XML information that the template has been updated successfully
     * 
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response updateTemplate(@Context HttpHeaders hh, String payload){
        logger.debug("Insert /templates");
        try{
            TemplateHelper templateRestService = getTemplateHelper();
            String serializedTemplate = templateRestService.updateTemplate(hh, payload);
            if (serializedTemplate !=null)
                return buildResponse(500, printError(500, "Error updating template the SLA Repository Database"));
            else
                return buildResponse(200, serializedTemplate);
        } catch (HelperException e) {
            logger.info("updateTemplate exception:"+e.getMessage());
            return buildResponse(e);
        }
    }
    
    
    private TemplateHelper getTemplateHelper() {
        return helper;
    }
}
