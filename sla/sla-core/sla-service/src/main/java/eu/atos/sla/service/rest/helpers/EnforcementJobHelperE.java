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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.enforcement.IEnforcementService;
import eu.atos.sla.parser.data.EnforcementJob;
import eu.atos.sla.service.rest.helpers.exception.DBExistsHelperException;
import eu.atos.sla.service.rest.helpers.exception.DBMissingHelperException;
import eu.atos.sla.service.rest.helpers.exception.InternalHelperException;
import eu.atos.sla.util.IModelConverter;
import eu.atos.sla.util.ModelConversionException;

/**
 * 
 */

@Service
@Transactional
public class EnforcementJobHelperE{
    private static Logger logger = LoggerFactory.getLogger(EnforcementJobHelperE.class);

    
    @Autowired
    private IAgreementDAO agreementDAO;

    @Autowired
    private IEnforcementJobDAO enforcementJobDAO;
    
    @Autowired
    private IEnforcementService enforcementService;
    
    @Autowired
    IModelConverter modelConverter;

    public EnforcementJobHelperE() {
    }



    private boolean doesEnforcementExistInRepository(String agreementId) {
        return enforcementJobDAO.getByAgreementId(agreementId) != null;
    }


    public List<EnforcementJob> getEnforcements() {
        logger.debug("StartOf getEnforcements");
        List<IEnforcementJob> storedEnforcementJobs  = this.enforcementJobDAO.getAll();
        List<EnforcementJob> enforcementJobs = new ArrayList<EnforcementJob>(); 
        for (IEnforcementJob storedEnforcementJob:storedEnforcementJobs) enforcementJobs.add(modelConverter.getEnforcementJobXML(storedEnforcementJob));
        logger.debug("EndOf getEnforcements");
        return enforcementJobs;
    }

    public EnforcementJob getEnforcementJobByUUID(String agreementUUID) {
        logger.debug("StartOf getEnforcementJobByUUID uuid:{}", agreementUUID);
        EnforcementJob enforcementJob = null;
        IEnforcementJob storedEnforcementJob = this.enforcementJobDAO.getByAgreementId(agreementUUID);
        if (storedEnforcementJob!=null)
            enforcementJob  = modelConverter.getEnforcementJobXML(storedEnforcementJob);
        logger.debug("EndOf getEnforcementJobByUUID");
        return enforcementJob;
    }


    public boolean startEnforcementJob(String agreementUUID){
        logger.debug("startEnforcementJob agreementId:{}", agreementUUID);
        return enforcementService.startEnforcement(agreementUUID);
    }

    public boolean stopEnforcementJob(String agreementUuid){
        logger.debug("stopEnforcementJob agreementUuid:{}" ,agreementUuid);
        return enforcementService.stopEnforcement(agreementUuid);
    }

    public String createEnforcementJob(EnforcementJob enforcementJobXML)
            throws DBExistsHelperException, InternalHelperException, DBMissingHelperException {
        logger.debug("StartOf createEnforcementJob");
        IEnforcementJob enforcementJob = null;
        IEnforcementJob stored = null;
    
        try {
            if (enforcementJobXML != null) {
                if (!doesEnforcementExistInRepository(enforcementJobXML.getAgreementId())) {
                    // the enforcement doesn't exist
                    enforcementJob = modelConverter.getEnforcementJobFromEnforcementJobXML(enforcementJobXML);
                    IAgreement agreement = agreementDAO.getByAgreementId(enforcementJobXML.getAgreementId());
                    if (agreement == null)
                        throw new DBMissingHelperException("Agreement with id:"
                                + enforcementJobXML.getAgreementId()
                                + " doesn't exists in the SLA Repository Database. No enforcement job could be started");
                    stored = enforcementService.createEnforcementJob(enforcementJob);
                } else {
                    throw new DBExistsHelperException("Enforcement with id:"
                            + enforcementJobXML.getAgreementId()
                            + " already exists in the SLA Repository Database");
                }
            }
        
            if (stored != null) {
                logger.debug("EndOf createEnforcementJob");
                return stored.getAgreement().getAgreementId();
            } else {
                logger.debug("EndOf createEnforcementJob");
                throw new InternalHelperException("Error when creating enforcementJob the SLA Repository Database");
            }
        } catch (ModelConversionException e) {
            logger.error("createEnforcementJob error:",e);
            throw new InternalHelperException(e.getMessage());
        }

    }

}
