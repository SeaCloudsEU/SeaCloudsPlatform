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
package eu.atos.sla.datamodel;

import java.util.Date;

import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;

public interface ICompensation {

    public interface IPenalty extends ICompensation {
        
        IPenaltyDefinition getDefinition();

        /**
         * Last violation that generated this penalty.
         */
        IViolation getViolation();
    }
    
    public interface IReward extends ICompensation {
        
    }

    /*
     * Internal generated ID
     */
    Long getId();

    /**
     * Internal generated UUID. The interested external parties are going to
     * identify this violation by the UUID.
     */
    String getUuid();

    /**
     * AgreementId where this compensation has been enforced.
     */
    String getAgreementId();

    /**
     * Date and time when the compensation was raised.
     */
    Date getDatetime();

    /**
     * Name of the kpi that has generated this compensation.
     */
    String getKpiName();
}
