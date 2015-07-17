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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IEnforcementJob;

@Entity
@Table(name = "enforcement_job")
@NamedQueries({
        @NamedQuery(name = EnforcementJob.QUERY_FIND_ALL, query = "SELECT o FROM EnforcementJob o"),
        @NamedQuery(name = EnforcementJob.QUERY_FIND_NOT_EXECUTED, query = "SELECT o FROM EnforcementJob o "
                + "WHERE o.enabled = true AND (o.lastExecuted < :since OR o.lastExecuted is null)"),
        @NamedQuery(name = EnforcementJob.QUERY_FIND_BY_AGREEMENT_ID, query = "SELECT o FROM EnforcementJob o "
                + "WHERE o.agreement.agreementId = :agreementId") })
public class EnforcementJob implements IEnforcementJob, Serializable {

    private static final long serialVersionUID = -4913452966352163156L;

    public final static String QUERY_FIND_ALL = "EnforcementJob.findAll";
    public final static String QUERY_FIND_NOT_EXECUTED = "EnforcementJob.findNotExecuted";
    public final static String QUERY_FIND_BY_AGREEMENT_ID = "EnforcementJob.findByAgreementId";
    public final static String QUERY_START_AGREEMENT = "EnforcementJob.startAgreement";
    public final static String QUERY_STOP_AGREEMENT = "EnforcementJob.stopAgreement";

    private Long id;
    private Date firstExecuted;
    private Date lastExecuted;
    private boolean enabled;
    private IAgreement agreement;

    @Id
    @GeneratedValue
    public Long getId() {

        return id;
    }

    @Column(name = "first_executed")
    public Date getFirstExecuted() {
        return firstExecuted;
    }

    @Column(name = "last_executed")
    public Date getLastExecuted() {

        return lastExecuted;
    }

    @Column(name = "enabled", columnDefinition = "BIT", length = 1)
    public boolean getEnabled() {

        return enabled;
    }

    @ManyToOne(targetEntity = Agreement.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "agreement_id", referencedColumnName = "id", nullable = false)
    public IAgreement getAgreement() {

        return agreement;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstExecuted(Date firstExecuted) {
        this.firstExecuted = firstExecuted;
    }

    public void setLastExecuted(Date lastExecuted) {
        this.lastExecuted = lastExecuted;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAgreement(IAgreement agreement) {
        this.agreement = agreement;
    }

}
