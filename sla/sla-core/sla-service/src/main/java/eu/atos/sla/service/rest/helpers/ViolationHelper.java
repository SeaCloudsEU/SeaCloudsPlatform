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
package eu.atos.sla.service.rest.helpers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.service.rest.helpers.exception.HelperException;
import eu.atos.sla.service.rest.helpers.exception.HelperException.Code;
import eu.atos.sla.util.IModelConverter;

/**
 * 
 */
@Deprecated
@Service
@Transactional
public class ViolationHelper {
    private static Logger logger = LoggerFactory.getLogger(ViolationHelper.class);

    @Autowired
    public IViolationDAO violationDAO;

    @Autowired
    private IAgreementDAO agreementDAO;

    @Autowired
    private IProviderDAO providerDAO;

    @Autowired
    private ITemplateDAO templateDAO;
    
    
    @Autowired
    private IModelConverter modelConverter;

    public ViolationHelper() {

    }


    private String printViolationToXML(IViolation violation) throws JAXBException {

        eu.atos.sla.parser.data.Violation violationXML = modelConverter.getViolationXML(violation);

        JAXBContext jaxbContext = JAXBContext
                .newInstance(eu.atos.sla.parser.data.Violation.class);
        Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(violationXML, out);

        return out.toString();
    }

    private String printViolationsToXML(List<IViolation> violations)
            throws JAXBException {

        logger.debug("Getting the list of violations from the database");
        logger.debug("Found " + violations.size() + " in the database");

        StringBuilder xmlResponse = new StringBuilder();
        xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlResponse.append("<collection href=\"/violations\">\n");
        xmlResponse.append("<items offset=\"0\" total=\"" + violations.size()
                + "\">\n");

        for (IViolation violation : violations) {
        xmlResponse.append(printViolationToXML(violation));
        
        }

        xmlResponse.append("</items>\n");
        xmlResponse.append("</collection>\n");

        return xmlResponse.toString();
    }

    private String getViolations(String resultType) throws JAXBException {
        
        String serialized = null;
        List<IViolation> violations = this.violationDAO.getAll();
        serialized = serialize(violations, resultType);

        return serialized;
    }

    public String getViolationByUUID(UUID uuid) throws HelperException {
        logger.debug("StartOf getViolationByUUID uuid:"+uuid);
        try{
            IViolation violation = violationDAO.getViolationByUUID(uuid.toString());
            
            String serialized = null;
            if (violation != null) 
                serialized = serialize(violation, MediaType.APPLICATION_XML);
            logger.debug("EndOf getViolationByUUID");
            return serialized;
        } catch (JAXBException e) {
            logger.error("Error in getViolationByUUID " , e);
            throw new HelperException(Code.PARSER,  "Error parsing violations"+ e.toString() );
        }
    }

    private String getViolationsByAgreementId(String agreementId,
            String termName, Date begin, Date end, String resultType)
            throws HelperException{
        logger.debug("StartOf getViolationsByAgreementId agreementId:"+agreementId +" termName "+termName+" begin "+begin+" end "+end+" resultType "+resultType);;
        try{
            List<IViolation> violations = new ArrayList<IViolation>();
            String serialized = null;
            violations = this.violationDAO.getByAgreementIdInARangeOfDates(agreementId, termName, begin, end);
            serialized = serialize(violations, resultType);
            logger.debug("EndOf getViolationsByAgreementId");
            return serialized;
        } catch (JAXBException e) {
            logger.error("Error in getViolationsByAgreementId " , e);
            throw new HelperException(Code.PARSER,  "Error parsing violations"+ e.toString() );
    }
    }

    private String getViolationsByProvider(String providerUuid, Date begin,
            Date end, String resultType) throws HelperException, JAXBException {

        List<IViolation> violations = new ArrayList<IViolation>();
        
        String violationsResult = null;

        if (providerUuid != null && (begin != null || end != null)) {
            violations = this.violationDAO.getByProviderInaRangeOfDates(
                    providerUuid, begin, end);
        } else if (providerUuid != null && begin == null && end == null) {
            violations = this.violationDAO.getByProvider(providerUuid);
        }

        violationsResult = serialize(violations, resultType);
        
        if (violations != null) {
            return violationsResult;
        } else if (begin != null && end != null) {
            throw new HelperException(Code.DB_MISSING, "There is no violation with providerId: "
                    + providerUuid + " in that range of Dates: begin: " + begin + " end:" + end
                    + " in the SLA Repository Database");
        } else {
            throw new HelperException(Code.DB_MISSING, "There is no violation with providerId: "
                    + providerUuid + " in the SLA Repository Database");
        }
    }

