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
package eu.atos.sla.enforcement;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.ICompensation;
import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.ICompensation.IReward;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IGuaranteeTerm.GuaranteeTermStatusEnum;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.EnforcementJob;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * 
 *
 */
@Service("EnforcementManager")
@Transactional
public class EnforcementService implements IEnforcementService {
    private static Logger logger = LoggerFactory.getLogger(EnforcementService.class);
    
    @Autowired
    IEnforcementJobDAO enforcementJobDAO;

    @Autowired
    IAgreementDAO agreementDAO;
    
    @Autowired
    IGuaranteeTermDAO guaranteeTermDAO;
    
    @Autowired
    IViolationDAO violationDAO;

    @Autowired
    IPenaltyDAO penaltyDAO;
    
    @Autowired
    AgreementEnforcement agreementEnforcement;
    
    
    @Override
    public IEnforcementJob getEnforcementJob(Long id) {

        return enforcementJobDAO.getById(id);
    }

    @Override
    public IEnforcementJob getEnforcementJobByAgreementId(String agreementId) {

        return enforcementJobDAO.getByAgreementId(agreementId);
    }

    @Override
    public List<IEnforcementJob> getEnforcementJobs() {

        return enforcementJobDAO.getAll();
    }

    @Override
    public IEnforcementJob createEnforcementJob(IEnforcementJob job) {

        String agreementId = job.getAgreement().getAgreementId();
        if (enforcementJobDAO.getByAgreementId(agreementId) != null) {
            throw new IllegalStateException("Agreement " + agreementId
                    + " already has EnforcementJob");
        }
        IAgreement agreement = agreementDAO.getByAgreementId(agreementId);
        if (agreement==null)
            throw new IllegalStateException("Agreement " + agreementId
                    + " doesn't exist in the database, cannot be associated to the enforcement");

        job.setAgreement(agreement);

        IEnforcementJob saved = enforcementJobDAO.save(job);

        return saved;

    }
    
    @Override
    public IEnforcementJob createEnforcementJob(String agreementId) {

        IEnforcementJob job = new EnforcementJob();
        IAgreement agreement = new Agreement();
        agreement.setAgreementId(agreementId);
        job.setAgreement(agreement);
        job.setEnabled(false);
        
        IEnforcementJob saved = createEnforcementJob(job);
        return saved;
    }

    @Override
    public boolean deleteEnforcementJobByAgreementId(String agreementId) {
        IEnforcementJob job = enforcementJobDAO.getByAgreementId(agreementId);

        if (job == null) {
            return false;
        }

        boolean result = enforcementJobDAO.delete((EnforcementJob)job);

        return result;
    }

    @Override
    public boolean startEnforcement(String agreementId) {
        logger.debug("startEnforcement({})", agreementId);
        IEnforcementJob job = enforcementJobDAO.getByAgreementId(agreementId);

        if (job == null) {
            return false;
        }
        job.setEnabled(true);
        enforcementJobDAO.save(job);
        IAgreement agreement = agreementDAO.getByAgreementId(agreementId);
        for (IGuaranteeTerm term : agreement.getGuaranteeTerms()) {
            if (term.getStatus() == null || GuaranteeTermStatusEnum.NON_DETERMINED.equals(term.getStatus())) {
                term.setStatus(GuaranteeTermStatusEnum.FULFILLED);
            }
            guaranteeTermDAO.save(term);
        }
        
        return true;
    }

    @Override
    public boolean stopEnforcement(String agreementId) {
        IEnforcementJob job = enforcementJobDAO.getByAgreementId(agreementId);
        if (job == null) {
            return false;
        }
        job.setEnabled(false);
        enforcementJobDAO.save(job);
            IAgreement agreement = agreementDAO.getByAgreementId(agreementId);
            if (agreement.getHasGTermToBeEvaluatedAtEndOfEnformcement()!= null){
                if (agreement.getHasGTermToBeEvaluatedAtEndOfEnformcement()){
                    try{
                        agreementEnforcement.enforce(agreement, job.getLastExecuted(), true);
                    }catch(Throwable t){
                        logger.error("Fatal error in stopEnforcement doing enforcement", t);
                    }

                }
            }
        return true;
    }

    @Override
    public void saveEnforcementResult(IAgreement agreement,
            Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> enforcementResult) {
        
        for (IGuaranteeTerm gterm : enforcementResult.keySet()) {
            IGuaranteeTerm dbTerm = guaranteeTermDAO.getById(gterm.getId());
            GuaranteeTermEvaluationResult gttermResult = enforcementResult.get(gterm);
            
            for (IViolation violation : gttermResult.getViolations()) {
                dbTerm.getViolations().add(violation);
                violationDAO.save(violation);
            }
            
            for (ICompensation compensation : gttermResult.getCompensations()) {
                
                if (compensation instanceof IPenalty) {
                    IPenalty penalty = (IPenalty)compensation;
                    penaltyDAO.save(penalty);
                    dbTerm.getPenalties().add(penalty);
                }
                else if (compensation instanceof IReward) {
                    logger.warn("Saving a Reward is not implemented");
                }
                else {
                    throw new AssertionError("Unexpected compensation type: " + compensation.getClass().getName());
                }
            }
            
            dbTerm.setStatus( dbTerm.getViolations().size() > 0? 
                    GuaranteeTermStatusEnum.VIOLATED : GuaranteeTermStatusEnum.FULFILLED);
            try{
                guaranteeTermDAO.update(dbTerm);
            }catch(Exception e){
                // sometimes the update failes, for example in the test cases.
                //in such a case 
                guaranteeTermDAO.save(dbTerm);
            }
        }
        
        IEnforcementJob job = getEnforcementJobByAgreementId(agreement.getAgreementId());
        job.setLastExecuted(new Date());
        if (job.getFirstExecuted() == null) job.setFirstExecuted(job.getLastExecuted());
        enforcementJobDAO.save(job);
        logger.info("saved enforcement result(agreement=" + agreement.getAgreementId()+")");
    }
    
    public void enforceReceivedMetrics(
            IAgreement agreement, String guaranteeTermName, List<IMonitoringMetric> metrics) {
        
        logger.debug(
                "enforceReceivedMetrics(agreement=" + agreement.getAgreementId() + ", gt=" + guaranteeTermName + ")");
        Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap = 
                new HashMap<IGuaranteeTerm, List<IMonitoringMetric>>();

        for (IGuaranteeTerm gt : agreement.getGuaranteeTerms()) {
            if (guaranteeTermName.equals(gt.getName())) {
                metricsMap.put(gt, metrics);
            }
            else {
                metricsMap.put(gt, Collections.<IMonitoringMetric>emptyList());
            }
        }
        agreementEnforcement.enforce(agreement, metricsMap);
    }
    
    @Override
    public void doEnforcement(IAgreement agreement,
            Map<IGuaranteeTerm, List<IMonitoringMetric>> metrics) {

        logger.debug("enforceReceivedMetrics(" + agreement.getAgreementId() + ")");
        agreementEnforcement.enforce(agreement, metrics);
    }

    

    @Override
    public void saveCheckedGuaranteeTerm(IGuaranteeTerm term) {
        term.setLastSampledDate(new Date());
        guaranteeTermDAO.update(term);
    }
}
