/**
 * Copyright 2015 SeaClouds
 * Contact: SeaClouds
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
import org.springframework.web.bind.annotation.RequestBody;

import eu.atos.sla.parser.data.Provider;
import eu.atos.sla.service.rest.exception.ConflictException;
import eu.atos.sla.service.rest.exception.InternalException;
import eu.atos.sla.service.rest.exception.NotFoundException;
import eu.atos.sla.service.rest.helpers.ProviderHelperE;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;

/**
 * Rest Service that exposes all the stored information of the SLA core
 * 
 * @author Elena Garrido
 */
@Path("/providers")
@Component
@Scope("request")
public class ProviderRestEntity  extends AbstractSLARest {

	@Autowired
	private ProviderHelperE helper;

	private static Logger logger = LoggerFactory.getLogger(ProviderRestEntity.class);

	public ProviderRestEntity() {
	}



	/**
	 * Get the list of available providers
	 * 
	 * <pre>
	 *  GET /providers 
	 *  
	 *  Request:
	 *  	GET /providers HTTP/1.1
	 *  
	 *  Response:
	 *      Type will depend on the Accept header from the call
	 *  
	 * With Accept: application/xml
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	 * <providers>
	 *   <provider>
	 *     <uuid>provider01</uuid>
	 *     <name>provider01name</name>
	 *   </provider>
	 *   <provider>
	 *     <uuid>f4c993580-03fe-41eb-8a21-a56709f9370f</uuid>
	 *     <name>provider-a</name>
	 *   </provider>
	 *   <provider>
	 *     <uuid>provider02</uuid>
	 *     <name>provider02name</name>
	 *   </provider>
	 * </providers>
	 * }
	 * 
 	 * With Accept: application/json
	 * [{"uuid":"f4c993580-03fe-41eb-8a21-a56709f9370f", "name":"provider-prueba"},
	 *  {"uuid":"f4c993580-03fe-41eb-8a21-a56709f9370e","name":"provider-b"},
	 *  {"uuid":"trento","name":"Trento"},
	 *  {"uuid":"Lannion","name":"Lannion"}]
	 * </pre>
	 * 
	 * Example: <li>curl http://localhost:8080/sla-service/providers</li>
	 * 
	 * @return XML information with the different details of the providers
	 * 
	 */

	@GET
	public List<Provider> getProviders() {
		logger.debug("StartOf getProviders - REQUEST for /providers");

		ProviderHelperE providerRestHelper =  getProviderHelper();
		List<Provider> providerList = null;
		try {
			providerList = providerRestHelper.getProviders();
		} catch (HelperException e) {
			logger.info("getProviders HelperException:"+e.getMessage());
		}
		logger.debug("EndOf getProviders");
		return providerList;
	}

	/**
	 * Gets the information of an specific provider If the provider it is not in
	 * the database, it returns 404 with empty payload
	 * 
	 * <pre>
	 *  GET /providers/{uuid} 
	 *  
	 *  Request:
	 *  	GET /providers HTTP/1.1
	 *  
	 *  Response:
	 *    Type will depend on the Accept header from the call
	 *    
	 * With Accept: application/xml
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <provider>
	 *    <uuid>fc923960-03fe-41eb-8a21-a56709f9370f</uuid>
	 *    <name>provider-prueba</name>
	 * </provider>
	 * }
	 * 
	 * With Accept: application/json
	 * {"uuid":"fc923960-03fe-41eb-8a21-a56709f9370f","name":"provider-prueba"}
	 * </pre>
	 * 
	 * Example: <li>curl -H "Accept: application/json"
	 * http://localhost:8080/sla-service/providers/fc923960-03f
	 * e-41eb-8a21-a56709f9370f</li>
	 * 
	 * @param uuid of the provider
	 *            
	 * @return XML or JSON information with the different details of the provider
	 * @throws NotFoundException when no provider with uuid exists in the database
	 */

	
	
	@GET
	@Path("{uuid}")
	public Provider getProviderByUuid(@PathParam("uuid") String providerUUID) throws NotFoundException{
		logger.debug("StartOf getProviderByUuid - REQUEST for /providers/{}", providerUUID);

		ProviderHelperE providerRestHelper = getProviderHelper();
		Provider provider = providerRestHelper.getProviderByUUID(providerUUID);
		if (provider==null){
			logger.info("There is no provider with uuid " + providerUUID + " in the SLA Repository Database");
			throw new NotFoundException("There is no provider with uuid " + providerUUID + " in the SLA Repository Database");		
		}
		return provider;
	}

	
	/**
	 * Creates a new provider
	 * 
	 * 
	 * <pre>
	 *   POST /providers

	 *   Request:
	 *   	POST /providers HTTP/1.1
	 *   	Accept: application/xml
	 *   
	 *   Response:
	 *  
	 *   {@code
	 *  <provider>
	 *    <uuid>fc993580-03fe-41eb-8a21-a56709f9370f</uuid>
	 *    <name>provider-3</name>
	 * 	</provider>
	 *     
	 *   }
	 * 
	 * </pre>
	 * 
	 * Example: <li>curl -H "Content-type: application/xml" -X POST -d
	 * @provider.xml localhost:8080/sla-service/providers/</li>
	 * 
	 * @return XML information with the different details of the provider
	 * @throws ConflictException when a provider with uuid already exists in the database
	 * @throws InternalException when it could not be stored due to any other reason 
	 * 
	 */
	@POST
	public Response createProvider(@Context UriInfo uriInfo,  @RequestBody Provider provider)
			throws ConflictException, InternalException {
		
		logger.debug("StartOf createProvider - REQUEST Insert /providers");
		ProviderHelperE providerRestHelper = getProviderHelper();
		String id = null, location = null;
		try {
			id = providerRestHelper.createProvider(provider);
			location = buildResourceLocation(uriInfo.getAbsolutePath().toString() ,id);
		} catch (DBExistsHelperException e) {
			logger.info("createProvider ConflictException:"+ e.getMessage());
			throw new ConflictException(e.getMessage());
		} catch (InternalHelperException e) {
			logger.info("createProvider InternalException", e);
			throw new InternalException(e.getMessage());
		} catch (Throwable t){
			logger.info("createProvider throwable", t);			
		}
		logger.debug("EndOf createProvider");
		return buildResponsePOST(
				HttpStatus.CREATED,
				createMessage(HttpStatus.CREATED, id,
						"The provider has been stored successfully in the SLA Repository Database. It has location "+location),
				location);
	}

	
	@DELETE
	@Path("{uuid}")
	public Response deleteProvider(@PathParam("uuid") String providerUUID) throws ConflictException{
		logger.debug("DELETE /providers/{}", providerUUID);
	
		ProviderHelperE providerRestHelper = getProviderHelper();
		try{
			boolean deleted = providerRestHelper.deleteByProviderUUID(providerUUID);
			if (deleted)
				return buildResponse(HttpStatus.OK, 
						"The provider with uuid " + providerUUID + " was deleted successfully");
			else{
				logger.info("deleteProvider NotFoundException: There is no provider with uuid "
								+ providerUUID + " in the SLA Repository Database");
				return buildResponse(HttpStatus.NOT_FOUND, 
						printError(HttpStatus.NOT_FOUND, "There is no provider with uuid "
								+ providerUUID + " in the SLA Repository Database"));
			}
		}catch(DBExistsHelperException e){
			logger.info("deleteProvider ConflictException:"+ e.getMessage());
			throw new ConflictException(e.getMessage());
		}
	}

	
	private ProviderHelperE getProviderHelper() {
		return helper;
	}

}
