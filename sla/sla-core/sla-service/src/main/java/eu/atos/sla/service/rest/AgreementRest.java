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

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

import eu.atos.sla.service.rest.helpers.AgreementHelper;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
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
 * @author Pedro Rey
 */
@Deprecated
@Path("/agreementso")
@Component
@Scope("request")
public class AgreementRest extends AbstractSLARest{
	private static Logger logger = LoggerFactory.getLogger(AgreementRest.class);

	@Autowired
	private AgreementHelper helper;

	
	public AgreementRest() {
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
	 * 
	 * @throws JAXBException
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getAgreements(
			@QueryParam("consumerId") String consumerId,
			@QueryParam("providerId") String providerId,
			@QueryParam("active") BooleanParam active)  {
		logger.debug("StartOf getAgreements - REQUEST for /agreements");
		try{
			AgreementHelper agreementRestService = getAgreementHelper();
			String serializedAgreement =  agreementRestService.getAgreements(consumerId, providerId, BooleanParam.getValue(active));
			
			Response result = buildResponse(200, serializedAgreement);
			logger.debug("EndOf getAgreements");
			return result;
			
		} catch (HelperException e) {
			logger.info("getAgreements exception:"+e.getMessage());
			return buildResponse(e);
		}

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
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getAgreementById(@PathParam("id") String agreement_id) {
		logger.debug("StartOf getAgreementById REQUEST for /agreements/" + agreement_id);

		Response result;
		
		try{
			AgreementHelper agreementRestService = getAgreementHelper();
			String serializedAgreement =  agreementRestService.getAgreementByID(agreement_id);
			if (serializedAgreement!=null){
				result = buildResponse(200, serializedAgreement);
			}else{
				result = buildResponse(404, printError(404, "There are no getAgreements with agreementId " + agreement_id 
						+ " in the SLA Repository Database"));		
			}			
		} catch (HelperException e) {
			logger.info("getAgreementById exception:"+e.getMessage());
			result = buildResponse(e);
		}

		logger.debug("EndOf getAgreementById");
		return result;
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
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response createAgreement(@Context HttpHeaders hh,@Context UriInfo uriInfo,  String payload){
		logger.debug("StartOf createAgreement - Insert /agreements");
		String location = null;
		try{
			AgreementHelper agreementRestService = getAgreementHelper();
			location = agreementRestService.createAgreement(hh,uriInfo.getAbsolutePath().toString(), payload);
		} catch (HelperException e) {
			logger.info("createAgreement exception", e);
			return buildResponse(e);
		}
		Response result = buildResponsePOST(HttpStatus.CREATED, 
				printMessage(
					HttpStatus.CREATED,
					"The agreement has been stored successfully in the SLA Repository Database with location:"+location),
				location);
		logger.debug("EndOf createAgreement");
		return result;
	}

	@GET
	@Path("active")
	@Produces(MediaType.APPLICATION_XML)
	public Response getActiveAgreements()  {
		logger.debug("StartOf getActiveAgreements - Get active agreements");
		long actualDate = new Date().getTime();
		
		Response result;
		try{
			AgreementHelper agreementRestService = getAgreementHelper();
			String serializedAgreement =  agreementRestService.getActiveAgreements(actualDate);
			result = buildResponse(200, serializedAgreement);
		} catch (HelperException e) {
			logger.info("getActiveAgreements exception:"+e.getMessage());
			return buildResponse(e);
		}
		logger.debug("EndOf getActiveAgreements");
		return result;
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
	 *  	DELETE /agreements HTTP/1.1
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
	@Produces(MediaType.APPLICATION_XML)
	public Response deleteAgreement(@PathParam("agreementId") String agreementId){
		logger.debug("DELETE /agreements/" + agreementId);

		AgreementHelper agreementRestService = getAgreementHelper();
		boolean deleted = agreementRestService.deleteByAgreementId(agreementId);
		if (deleted)
			return buildResponse(
					204,
					printMessage(204, "The agreement with id:" + agreementId
							+ " was deleted successfully"));
		else
			return buildResponse(
					404,
					printError(404, "There is no agreement with id "
							+ agreementId + " in the SLA Repository Database"));
		
	}

	private AgreementHelper getAgreementHelper() {
		return helper;
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
	 *   Content-type: application/xml
	 *  
	 * {@code
	 *   <GuaranteeStatus agreementId="$agreementId" value="FULFILLED|VIOLATED|NON_DETERMINED">
	 *     <GuaranteeTermStatus name="$gt_name1" value="FULFILLED|VIOLATED|NON_DETERMINED"/>
	 *    ...
	 *     <GuaranteeTermStatus name="$gt_nameN" value="FULFILLED|VIOLATED|NON_DETERMINED"/>
	 *   </GuaranteeStatus>
	 * }
	 *  
	 * 
	 * </pre>
	 * 
	 * Example: <li>curl -H "Content-type: application/xml" http://localhost:8080/sla-service/agreements/{agreementId}/guaranteestatus</li>
	 * 
	 * 
	 * @param id
	 *            of the agreement
	 * @return XML information with Guarantee Status
	 */
	@GET
	@Path("{id}/guaranteestatus")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response getStatusAgreementXML(@PathParam("id") String agreement_id) {
		logger.debug("StartOf getStatusAgreementXML - REQUEST for /agreements/" + agreement_id
				+ "/guaranteestatus");
		Response result;
		try{
			AgreementHelper agreementRestService = getAgreementHelper();
			String serializedAgreement =  agreementRestService.getAgreementStatus(agreement_id,
					MediaType.APPLICATION_XML);
			if (serializedAgreement!=null){
				result = buildResponse(200, serializedAgreement);
			}else{
				result = buildResponse(404, printError(404, "No agreement with "+agreement_id));
			}			
		} catch (HelperException e) {
			logger.info("getStatusAgreementXML exception:"+e.getMessage());
			return buildResponse(e);
		}
		logger.debug("EndOf getStatusAgreementXML");
		return result;
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
	 *   Content-type: application/xml
	 *  
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatusAgreementJson(@PathParam("id") String agreement_id) {
		logger.debug("StartOf getStatusAgreementJson - REQUEST for /agreements/" + agreement_id
				+ "/guaranteestatus");
		
		Response result;
		try{
			AgreementHelper agreementRestService = getAgreementHelper();
			String serializedAgreement =  agreementRestService.getAgreementStatus(agreement_id,
					MediaType.APPLICATION_JSON);
			if (serializedAgreement!=null){
				result = buildResponse(200, serializedAgreement);
			}else{
				result = buildResponse(404, printError(404, "No agreement with "+agreement_id));
			}			
		} catch (HelperException e) {
			logger.info("getStatusAgreementJson exception:"+e.getMessage());
			return buildResponse(e);
		}
		logger.debug("EndOf getStatusAgreementJson");
		return result;
	}

	
}
