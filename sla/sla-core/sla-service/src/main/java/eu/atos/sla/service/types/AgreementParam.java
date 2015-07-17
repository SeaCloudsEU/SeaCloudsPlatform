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
package eu.atos.sla.service.types;

import eu.atos.sla.parser.data.wsag.Agreement;


/**
 * 
 */

public class AgreementParam {
    Agreement agreement;
    String originalSerialzedAgreement;
    public Agreement getAgreement() {
        return agreement;
    }
    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }
    public String getOriginalSerialzedAgreement() {
        return originalSerialzedAgreement;
    }
    public void setOriginalSerialzedAgreement(String originalSerialzedAgreement) {
        this.originalSerialzedAgreement = originalSerialzedAgreement;
    }
}
