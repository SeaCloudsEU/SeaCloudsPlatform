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

import java.util.Date;
import java.util.List;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IBreach;

/**
 * Defines the access to a repository of breaches. This repository is needed for the PoliciedServiceLevelEvaluator.
 * 
 * @see PoliciedServiceLevelEvaluator
 * 
 *
 */
public interface IBreachRepository {
    
    /**
     * Get the agreement breaches in a specified interval (inclusive ends)
     * 
     * @param agreement to check.
     * @param kpiName that generated the breach
     * @param begin inclusive begin date.
     * @param end  inclusive end date.
     */
    List<IBreach> getBreachesByTimeRange(IAgreement agreement, String kpiName, Date begin, Date end);
    
    /**
     * Persist in the repository a list of breaches.
     */
    void saveBreaches(List<IBreach> breaches);
}