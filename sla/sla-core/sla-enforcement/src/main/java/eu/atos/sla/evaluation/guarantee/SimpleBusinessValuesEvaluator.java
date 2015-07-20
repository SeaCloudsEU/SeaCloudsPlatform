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
package eu.atos.sla.evaluation.guarantee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IBusinessValueList;
import eu.atos.sla.datamodel.ICompensation;
import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.ICompensationDefinition.CompensationKind;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Penalty;

/**
 * BusinessValuesEvaluator that raises a penalty if the existent number of violations match the 
 * count in the penalty definition and they occur in interval time defined in the penalty definition.
 * 
 */
public class SimpleBusinessValuesEvaluator implements IBusinessValuesEvaluator {
    private static Logger logger = LoggerFactory.getLogger(SimpleBusinessValuesEvaluator.class);
    
    private IViolationRepository repository;
    
    @Override
    public List<? extends ICompensation> evaluate(
            IAgreement agreement, IGuaranteeTerm term, List<IViolation> newViolations, Date now) {
        
        logger.debug("Evaluating business for {} new violations", newViolations.size());
        List<ICompensation> result = new ArrayList<ICompensation>();
        IBusinessValueList businessValues = term.getBusinessValueList();
        if (businessValues == null) {
            /*
             * sanity check
             */
            return Collections.emptyList();
        }
        for (IPenaltyDefinition penaltyDef : businessValues.getPenalties()) {
            if (penaltyDef.getKind() != CompensationKind.CUSTOM_PENALTY) {
                continue;
            }
            Date violationsBegin = new Date(now.getTime() - penaltyDef.getTimeInterval().getTime());
            /*
             * TODO: violationsBegin should be max(violationsBegin, select last(penalty) where penalty.definition = def
             */
            List<IViolation> oldViolations = 
                    repository.getViolationsByTimeRange(agreement, term.getName(), violationsBegin, now);
            
            if (thereIsPenalty(penaltyDef, newViolations, oldViolations)) {
                
                IPenalty penalty = new Penalty(
                        agreement.getAgreementId(),
                        now,
                        term.getKpiName(),
                        penaltyDef, 
                        getLastViolation(newViolations, oldViolations));
                result.add(penalty);
                logger.debug("Raised {}", penalty);
            }
        }
        return result;
    }

    private boolean thereIsPenalty(IPenaltyDefinition penaltyDef,
            List<IViolation> newViolations, List<IViolation> oldViolations) {
        return oldViolations.size() + newViolations.size() >= penaltyDef.getCount();
    }
    
    private IViolation getLastViolation(List<IViolation> violations1, List<IViolation> violations2) {
        
        if (violations1.size() > 0) {
            return violations1.get(violations1.size() - 1);
        }
        else if (violations2.size() > 0) {
            return violations2.get(violations2.size() - 1);
        }
        else {
            throw new IllegalStateException("Raising penalty with no violations");
        }
    }

    public IViolationRepository getCompensationRepository() {
        return repository;
    }

    public void setCompensationRepository(IViolationRepository repository) {
        this.repository = repository;
    }
}