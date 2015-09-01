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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
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

import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.service.rest.exception.ConflictException;
import eu.atos.sla.service.rest.exception.InternalException;
import eu.atos.sla.service.rest.exception.NotFoundException;
import eu.atos.sla.service.rest.helpers.TemplateHelperE;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;
import eu.atos.sla.service.types.TemplateParam;

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
@Path("/templates")
@Component
@Scope("request")
@Transactional
public class TemplateRestEntity extends AbstractSLARest{
    private static Logger logger = LoggerFactory.getLogger(TemplateRestEntity.class);

    @Autowired
    private TemplateHelperE helper;

    public TemplateRestEntity() {
    }

    
    /**
     * Gets a the list of available templates from where we can get metrics,
     * host information, etc.
     * 
     * <pre>
     *   GET /templates
     *   
     *   Request:
     *       GET /templates{?serviceId} HTTP/1.1
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
     *             <li>curl http://localhost:8080/sla-service/templates?serviceIds=service02</li>
     *             <li>curl http://localhost:8080/sla-service/templates?serviceIds=service02,service03</li>
     * @return XML information with the different details and urls of the
     *         templates
     * 
     */
    @GET
    public List<ITemplate> getTemplates(@QueryParam("providerId") String providerId, @QueryParam("serviceIds") String serviceIds)  {
        logger.debug("StartOf getTemplates - REQUEST for /templates");
        TemplateHelperE templateRestHelper = getTemplateHelper();
        // we remove the blank spaces just in case somebody is adding them
        String serviceIdsSplitted[] = null;
        if (serviceIds!=null){
            String serviceIdsSplittedTmp[] = serviceIds.split(",");
            serviceIdsSplitted = new String[serviceIdsSplittedTmp.length];
            String tmp = "";
            for (int i=0; i<serviceIdsSplittedTmp.length;i++){
                serviceIdsSplitted[i] = serviceIdsSplittedTmp[i].trim();
                tmp+= "  "+serviceIdsSplitted[i];
            }
            logger.debug("getTemplates will search for service ids:"+tmp);
        }
        List<ITemplate> templates =  templateRestHelper.getTemplates(providerId, serviceIdsSplitted);
        logger.debug("EndOf getTemplates");
        return templates;
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
     * @throws NotFoundException 
     * 
     */
    @GET
    @Path("{id}")
    public ITemplate getTemplateByUuid(@PathParam("id") String templateId) throws NotFoundException{
        logger.debug("StartOf getTemplateByUuid - REQUEST for /templates/{}", templateId);
        TemplateHelperE templateRestHelper = getTemplateHelper();
        ITemplate template = templateRestHelper.getTemplateByUUID(templateId);
        if (template==null){
            logger.info("getTemplateByUuid NotFoundException: There is no template with id " + templateId + " in the SLA Repository Database");            
            throw new NotFoundException("There is no template with id " + templateId + " in the SLA Repository Database");        
        }            
        return template;
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
    public Response deleteTemplate(@PathParam("uuid") String uuid) {
        logger.debug("DELETE /templates/{}", uuid);
        TemplateHelperE templateRestHelper = getTemplateHelper();
        boolean deleted;
        try {
            deleted = templateRestHelper.deleteTemplateByUuid(uuid);
            if (deleted)
                return buildResponse(HttpStatus.OK, /*egarrido it was returned HttpStatus.NO_CONTENT, I don't know why */
                        "Template with uuid " + uuid + " was deleted successfully");
            else
                return buildResponse(HttpStatus.NOT_FOUND, 
                        printError(HttpStatus.NOT_FOUND, "There is no template with uuid "
                                + uuid + " in the SLA Repository Database"));
        } catch (DBExistsHelperException e) {
            logger.info("deleteTemplate Conflict:"+e.getMessage());
            return buildResponse(HttpStatus.CONFLICT, printError(HttpStatus.CONFLICT, e.getMessage()));
        }
    }

    /**
     * Returns the information of an specific template If the template it is not
     * in the database, it returns 404 with empty payload /** Creates a new
     * template
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
     * @throws NotFoundException 
     * @throws ConflictException 
     * @throws InternalException 
     * 
     */
    @POST
    public Response createTemplate(@Context UriInfo uriInfo, TemplateParam templateParam) throws NotFoundException, ConflictException, InternalException {
        logger.debug("StartOf createTemplate - Insert /templates");

        TemplateHelperE templateRestHelper = getTemplateHelper();
        String id, location = null;
        try {
            id = templateRestHelper.createTemplate(templateParam.getTemplate(), templateParam.getOriginalSerialzedTemplate());
            location = buildResourceLocation(uriInfo.getAbsolutePath().toString() ,id);
        } catch (DBMissingHelperException e) {
            logger.info("createTemplate ConflictException"+ e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (DBExistsHelperException e) {
            logger.info("createTemplate ConflictException"+ e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (InternalHelperException e) {
            logger.info("createTemplate InternalException", e);
            throw new InternalException(e.getMessage());
        } catch (ParserHelperException e) {
            logger.info("createTemplate ParserHelperException", e);
            throw new InternalException(e.getMessage());
        }
        logger.debug("EndOf createTemplate");
        return buildResponsePOST(
            HttpStatus.CREATED,
            createMessage(
                    HttpStatus.CREATED, id,
                    "The template has been stored successfully in the SLA Repository Database. It has location "+location),location);
        
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
     * @throws InternalException 
     * @throws NotFoundException 
     * 
     */
    @PUT
    @Path("{uuid}")
    public ITemplate updateTemplate(@PathParam("uuid") String uuid, TemplateParam templateParam) throws InternalException, NotFoundException, ConflictException{
        logger.debug("StartOf updateTemplate");
        ITemplate updatedTemplate = null;
        try{
            TemplateHelperE templateRestHelper = getTemplateHelper();
            updatedTemplate = templateRestHelper.updateTemplate(uuid, templateParam.getTemplate(), templateParam.getOriginalSerialzedTemplate());
        } catch (ParserHelperException e) {
            logger.info("updateTemplate exception", e);
            throw new InternalException(e.getMessage());
        } catch (InternalHelperException e) {
            logger.info("updateTemplate ConflictException:"+ e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (DBMissingHelperException e) {
            logger.info("updateTemplate ConflictException: "+ e.getMessage());
            throw new ConflictException(e.getMessage());
        } catch (DBExistsHelperException e){
            logger.info("updateTemplate ConflictException: "+ e.getMessage());
            throw new ConflictException(e.getMessage());        
        }
        
        
        if (updatedTemplate==null){
            logger.info("updateTemplate: Couldn't update template, "+templateParam.getTemplate().getTemplateId()+ ",please check that it exists in database");
            throw new NotFoundException("Couldn't update template, "+templateParam.getTemplate().getTemplateId()+ ",please check that it exists in database");
        }

        logger.debug("EndOf updateTemplate");
        return updatedTemplate;
    }
    
    
    

    
    private TemplateHelperE getTemplateHelper() {
        return helper;
    }
}
