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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.datamodel.bean.EnforcementJob;


@Repository("EnforcementJobService")
public class EnforcementJobDAOJpa implements IEnforcementJobDAO {
    private static Logger logger = LoggerFactory.getLogger(EnforcementJobDAOJpa.class);
    private EntityManager entityManager;

    public EnforcementJob getById(Long id) {
        return entityManager.find(EnforcementJob.class, id);
    }

    @PersistenceContext(unitName = "slarepositoryDB")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<IEnforcementJob> getNotExecuted(Date since) {

        TypedQuery<IEnforcementJob> query = entityManager.createNamedQuery(EnforcementJob.QUERY_FIND_NOT_EXECUTED, IEnforcementJob.class);
        query.setParameter("since", since);
        List<IEnforcementJob> list = query.getResultList();

        if (list != null) {
            logger.debug("Number of enforcements:" + list.size());
        } else {
            logger.debug("No Result found.");
        }

        return list;

    }

    @Override
    public EnforcementJob getByAgreementId(String agreementId) {

        TypedQuery<EnforcementJob> query = entityManager.createNamedQuery(EnforcementJob.QUERY_FIND_BY_AGREEMENT_ID,EnforcementJob.class);
        query.setParameter("agreementId", agreementId);

        EnforcementJob result;
        try {
            result = query.getSingleResult();

        } catch (NoResultException e) {
            logger.debug("Null will returned due to no Result found: " + e);
            return null;
        }

        return result;
    }

    @Override
    public IEnforcementJob save(IEnforcementJob enforcementJob) {

        entityManager.persist(enforcementJob);
        entityManager.flush();

        return enforcementJob;
    }

    @Override
    public List<IEnforcementJob> getAll() {
        TypedQuery<IEnforcementJob> query = entityManager.createNamedQuery(    "EnforcementJob.findAll", IEnforcementJob.class);

        List<IEnforcementJob> enforcementJob = new ArrayList<IEnforcementJob>();
        enforcementJob = query.getResultList();

        if (enforcementJob != null) {
            logger.debug("Number of enforcementJob:" + enforcementJob.size());
        } else {
            logger.debug("No Result found.");
        }

        return enforcementJob;
    }

    @Override
    public boolean delete(IEnforcementJob enforcementJob) {
        try {
            enforcementJob = entityManager.getReference(EnforcementJob.class,
                    enforcementJob.getId());
            entityManager.remove(enforcementJob);
            entityManager.flush();
            return true;
        } catch (EntityNotFoundException e) {
            logger.debug("enforcement[{}] not found", enforcementJob.getId());
            return false;
        }
    }

}
