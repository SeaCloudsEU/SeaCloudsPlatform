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

import java.util.List;
import java.util.Map;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;
import eu.atos.sla.monitoring.IMonitoringMetric;


/**
 * Business layer for the enforcement processes.
 * 
 *
 */
public interface IEnforcementService {

    /**
     * Store a new EnforcementJob.
     * 
     * @param job
     * @return created enforcement job
     */
    IEnforcementJob createEnforcementJob(IEnforcementJob job);

    /**
     * Create a disabled enforcement job for an agreement. 
     * 
     * @param agreementId of agreement to enforce
     * @return created enforcement job
     */
    IEnforcementJob createEnforcementJob(String agreementId);
    
    /**
     * Delete an existing EnforcementJob
     * 
     * @param agreementId AgreementId of the associated agreement
     * @return <code>true</code> if job deleted; else <code>false</code>
     */
    boolean deleteEnforcementJobByAgreementId(String agreementId);
    
    /**
     * Get job by id
     */
    IEnforcementJob getEnforcementJob(Long id);
    
    /**
     * Get job by related agreementId
     */
    IEnforcementJob getEnforcementJobByAgreementId(String agreementId);
    
    /**
     * Get all enforcement jobs
     */
    List<IEnforcementJob> getEnforcementJobs();
    
    /**
     * Start the enforcement job with related agrementId
     */
    boolean startEnforcement(String agreementId);
    
    /**
     * Stop the enforcement job with related agrementId
     */
    boolean stopEnforcement(String agreementId);

    /**
     * Save to repository the result of an enforcement process.
     * 
     * @param agreement Agreement enforced.
     * @param result List of violations and business violations that were raised in the enforcement.
     */
    void saveEnforcementResult(IAgreement agreement, Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> result);
    
    /**
     * Enforce an agreement with the given metrics.
     * @param agreement to enforce
     * @param metrics to enforce.
     */
    void doEnforcement(IAgreement agreement, Map<IGuaranteeTerm, List<IMonitoringMetric>> metrics);
    
    
    void saveCheckedGuaranteeTerm(IGuaranteeTerm term);
    
}
