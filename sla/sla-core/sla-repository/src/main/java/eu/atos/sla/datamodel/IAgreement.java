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

public interface IAgreement {

    static public class Context {
        
        static public enum ServiceProvider {
            AGREEMENT_INITIATOR("AgreementInitiator"), 
            AGREEMENT_RESPONDER("AgreementResponder");
            
            String label;
            private ServiceProvider(String label) {
                
                this.label = label;
            }
            
            @Override
            public String toString() {
                return label;
            }

            public static ServiceProvider fromString(String value) {
                
                if (AGREEMENT_INITIATOR.toString().equals(value)) {
                    return AGREEMENT_INITIATOR;
                }
                else if (AGREEMENT_RESPONDER.toString().equals(value)) {
                    return AGREEMENT_RESPONDER;
                }
                throw new IllegalArgumentException(String.format(
                        "No enum %s[label=%s]", ServiceProvider.class.getName(), value));
            }
        }        
    }
    static public enum AgreementStatus {
        PENDING, OBSERVED, REJECTED, COMPLETE, PENDING_AND_TERMINATING, OBSERVED_AND_TERMINATING, TERMINATED
    }
    
    

    /*
     * Internal ID
     */
    Long getId();

    // /**
    // * This agreement is recognized by external parties by this internally
    // generated UUID.
    // */
    // UUID getUuid();

    /**
     * This agreement is recognized by external parties by the wsag:Agreement/@AgreementId
     * attribute. If the attribute was not set, a new one is generated and
     * communicated to the consumer.
     */
    String getAgreementId();

    void setAgreementId(String agreementId);

    /**
     * The provider of the service.
     */
    IProvider getProvider();

    void setProvider(IProvider provider);

    /**
     * Consumer ID, provided by the consumer.
     */
    String getConsumer();

    void setConsumer(String consumer);

    /**
     * This agreement is valid until the expiration date
     */
    Date getExpirationDate();

    void setExpirationDate(Date date);

    /**
     * The agreement is in this ws-agreement state. An agreement is based on a
     * template. During the negotiation, an agreement is considered and
     * AgreementOffer. Once the negotiation is accepted, the agreement is
     * considered a contract.
     */
    AgreementStatus getStatus();

    void setStatus(AgreementStatus status);

    /**
     * The agreement is based on this template.
     */
    ITemplate getTemplate();

    void setTemplate(ITemplate template);

    /**
     * Agreement body. This is an ws-agreement-compliant xml. NOTE: String?
     * Maybe there is a better type.
     */
    String getText();

    void setText(String text);

    /**
     * These are the statements to offered service levels.
     */
    List<IGuaranteeTerm> getGuaranteeTerms();

    void setGuaranteeTerms(List<IGuaranteeTerm> guaranteeTerm);

    /**
     * This is the content of /agreement/context/sla:service element.
     * 
     * This element identifies the unique service offered by this agreement. 
     * It differs from serviceName attributes in that there can be several
     * serviceNames per agreement.
     */
    public String getServiceId();

    public void setServiceId(String serviceId);

    /**
     * Name of the agreement.
     */
    public String getName();

    public void setName(String name);
    
    /**
     * ServiceProperties are used to define measurable and exposed properties associated
     * with a service, such as response time and throughtput. The properties are used
     * in expressing service level objectives.
     */
    public List<IServiceProperties> getServiceProperties();
    
    public void setServiceProperties(List<IServiceProperties> serviceProperties);
    
    
    /**
     * some of the GuaranteeTerms should just measured and evaluated at the end of the execution of 
     * the enforcementJob. This variable indicates if it has to be executed or not. 
     */
    public Boolean getHasGTermToBeEvaluatedAtEndOfEnformcement();

    public void setHasGTermToBeEvaluatedAtEndOfEnformcement(Boolean hasGTermToBeEvaluatedAtEndOfEnformcement);

}