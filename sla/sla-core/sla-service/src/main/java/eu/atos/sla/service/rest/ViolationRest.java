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
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.atos.sla.service.rest.helpers.ViolationHelper;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.types.DateParam;

/**
 * Rest Service that exposes all the stored information of the SLA core.
 * 
 * A violation is serialized as:
 *
 *<pre>
 * {@code
 * <violation>
 *     <uuid>ce0e148f-dfac-4492-bb26-ad2e9a6965ec</uuid>
 *     <contract_uuid>agreement04</contract_uuid>
 *     <service_scope></service_scope>
 *     <metric_name>Performance</metric_name>
 *     <datetime>2014-01-14T11:28:22Z</datetime>
 *     <actual_value>0.09555700123360344</actual_value>
 * </violation>
 * }
 * or 
 *   {
 *   "uuid": "e0e148f-dfac-4492-bb26-ad2e9a6965ec",
 *   "contract_uuid": "agreement04",
 *   "service_scope" : "",
 *   "metric_name" : "Performance",
 *   "datetime" : "2014-01-14T11:28:22Z"
 *   "actual_value": "0.09555700123360344"
 *   } 
 * 
 * </pre>
 * @author Pedro Rey
 */
@Deprecated
@Path("/violationso")
@Component
@Scope("request")
public class ViolationRest extends AbstractSLARest {

	@Context
	HttpHeaders headers;

	@Autowired
	private ViolationHelper helper;

	public static Logger logger = LoggerFactory.getLogger(ViolationRest.class);

	public ViolationRest() {
	}
	
	
	private ViolationHelper getViolationHelper() {
		return helper;
	}

	
	/**
	 * Returns the information of an specific violation given an uuid If the
	 * violation it is not in the database, it returns 404 with empty payload
	 * 
	 * <pre>
	 * GET /violations/{violation_uuid}
	 *    
	 * Request:
	 *   GET /violation HTTP/1.1
	 *   Accept: application/xml
	 *    
	 * Response:
	 *   HTTP/1.1 200 OK
	 *   Content-type: application/xml
	 * 
	 *   {@code
	 *   <?xml version="1.0" encoding="UTF-8"?>
	 *   <violation>...</violation>
	 *   }
	 * 
	 * </pre>
	 * 
	 * Example: 
	 * <li>curl
	 * http://localhost:8080/sla-service/violations/?agrementId=agreement04</li>
	 * 
	 * @return violations according to parameters in the query string.
	 */
	@GET
	@Path("{uuid}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getViolationByUuid(@PathParam("uuid") UUID violationUuid) {
		logger.debug("StartOf getViolationByUuid - REQUEST for /violations/" + violationUuid);
		String serializedViolations = null;
		ViolationHelper violationRestHelper = getViolationHelper();
		
		try{
			serializedViolations = violationRestHelper.getViolationByUUID(violationUuid);
		} catch (HelperException e) {
			logger.info("getViolationByUuid exception:"+e.getMessage());
			return buildResponse(e);
		}
		logger.debug("EndOf getViolationByUuid");
		return buildResponse(200, serializedViolations);
		
	}

	/**
	 * Search violations given an agreementId as query string.
	 * 
	 * If no parameters specified, return all violations.
	 * 
	 * <pre>
	 * GET /violations{?agreementId,guaranteeTerm,providerId,begin,end}
	 *    
	 * Request:
	 *   GET /violation HTTP/1.1
	 *   Accept: application/xml
	 *    
	 * Response:
	 *   HTTP/1.1 200 OK
	 *   Content-type: application/xml
	 *    
	 *   {@code
	 *   <?xml version="1.0" encoding="UTF-8"?>
	 *   <collection href="/violations">
	 *   <items offset="0" total="1">
	 *   <violation>...</violation>
	 *   <violation>...</violation>
	 *   ...
	 *   </items>
	 *   </collection>
	 *   }
	 * 
	 * </pre>
	 * 
	 * Examples: 
	 * <li>curl -H"Accept: application/xml"
	 * http://localhost:8080/sla-service/violations/?agrementId=agreement04&guaranteeTerm=gt_uptime</li>
	 * <li>curl "Content-type: application/json"
	 * http://localhost:8080/sla-service/violations/?providerId=agreement04&begin=2014-03-18T15:23:00</li>
	 * 
	 * @param agreementId
	 * @param guaranteeTerm
	 * @param providerUuid
	 * @param begin
	 * @param end
	 * @return violations
	 * 
	 * @return violations according to parameters in the query string.
	 */
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response getViolationsXML(
			@QueryParam("agreementId") String agreementId,
			@QueryParam("guaranteeTerm") String guaranteeTerm,
			@QueryParam("providerId") String providerUuid,
			@QueryParam("begin") DateParam begin, @QueryParam("end") DateParam end) {

		logger.debug("StartOf  getViolationsXML REQUEST for /violations/?agreementId=\"\"&guaranteeTerm=\"\"&providerUuid=\"\"&begin=\"\"&end");
		logger.debug("Accept: application/xml");

		String serializedViolations = null;
		Date dBegin = (begin == null)? null : begin.getDate();
		Date dEnd = (end == null)? null : end.getDate();
		
		ViolationHelper violationRestHelper = getViolationHelper();
		
		try{
			serializedViolations = violationRestHelper.getViolations(
					agreementId, guaranteeTerm, providerUuid, dBegin, dEnd, MediaType.APPLICATION_XML );
		} catch (HelperException e) {
			logger.info("getViolationsXML exception:"+e.getMessage());
			return buildResponse(e);
		}
		logger.debug("EndOf getViolationsXML");
		return buildResponse(200, serializedViolations);
		

	}
	
	/**
	 * Search violations given an agreementId as query string.
	 * 
	 * If no parameters specified, return all violations.
     * 
	 * <pre>
	 * GET /violations 
	 *    
	 * Request:
	 *   GET /violation HTTP/1.1
	 *   Accept: application/json
	 *    
	 * Response:
	 *   HTTP/1.1 200 OK
	 *   Content-type: application/json
	 *   
	 *   {@code
	 *   [
	 *    {...}, 
	 *    {...} ...
	 *    ...
	 *   ]
	 *   }
	 * 
	 * </pre>
	 * 
	 * Example: 
	 * <li>curl "Content-type: application/json"
	 * http://localhost:8080/sla-service/violations/?agrementId=agreement04</li>
	 *
	 * @return violations according to parameters in the query string.
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getViolationsJson(
			@QueryParam("agreementId") String agreementId,
			@QueryParam("guaranteeTerm") String guaranteeTerm,
			@QueryParam("providerUuid") String providerUuid,
			@QueryParam("begin") DateParam begin, @QueryParam("end") DateParam end){

		logger.debug("StartOf  getViolationsJson REQUEST for /violations/?agreementId=\"\"&guaranteeTerm=\"\"&providerUuid=\"\"&begin=\"\"&end");
		logger.debug("Accept: application/json");

		String serializedViolations = null;
		Date dBegin = (begin == null)? null : begin.getDate();
		Date dEnd = (end == null)? null : end.getDate();
		
		ViolationHelper violationRestHelper = getViolationHelper();
		
		try{
			serializedViolations = violationRestHelper.getViolations(
					agreementId, guaranteeTerm, providerUuid, dBegin, dEnd, MediaType.APPLICATION_JSON );
		} catch (HelperException e) {
			logger.info("getViolationsJson exception:"+e.getMessage());
			return buildResponse(e);
		}
		logger.debug("EndOf getViolationsJson");
		return buildResponse(200, serializedViolations);
		
	}
}
