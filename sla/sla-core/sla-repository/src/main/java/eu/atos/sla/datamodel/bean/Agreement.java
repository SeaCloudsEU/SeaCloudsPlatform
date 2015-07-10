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
package eu.atos.sla.datamodel.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.IServiceProperties;
import eu.atos.sla.datamodel.ITemplate;

/**
 * A POJO Object that stores all the information from a Agreement
 * 
 */
@Entity
@Table(name = "agreement")
@NamedQueries({
        @NamedQuery(name = Agreement.QUERY_FIND_ALL, query = "SELECT p FROM Agreement p"),
        @NamedQuery(name = Agreement.QUERY_FIND_BY_AGREEMENT_ID, query = "SELECT p FROM Agreement p where p.agreementId = :agreementId "),
        @NamedQuery(name = Agreement.QUERY_FIND_BY_CONSUMER, query = "SELECT p FROM Agreement p where p.consumer = :consumerId "),
        @NamedQuery(name = Agreement.QUERY_FIND_BY_PROVIDER, query = "SELECT p FROM Agreement p where  p.provider.uuid = :providerUuid "),
        @NamedQuery(name = Agreement.QUERY_FIND_BY_TEMPLATEUUID, query = "SELECT p FROM Agreement p where  p.template.uuid = :templateUUID "),
        @NamedQuery(name = Agreement.QUERY_ACTIVE_AGREEMENTS, query = "SELECT p FROM Agreement p where p.expirationDate > :actualDate "),
        @NamedQuery(name = Agreement.QUERY_FIND_BY_TEMPLATEUUID_AND_CONSUMER, query = "SELECT p FROM Agreement p where (p.template.uuid = :templateUUID) AND (p.consumer = :consumerId)"),
        @NamedQuery(name = Agreement.QUERY_SEARCH, query = "SELECT a FROM Agreement a "
                + "LEFT JOIN a.template t "
                + "WHERE (:providerId is null or a.provider.uuid = :providerId) "
                + "AND (:consumerId is null or a.consumer = :consumerId) "
                + "AND (:templateId is null or t.uuid = :templateId) "
                + "AND (:active is null "
                + "    or (:active = true and a.expirationDate > current_timestamp()) "
                + "    or (:active = false and a.expirationDate <= current_timestamp()))") })

public class Agreement implements IAgreement, Serializable {
    public final static String QUERY_FIND_ALL = "Agreement.findAll";
    public final static String QUERY_FIND_ALL_AGREEMENTS = "Agreement.findAllAgreements";
    public final static String QUERY_FIND_BY_PROVIDER = "Agreement.findByProvider";
    public final static String QUERY_FIND_BY_CONSUMER = "Agreement.findByConsumer";
    public final static String QUERY_FIND_BY_AGREEMENT_ID = "Agreement.getByAgreementId";
    public final static String QUERY_ACTIVE_AGREEMENTS = "Agreement.getActiveAgreements";
    public final static String QUERY_FIND_BY_TEMPLATEUUID = "Agreement.getByTemplateUUID";
    public final static String QUERY_FIND_BY_TEMPLATEUUID_AND_CONSUMER = "Agreement.getByTemplateUUIDAndConsumer";
    public final static String QUERY_SEARCH = "Agreement.search";

    private static final long serialVersionUID = -5939038640423447257L;

    private Long id;
    private String agreementId;
    private String consumer;
    private IProvider provider;
    private ITemplate template;
    private Date expirationDate;
    private AgreementStatus status;
    private String text;
    private List<IServiceProperties> serviceProperties;
    private List<IGuaranteeTerm> guaranteeTerms;
    private String serviceId;
    private Boolean hasGTermToBeEvaluatedAtEndOfEnformcement;
    private String name;
                            
    public Agreement() {
    }

    public Agreement(String agreementId) {
        this.agreementId = agreementId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "agreement_id", unique = true)
    public String getAgreementId() {

        return agreementId;
    }

    public void setAgreementId(String agreementId) {

        this.agreementId = agreementId;
    }

    @Column(name = "consumer")
    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    @ManyToOne(targetEntity = Provider.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", nullable = false)
    public IProvider getProvider() {
        return provider;
    }

    public void setProvider(IProvider provider) {
        this.provider = provider;
    }

    @ManyToOne(targetEntity = Template.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", referencedColumnName = "id", nullable = true)
    public ITemplate getTemplate() {
        return template;
    }

    public void setTemplate(ITemplate template) {
        this.template = template;
    }

    @Column(name = "expiration_time")
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Transient
    public AgreementStatus getStatus() {
        return status;
    }

    public void setStatus(AgreementStatus status) {
        this.status = status;
    }

    @Column(name = "text", columnDefinition = "longtext")
    @Lob
    public String getText() {
        return text;
    }

    @Lob
    public void setText(String text) {
        this.text = text;
    }

    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = ServiceProperties.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id", nullable = true)
    public List<IServiceProperties> getServiceProperties() {
        return serviceProperties;
    }

    public void setServiceProperties(List<IServiceProperties> serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    @Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = GuaranteeTerm.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id", nullable = true)
    public List<IGuaranteeTerm> getGuaranteeTerms() {
        return guaranteeTerms;
    }

    public void setGuaranteeTerms(List<IGuaranteeTerm> guaranteeTerms) {
        this.guaranteeTerms = guaranteeTerms;

    }

    @Column(name = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    @Column(name = "metrics_eval_end")
    public Boolean getHasGTermToBeEvaluatedAtEndOfEnformcement() {
        return hasGTermToBeEvaluatedAtEndOfEnformcement;
    }

    public void setHasGTermToBeEvaluatedAtEndOfEnformcement(
            Boolean hasGTermToBeEvaluatedAtEndOfEnformcement) {
        this.hasGTermToBeEvaluatedAtEndOfEnformcement = hasGTermToBeEvaluatedAtEndOfEnformcement;
    }

    @Column(name = "name")
    public String getName() {
        // TODO Auto-generated method stub
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
}