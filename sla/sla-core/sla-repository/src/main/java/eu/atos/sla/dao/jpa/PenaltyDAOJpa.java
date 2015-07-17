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
package eu.atos.sla.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.bean.Penalty;

@Repository("PenaltyRepository")
public class PenaltyDAOJpa implements IPenaltyDAO {

    private static Logger logger = LoggerFactory.getLogger(PenaltyDAOJpa.class);
    private EntityManager entityManager;

    @PersistenceContext(unitName = "slarepositoryDB")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    @Override
    public Penalty getById(Long id) {
        return entityManager.find(Penalty.class, id);
    }

    @Override
    public IPenalty save(IPenalty penalty) {

        entityManager.persist(penalty);
        entityManager.flush();

        return penalty;
    }

    @Override
    public IPenalty getByUuid(String uuid) {
        try {
            Query query = entityManager.createNamedQuery(Penalty.Query.FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            Penalty penalty = null;

            penalty = (Penalty) query.getSingleResult();

            return penalty;

        } catch (NoResultException e) {
            logger.debug("No Result searching " + uuid + ":" + e);
            return null;
        }
    }

    @Override
    public List<IPenalty> getByAgreement(String agreementId) {
        SearchParameters params = new SearchParameters();
        params.setAgreementId(agreementId);
        
        return search(params);
    }

    @Override
    public List<IPenalty> search(SearchParameters params) {
        TypedQuery<IPenalty> query = entityManager.createNamedQuery(
                Penalty.Query.SEARCH,
                IPenalty.class);

        query.setParameter("agreementId", params.getAgreementId());
        query.setParameter("termName", params.getGuaranteeTermName());
        query.setParameter("begin", params.getBegin());
        query.setParameter("end", params.getEnd());
        
        List<IPenalty> penalties = query.getResultList();
        return penalties;
    }

}
