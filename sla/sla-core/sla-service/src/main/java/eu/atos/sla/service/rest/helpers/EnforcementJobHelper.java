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
package eu.atos.sla.service.rest.helpers;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.parser.data.EnforcementJob;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.rest.helpers.exception.HelperException.Code;
import eu.atos.sla.util.IModelConverter;
import eu.atos.sla.util.ModelConversionException;

/**
 * 
 * @author Pedro Rey Estrada
 */
@Deprecated
@Service
@Transactional
public class EnforcementJobHelper{
	private static Logger logger = LoggerFactory.getLogger(EnforcementJobHelper.class);

	@Autowired
	private IEnforcementJobDAO enforcementJobDAO;
	
	@Autowired
	private IEnforcementService enforcementService;
	
	@Autowired
	IModelConverter modelConverter;

	public EnforcementJobHelper() {
	}



	private boolean doesEnforcementExistInRepository(String agreementId) {
		if (this.enforcementJobDAO.getByAgreementId(agreementId) != null)
			return true;
		else
			return false;
	}

	private  String printEnforcementJobToXML(IEnforcementJob enforcementJob)
			throws JAXBException {

		EnforcementJob enforcementJobXML = modelConverter.getEnforcementJobXML(enforcementJob);

		JAXBContext jaxbContext = JAXBContext.newInstance(EnforcementJob.class);
		Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(enforcementJobXML, out);

		return out.toString();
	}

	private  String printEnforcementJobsToXML(List<IEnforcementJob> enforcementJobs)
			throws JAXBException {
		logger.debug("printEnforcementJobsToXML");

		StringBuilder xmlResponse = new StringBuilder();
		xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlResponse.append("<collection href=\"/enforcementJobs\">\n");
		xmlResponse.append("<items offset=\"0\" total=\""
				+ enforcementJobs.size() + "\">\n");

		for (IEnforcementJob enforcementJob : enforcementJobs) {
			xmlResponse.append(printEnforcementJobToXML(enforcementJob));
		}

		xmlResponse.append("</items>\n");
		xmlResponse.append("</collection>\n");

		return xmlResponse.toString();
	}

	public String getEnforcements() throws HelperException{
		logger.debug("StartOf getEnforcements");
		try{
			List<IEnforcementJob> enforcementJobs = new ArrayList<IEnforcementJob>();
	
			enforcementJobs = this.enforcementJobDAO.getAll();
			
			if (enforcementJobs.size() != 0){
				String str = printEnforcementJobsToXML(enforcementJobs);
				logger.debug("EndOf getEnforcements");
				return str;
			}
			else{
				logger.debug("EndOf getEnforcements");
				throw new HelperException(Code.DB_DELETED, "There are no enformcements in the SLA Repository Database");
			}
		} catch (JAXBException e) {
			logger.error("Error in getEnforcementJobByUUID " , e);
			throw new HelperException(Code.PARSER, "Error when creating enforcementJob parsing file:" + e.getMessage() );
		}
	}

	public String getEnforcementJobByUUID(String uuid)  throws HelperException{
		logger.debug("StartOf getEnforcementJobByUUID uuid:"+uuid);
		try{
			IEnforcementJob enforcementJob = null;
	
			enforcementJob = this.enforcementJobDAO.getByAgreementId(uuid);
	
			String str = null;
			if (enforcementJob != null)	str = printEnforcementJobToXML(enforcementJob);
			logger.debug("EndOf getEnforcementJobByUUID");
			return str;
		} catch (JAXBException e) {
			logger.error("Error in getEnforcementJobByUUID " , e);
			throw new HelperException(Code.PARSER, "Error when creating enforcementJob parsing file:" + e.getMessage() );
		}
	}


	public boolean startEnforcementJob(String agreementId){
		logger.debug("startEnforcementJob agreementId:"+agreementId);
		return enforcementService.startEnforcement(agreementId);
	}

	public boolean stopEnforcementJob(String agreementUuid){
		logger.debug("stopEnforcementJob agreementUuid:"+agreementUuid);
		return enforcementService.stopEnforcement(agreementUuid);
	}

	public String  createEnforcementJob(HttpHeaders hh, String collectionUri, String payload)
			throws HelperException {
		logger.debug("StartOf createEnforcementJob payload:"+payload);
		try {
			
			JAXBContext jaxbContext = JAXBContext.newInstance(EnforcementJob.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			EnforcementJob enforcementJobXML = (EnforcementJob) jaxbUnmarshaller
					.unmarshal(new StringReader(payload));
	
			IEnforcementJob enforcementJob = null;
			IEnforcementJob stored = null;
			String location = null;
	
			if (enforcementJobXML != null) {
	
				if (!doesEnforcementExistInRepository(enforcementJobXML.getAgreementId())) {
					// the enforcement doesn't eist
					enforcementJob = modelConverter.getEnforcementJobFromEnforcementJobXML(enforcementJobXML);
					stored = this.enforcementService.createEnforcementJob(enforcementJob);
				} else {
					throw new HelperException(Code.DB_EXIST, "Enforcement with id:"
							+ enforcementJobXML.getAgreementId()
							+ " already exists in the SLA Repository Database");
				}
			}
	
			if (stored != null) {
				location = Utils.buildResourceLocation(collectionUri, stored.getAgreement().getAgreementId());
				logger.debug("EndOf createEnforcementJob");
				return location;
			} else {
				logger.debug("EndOf createEnforcementJob");
				throw new HelperException(Code.INTERNAL, "Error when creating enforcementJob the SLA Repository Database");
			}
		} catch (JAXBException e) {
			logger.error("Error in createEnforcementJob " , e);
			throw new HelperException(Code.PARSER, "Error when creating enforcementJob parsing file:" + e.getMessage() );
		} catch (ModelConversionException e) {
			logger.error("Error in createEnforcementJob " , e);
			throw new HelperException(Code.INTERNAL, e.getMessage() );
		}

	}
	


}
