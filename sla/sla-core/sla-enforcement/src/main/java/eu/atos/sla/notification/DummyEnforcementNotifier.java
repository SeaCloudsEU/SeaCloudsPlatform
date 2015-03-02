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
package eu.atos.sla.notification;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;

public class DummyEnforcementNotifier implements IAgreementEnforcementNotifier {
	private static Logger logger = LoggerFactory.getLogger(DummyEnforcementNotifier.class);

	@Override
	public void onFinishEvaluation(IAgreement agreement,
			Map<IGuaranteeTerm, GuaranteeTermEvaluationResult>  guaranteeTermEvaluationMap) {
		logger.debug("Notifying onFinishEvaluation {}", agreement.getId());
		for (Entry<IGuaranteeTerm, GuaranteeTermEvaluationResult> e:guaranteeTermEvaluationMap.entrySet()) {
			IGuaranteeTerm gt = e.getKey();
			GuaranteeTermEvaluationResult gtresult = e.getValue();
			logger.debug("Notifying onFinishEvaluation GuaranteeTerm:{} Num violations:{} Num compensations:{} ", gt.getId(), gtresult.getViolations().size(), gtresult.getCompensations().size());
		}
		logger.debug("  onFinishEvaluation", agreement.getId());
	}

}
