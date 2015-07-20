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
package eu.atos.sla.parser.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.atos.sla.parser.DateTimeDeserializerJSON;
import eu.atos.sla.parser.DateTimeSerializerJSON;


/**
 * A POJO Object that stores all the information from a Agreement
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "enforcement_job")
public class EnforcementJob {
    @XmlElement(name = "id")
    private Long id;
    @XmlElement(name = "agreement_id", required=true)
    private String agreementId;
    @XmlElement(name = "enabled")
    private boolean enabled;
    @JsonSerialize(using=DateTimeSerializerJSON.class)
    @JsonDeserialize(using=DateTimeDeserializerJSON.class)
    @XmlElement(name = "last_executed", nillable=true)
    private Date lastExecuted;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(String agreementId) {
        this.agreementId = agreementId;
    }

    public Date getLastExecuted() {

        return lastExecuted;
    }

    public boolean getEnabled() {

        return enabled;
    }

    public void setLastExecuted(Date lastExecuted) {
        this.lastExecuted = lastExecuted;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
