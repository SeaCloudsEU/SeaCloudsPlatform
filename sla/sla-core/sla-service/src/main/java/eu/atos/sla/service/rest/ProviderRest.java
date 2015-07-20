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

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import eu.atos.sla.service.rest.helpers.ProviderHelper;
import eu.atos.sla.service.rest.helpers.exception.HelperException;

/**
 * Rest Service that exposes all the stored information of the SLA core
 * 
 */
@Deprecated
@Path("/providerso")
@Component
@Scope("request")
public class ProviderRest  extends AbstractSLARest {

    @Autowired
    private ProviderHelper helper;


    private static Logger logger = LoggerFactory.getLogger(ProviderRest.class);

    public ProviderRest() {
    }



    /**
     * Get the list of available providers
     * 
     * <pre>
     *  GET /providers 
     *  
     *  Request:
     *      GET /providers HTTP/1.1
     *      Accept: application/xml
     *  
     *  Response:
     *  
     * {@code
     * <?xml version="1.0" encoding="UTF-8"?>
     * <collection href="/providers">
     * <items offset="0" total="1">
     * 
     * <provider>
     *    <uuid>fc923960-03fe-41eb-8a21-a56709f9370f</uuid>
     *    <name>provider-prueba</name>
     * </provider>
     * </items>
     * </collection>
     * }
     * 
     * </pre>
     * 
     * Example: <li>curl http://localhost:8080/sla-service/providers</li>
     * 
     * @return XML information with the different details of the providers
     * 
     * @throws JAXBException
     */

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getProviders() {
        logger.debug("StartOf getProviders - REQUEST for /providers");

        ProviderHelper providerRestService =  getProviderHelper();
        String serializedProviders = null;
        try {
            serializedProviders = providerRestService.getProviders();
        } catch (HelperException e) {
            logger.info("getTemplates exception:"+e.getMessage());
            return buildResponse(e);
        }
        logger.debug("EndOf getTemplates");
        return buildResponse(200, serializedProviders);
        
    }

    /**
     * Gets the information of an specific provider If the provider it is not in
     * the database, it returns 404 with empty payload
     * 
     * <pre>
     *  GET /providers/{uuid} 
     *  
     *  Request:
     *      GET /providers HTTP/1.1
     *  
     *  Response:
     *  
     * {@code
     * <?xml version="1.0" encoding="UTF-8"?>
     * <provider>
     *    <uuid>fc923960-03fe-41eb-8a21-a56709f9370f</uuid>
     *    <name>provider-prueba</name>
     * </provider>
     * }
     * 
     * </pre>
     * 
     * Example: <li>curl
     * http://localhost:8080/sla-service/providers/fc923960-03f
     * e-41eb-8a21-a56709f9370f</li>
     * 
     * @param uuid
     *            of the provider
     * @return XML information with the different details of the provider
     */
    @GET
    @Path("{uuid}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getProviderByUuid(@PathParam("uuid") String provider_uuid)
            throws IOException, JAXBException {
        logger.debug("StartOf getProviderByUuid - REQUEST for /providers/" + provider_uuid);

        try {
            ProviderHelper providerRestService = getProviderHelper();
            String serializedProvider = providerRestService.getProviderByUUID(provider_uuid);
            
            if (serializedProvider!=null){
                logger.debug("EndOf getProviderByUuid");
                return buildResponse(200, serializedProvider);
            }else{
                logger.debug("EndOf getProviderByUuid");
                return buildResponse(404, printError(404, "There is no provider with uuid " + provider_uuid
                        + " in the SLA Repository Database"));        
            }
        } catch (HelperException e) {
            logger.info("getProviderByUuid exception:"+e.getMessage());
            return buildResponse(e);
        }
            
    }

    /**
     * Creates a new provider
     * 
     * 
     * <pre>
     *   POST /providers

     *   Request:
     *       POST /providers HTTP/1.1
     *       Accept: application/xml
     *   
     *   Response:
     *  
     *   {@code
     *  <provider>
     *    <uuid>fc993580-03fe-41eb-8a21-a56709f9370f</uuid>
     *    <name>provider-3</name>
     *     </provider>
     *     
     *   }
     * 
     * </pre>
     * 
     * Example: <li>curl -H "Content-type: application/xml" -X POST -d
     * @provider.xml localhost:8080/sla-service/providers/</li>
     * 
     * @return XML information with the different details of the provider
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createProvider(@Context HttpHeaders hh,@Context UriInfo uriInfo, String payload)
            throws IOException, JAXBException {
        
        logger.debug("StartOf createProvider - REQUEST Insert /providers");
        
        ProviderHelper providerRestService = getProviderHelper();
        String location;
        try {
            location = providerRestService.createProvider(
                    hh, uriInfo.getAbsolutePath().toString(), payload);
        } catch (HelperException e) {
            logger.info("createProvider exception", e);
            return buildResponse(e);
        }
        logger.debug("EndOf createProvider");
        return buildResponsePOST(
                HttpStatus.CREATED,
                printMessage(HttpStatus.CREATED,
                        "The createProvider has been stored successfully in the SLA Repository Database"),
                location);
    }

    
    
    private ProviderHelper getProviderHelper() {
        return helper;
    }

}
