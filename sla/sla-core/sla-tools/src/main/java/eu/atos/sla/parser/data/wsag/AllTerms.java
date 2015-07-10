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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "All")
public class AllTerms {

    @XmlElement(name = "ServiceDescriptionTerm")
    private ServiceDescriptionTerm serviceDescriptionTerm;
    @XmlElement(name = "ServiceProperties")
    private List<ServiceProperties> serviceProperties;
    @XmlElement(name = "GuaranteeTerm")
    private List<GuaranteeTerm> guaranteeTerms;

    public AllTerms() {
    }

    public ServiceDescriptionTerm getServiceDescriptionTerm() {
        return serviceDescriptionTerm;
    }

    public void setServiceDescriptionTerm(
            ServiceDescriptionTerm serviceDescriptionTerm) {
        this.serviceDescriptionTerm = serviceDescriptionTerm;
    }

    public List<ServiceProperties> getServiceProperties() {
        return serviceProperties;
    }

    public void setServiceProperties(List<ServiceProperties> serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public List<GuaranteeTerm> getGuaranteeTerms() {
        return guaranteeTerms;
    }

    public void setGuaranteeTerms(List<GuaranteeTerm> guaranteeTerms) {
        this.guaranteeTerms = guaranteeTerms;
    }

    

}