    /* TODO egarrido, is not beeing used
    private Response createViolation(HttpHeaders hh, String payload)
            throws JAXBException, FileNotFoundException {

        JAXBContext context = JAXBContext.newInstance(Violation.class);
        Unmarshaller unMarshaller = context.createUnmarshaller();
        Violation violation = (Violation) unMarshaller
                .unmarshal(new FileInputStream(payload));

        IViolation violationStored = new Violation();
        try {
            violationStored = this.violationDAO.save(violation);
        } catch (Exception e) {
            return buildResponse(
                    500,
                    printError(500,
                            "Error when creating violation the SLA Repository Database:"
                                    + e.getMessage()));
        }
        String location = null;

        if (violationStored != null) {

            String serialized = serialize(violation, MediaType.APPLICATION_XML);
            return buildResponsePOST(201, serialized, location);

        } else
            return buildResponse(
                    500,
                    printError(500,
                            "Error when creating violation the SLA Repository Database"));

    }*/

    
    public String getViolations(String agreementId, String guaranteeTerm,
            String providerUuid, Date begin, Date end, String resultType)
            throws HelperException {
        logger.debug("StartOf getViolationsByAgreementId agreementId:"+agreementId +" guaranteeTerm "+guaranteeTerm+ " providerUuid "+ providerUuid + " begin "+begin+" end "+end+" resultType "+resultType);
        
        String result = null;
        try{
            if (agreementId == null && guaranteeTerm == null
                    && providerUuid == null && begin == null && end == null) {
                /*
                 * All violations
                 */
                logger.debug("REQUEST for /violations/");
                result = getViolations(resultType);
                
            } else if (agreementId != null && providerUuid == null) {
                /*
                 * Violations by agreement
                 */
                logger.debug("REQUEST for /violations/?agreementId=\"\",&term=\"\",&begin=\"\",&term=\"\"");
                result = getViolationsByAgreementId(agreementId, guaranteeTerm,
                        begin, end, resultType);
    
            } else if (providerUuid != null) {
                /*
                 * Violations by provider
                 */
                logger.debug("REQUEST for /violations/?providerUuid=\"\"&begin=\"\",&end=\"\"");
                result = getViolationsByProvider(providerUuid, begin, end,
                        resultType);
    
            } else {
    
                // DEFAULT CASE: throw exception
    
                throw new HelperException(Code.PARSER, "Not valid query");
            }
        } catch (JAXBException e) {
            logger.info("getViolationsByAgreementId exception",e);
            throw new HelperException(Code.PARSER, "Could not parse result");
            
        }
        return result;
    }
    
    private String serialize(List<IViolation> violations, String resultType)
            throws JAXBException {
        String serialized;
        
        if (resultType == MediaType.APPLICATION_JSON) {
            Utils<List<IViolation>> utils =  new Utils<List<IViolation>>();
            serialized = utils.parseToJson(violations);
        } else {
            serialized = printViolationsToXML(violations);
        }
        return serialized;
    }

    private String serialize(IViolation violation, String resultType) throws JAXBException{
        String serialized;
        if (resultType == MediaType.APPLICATION_JSON) {

            throw new UnsupportedOperationException("Not implemented yet");
        } else {
            serialized = printViolationToXML(violation);
        }
        return serialized;
    }
    
    public List<IViolation> getViolations2(String agreementId, String guaranteeTerm,
            String providerUuid, Date begin, Date end, String resultType)     throws HelperException {
        logger.debug("StartOf getViolationsByAgreementId agreementId:"+agreementId +" guaranteeTerm "+guaranteeTerm+ " providerUuid "+ providerUuid + " begin "+begin+" end "+end+" resultType "+resultType);
        List<IViolation> violationList = null; 
        
        if (agreementId == null && guaranteeTerm == null
                && providerUuid == null && begin == null && end == null) {
            /*
             * All violations
             */
            logger.debug("REQUEST for /violations/");
            violationList = violationDAO.getAll();
        } else if (agreementId != null && providerUuid == null) {
            /*
             * Violations by agreement
             */
            logger.debug("REQUEST for /violations/?agreementId=\"\",&term=\"\",&begin=\"\",&term=\"\"");
            violationList = violationDAO.getByAgreementIdInARangeOfDates(agreementId, guaranteeTerm, begin, end);
        } else if (providerUuid != null) {
            /*
             * Violations by provider
             */
            logger.debug("REQUEST for /violations/?providerUuid=\"\"&begin=\"\",&end=\"\"");
            if (begin != null || end != null) {
                violationList = violationDAO.getByProviderInaRangeOfDates(providerUuid, begin, end);
            } else if (begin == null && end == null) {
                violationList = violationDAO.getByProvider(providerUuid);
            }
        } else {
            // DEFAULT CASE: throw exception
            throw new HelperException(Code.INTERNAL, "Not valid query");
        }
        return violationList;
    }
    
}
