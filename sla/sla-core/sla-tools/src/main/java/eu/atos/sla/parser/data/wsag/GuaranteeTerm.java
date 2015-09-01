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
package eu.atos.sla.parser.data.wsag;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GuaranteeTerm")
public class GuaranteeTerm {

    @XmlAttribute(name = "Name")
    private String name;
    @XmlElement(name = "ServiceScope")
    private ServiceScope serviceScope;
    @XmlElement(name = "ServiceLevelObjective")
    private ServiceLevelObjective serviceLevelObjective;
    @XmlElement(name = "QualifyingCondition")
    private String qualifyingCondition;
    @XmlElement(name="BusinessValueList")
    private BusinessValueList businessValueList;

    public GuaranteeTerm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceScope getServiceScope() {
        return serviceScope;
    }

    public void setServiceScope(ServiceScope serviceScope) {
        this.serviceScope = serviceScope;
    }

    public ServiceLevelObjective getServiceLevelObjetive() {
        return serviceLevelObjective;
    }

    public void setServiceLevelObjetive(
            ServiceLevelObjective serviceLevelObjetive) {
        this.serviceLevelObjective = serviceLevelObjetive;
    }

    public String getQualifyingCondition() {
        return qualifyingCondition;
    }

    public void setQualifyingCondition(String qualifyingCondition) {
        this.qualifyingCondition = qualifyingCondition;
    }

    public BusinessValueList getBusinessValueList() {
        return businessValueList;
    }
    
    public void setBusinessValueList(BusinessValueList businessValueList) {
        this.businessValueList = businessValueList;
    }
}
