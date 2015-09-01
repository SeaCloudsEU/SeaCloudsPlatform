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

public interface IBreach {

    /*
     * Internal generated ID
     */
    Long getId();

    /**
     * Date and time of the metric that has generated this breach.
     * 
     * @return
     */
    Date getDatetime();

    /**
     * Name of the kpiName that has generated this breach.
     */
    String getKpiName();

    String getValue();

    /**
     * Value of the metric that has generated this breach.
     * 
     * @return
     */

    void setId(Long id);

    void setValue(String value);

    /**
     * Date and time of the metric that has generated this breach.
     * 
     * @return
     */
    void setDatetime(Date date);

    /**
     * Name of the metric that has generated this breach.
     */
    void setKpiName(String metric);

    String getAgreementUuid();

    void setAgreementUuid(String agreementUuid);

}