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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.parser.data.Violation;
import eu.atos.sla.service.rest.helpers.exception.ParserHelperException;
import eu.atos.sla.util.IModelConverter;

/**
 * 
 */

@Service
@Transactional
public class ViolationHelperE {
    private static Logger logger = LoggerFactory.getLogger(ViolationHelperE.class);

    @Autowired
    public IViolationDAO violationDAO;
    
    @Autowired
    private IModelConverter modelConverter;
    

    public ViolationHelperE() {

    }


    public Violation getViolationByUUID(UUID uuid) {
        logger.debug("StartOf getViolationByUUID uuid:"+uuid);
        IViolation storedViolation = violationDAO.getViolationByUUID(uuid.toString());
        Violation violation = modelConverter.getViolationXML(storedViolation);
        logger.debug("EndOf getViolationByUUID");
        return violation;
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

    
    public List<Violation> getViolations(String agreementId, String guaranteeTerm,
            String providerUuid, Date begin, Date end)
            throws ParserHelperException {
        logger.debug(
                "StartOf getViolationsByAgreementId agreementId:{} guaranteeTerm:{} providerUuid:{} begin:{}  end:{}", 
                agreementId, guaranteeTerm, providerUuid, begin, end);
        
        List<Violation> violations = new ArrayList<Violation>();
        List<IViolation> storedViolations = null;
        if (agreementId == null && guaranteeTerm == null
                && providerUuid == null && begin == null && end == null) {
            /*
             * All violations
             */
            logger.debug("REQUEST for /violations/");
            storedViolations = violationDAO.getAll();
                
        } else if (agreementId != null && providerUuid == null) {
            /*
             * Violations by agreement
             */
            logger.debug("REQUEST for /violations/?agreementId=\"\",&term=\"\",&begin=\"\",&term=\"\"");
            storedViolations = violationDAO.getByAgreementIdInARangeOfDates(agreementId, guaranteeTerm, begin, end);
    
        } else if (providerUuid != null) {
            /*
             * Violations by provider
             */
            logger.debug("REQUEST for /violations/?providerUuid=\"\"&begin=\"\",&end=\"\"");
                
            if (begin != null || end != null) {
                storedViolations = violationDAO.getByProviderInaRangeOfDates(providerUuid, begin, end);
            } else if (begin == null && end == null) {
                storedViolations = violationDAO.getByProvider(providerUuid);
            }

        } else {
            throw new ParserHelperException("Not valid query");
        }
        for (IViolation storedViolation:storedViolations) violations.add(modelConverter.getViolationXML(storedViolation));
        return violations;
    }
    
}
