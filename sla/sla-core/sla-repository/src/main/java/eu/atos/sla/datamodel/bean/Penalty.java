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

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;
import eu.atos.sla.datamodel.IViolation;

@Entity
@Table(name="penalty")
@Access(AccessType.FIELD)
@NamedQueries({
    @NamedQuery(name=Penalty.Query.FIND_BY_UUID, query=
            "SELECT p FROM Penalty p where p.uuid = :uuid"),
    @NamedQuery(name=Penalty.Query.SEARCH, query=
            "select p from Agreement a "
            + "inner join a.guaranteeTerms t "
            + "inner join t.penalties p "
            + "where (:agreementId is null or a.agreementId = :agreementId) "
            + "and (:termName is null or t.name = :termName) "
            + "and (:begin is null or p.datetime >= :begin) "
            + "and (:end is null or p.datetime < :end) "
            + "order by p.datetime desc"),
})
public class Penalty extends Compensation implements IPenalty {
    
    public static final class Query {
        public static final String SEARCH = "Penalty.search";
        public static final String FIND_BY_UUID = "Penalty.findByUuid";
    }
    
    private static final IPenaltyDefinition DEFAULT_PENALTY = new PenaltyDefinition();
    private static final IViolation DEFAULT_VIOLATION = new Violation();
    
    @ManyToOne(targetEntity = PenaltyDefinition.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "definition_id", referencedColumnName = "id", nullable = false)
    private IPenaltyDefinition definition;
    
    @ManyToOne(targetEntity = Violation.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "violation_id", referencedColumnName = "id", nullable = false)
    private IViolation violation;
    
    public Penalty() {
        super();
        this.definition = DEFAULT_PENALTY;
        this.violation = DEFAULT_VIOLATION;
    }

    public Penalty(String agreementId, Date datetime, String kpiName, 
            IPenaltyDefinition definition, IViolation violation) {
        super(agreementId, datetime, kpiName);
        this.definition = definition;
        this.violation = violation;
    }
    
    @Override
    public IPenaltyDefinition getDefinition() {
        return definition;
    }

    @Override
    public IViolation getViolation() {
        return violation;
    }
    
    @Override
    public String toString() {
        return String.format(
                "Penalty [uuid=%s, agreementId=%s, datetime=%s, definition=%s]", 
                getUuid(), getAgreementId(), getDatetime(), definition);
    }

}
