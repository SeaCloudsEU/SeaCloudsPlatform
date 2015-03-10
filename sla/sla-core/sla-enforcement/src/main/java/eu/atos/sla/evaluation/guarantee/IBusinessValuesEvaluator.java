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

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.ICompensation;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IViolation;

/**
 * Assesses the compensations that are derived from a list of violations.
 * 
 * @author rsosa
 *
 */
public interface IBusinessValuesEvaluator {
	
	/**
	 * Assesses the compensations that are derived from a list of violations.
	 * 
	 * @param agreement agreement being evaluated.
	 * @param term of the agreement being evaluated.
	 * @param violations detected in the service level evaluation.
	 * @param now the evaluation period ends at <code>now</code>.
	 * @return list of compensations.
	 */
	List<? extends ICompensation> evaluate(
			IAgreement agreement, IGuaranteeTerm term, List<IViolation> violations, Date now);
}