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
import java.util.List;

public interface IViolation {

    /*
     * Internal generated ID
     */
    Long getId();

    /**
     * Internal generated UUID. The interested external parties are going to
     * identify this violation by the UUID.
     */
    String getUuid();

    void setUuid(String uuid);

    /**
     * Contract UUID where this violation has been detected.
     */
    String getContractUuid();

    void setContractUuid(String contractUuid);

    /**
     * @see IGuaranteeTerm#getServiceName()
     */
    String getServiceName();

    void setServiceName(String serviceName);

    /**
     * @see IGuaranteeTerm#getServiceScope()
     */
    String getServiceScope();

    void setServiceScope(String serviceScope);

    /**
     * Name of the kpi that has generated this breach.
     */
    String getKpiName();

    void setKpiName(String metricName);

    /**
     * Date and time when the violation was raised.
     */
    Date getDatetime();

    void setDatetime(Date dateTime);

    /**
     * Expected value of the non fulfilled metric.
     */
    String getExpectedValue();

    void setExpectedValue(String expectedValue);

    /**
     * Measured value of the metric. If a violation is raised by 2 or more breaches,
     * the value is a string of format { value1, value2, ..., valueN }
     */
    String getActualValue();

    void setActualValue(String actualValue);
    
    /**
     * Breaches that raised this violation.
     */
    List<IBreach> getBreaches();

    void setBreaches(List<IBreach> breaches);

    /**
     * Policy that raised this violation. If null, there is no policies in the GuaranteeTerm.
     */
    IPolicy getPolicy();
    
    void setPolicy(IPolicy policy);

}