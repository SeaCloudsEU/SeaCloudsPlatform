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
package eu.atos.sla.evaluation.guarantee;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.ICompensation;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.monitoring.IMonitoringMetric;

/**
 * A GuaranteeTermEvaluator performs the evaluation of a guarantee term, consisting in:
 * <ul>
 * <li>A service level evaluation, assessing which metrics are violations.
 * <li>A business evaluation, assessing what penalties are derived from the raised violations.
 * </ul>
 *
 * Usage:
 * <pre>
 * GuaranteeTermEvaluator gte = new GuaranteeTermEvaluator();
 * gte.setServiceLevelEvaluator(...);
 * gte.setBusinessEvaluator(...);
 * 
 * gte.evaluate(...)
 * </pre>
 * @see IServiceLevelEvaluator
 * @see IBusinessValuesEvaluator

 * @author rsosa
 *
 */
public class GuaranteeTermEvaluator {
	
	public static final class GuaranteeTermEvaluationResult {
		private final List<IViolation> violations;
		private final List<? extends ICompensation> compensations;

		public GuaranteeTermEvaluationResult(List<IViolation> violations,
				List<? extends ICompensation> compensations) {
			this.violations = violations;
			this.compensations = compensations;
		}

		public List<IViolation> getViolations() {
			return violations;
		}

		public List<? extends ICompensation> getCompensations() {
			return compensations;
		}
	}

	private static Logger logger = LoggerFactory.getLogger(GuaranteeTermEvaluator.class);
	
	private IServiceLevelEvaluator serviceLevelEval;
	private IBusinessValuesEvaluator businessEval;
	
	public GuaranteeTermEvaluator() {
	}
	
	/**
	 * Evaluate violations and penalties for a given guarantee term and a list of metrics.
	 * 
	 * @param agreement that contains the term to evaluate
	 * @param term guarantee term to evaluate
	 * @param metrics list of metrics to evaluated if fulfill the service level of the term.
	 * @param now the evaluation period ends at <code>now</code>.
	 */
	public GuaranteeTermEvaluationResult evaluate(
			IAgreement agreement, IGuaranteeTerm term, List<IMonitoringMetric> metrics, Date now) {

		/*
		 * throws NullPointerException if not property initialized 
		 */
		checkInitialized();
					
		logger.debug("evaluate(agreement={}, term={}, now={})", 
				agreement.getAgreementId(), term.getKpiName(), now);

		final List<IViolation> violations = serviceLevelEval.evaluate(agreement, term, metrics, now);
		logger.debug("Found " + violations.size() + " violations");
		final List<? extends ICompensation> compensations = businessEval.evaluate(agreement, term, violations, now);
		logger.debug("Found " + compensations.size() + " compensations");
		
		GuaranteeTermEvaluationResult result = new GuaranteeTermEvaluationResult(violations, compensations); 
		
		return result;
	}
	
	/**
	 * Evaluate penalties for a given guarantee term and a list of violations.
	 * 
	 * @param agreement that contains the term to evaluate
	 * @param term guarantee term to evaluate
	 * @param violations list of violations to evaluated if raise any compensation.
	 * @param now the evaluation period ends at <code>now</code>.
	 */
	public GuaranteeTermEvaluationResult evaluateBusiness(
			IAgreement agreement, IGuaranteeTerm term, List<IViolation> violations, Date now) {
		
		/*
		 * throws NullPointerException if not property initialized 
		 */
		checkInitialized();
					
		logger.debug("evaluateBusiness(agreement={}, term={}, now={})", 
				agreement.getAgreementId(), term.getKpiName(), now);

		logger.debug("Found " + violations.size() + " violations");
		final List<? extends ICompensation> compensations = businessEval.evaluate(agreement, term, violations, now);
		logger.debug("Found " + compensations.size() + " compensations");
		
		GuaranteeTermEvaluationResult result = new GuaranteeTermEvaluationResult(violations, compensations); 
		
		return result;
	}

	private void checkInitialized() {
		if (serviceLevelEval == null) {
			throw new NullPointerException("serviceLevelEvaluator has not been set");
		}
		if (businessEval == null) {
			throw new NullPointerException("businessEvaluator has not been set");
		}
	}

	public IServiceLevelEvaluator getServiceLevelEvaluator() {
		return serviceLevelEval;
	}

	public void setServiceLevelEvaluator(IServiceLevelEvaluator serviceLevelEval) {
		this.serviceLevelEval = serviceLevelEval;
	}

	public IBusinessValuesEvaluator getBusinessEvaluator() {
		return businessEval;
	}

	public void setBusinessEvaluator(IBusinessValuesEvaluator businessEval) {
		this.businessEval = businessEval;
	}

}