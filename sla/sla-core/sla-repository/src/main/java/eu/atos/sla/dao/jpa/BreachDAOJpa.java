/**
 * Copyright 2015 SeaClouds
 * Contact: SeaClouds
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IBreachDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IBreach;
import eu.atos.sla.datamodel.bean.Breach;

@Repository("BreachRepository")
public class BreachDAOJpa implements IBreachDAO {
	private static Logger logger = LoggerFactory.getLogger(BreachDAOJpa.class);
	private EntityManager entityManager;

	@PersistenceContext(unitName = "slarepositoryDB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Breach getById(Long id) {
		return entityManager.find(Breach.class, id);
	}

	public Breach getBreachByUUID(UUID uuid) {
		try {
			Query query = entityManager
					.createNamedQuery(Breach.QUERY_FIND_BY_UUID);
			query.setParameter("uuid", uuid);
			Breach breach = null;

			breach = (Breach) query.getSingleResult();

			return breach;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	public List<IBreach> getAll() {
		TypedQuery<IBreach> query = entityManager.createNamedQuery(
				Breach.QUERY_FIND_ALL, IBreach.class);
		List<IBreach> breaches = null;
		breaches = query.getResultList();

		if (breaches != null) {
			logger.debug("Number of active breaches:" + breaches.size());
		} else {
			logger.debug("No Result found.");
		}

		return breaches;
	}

	public IBreach save(IBreach breach) {

		entityManager.persist(breach);
		entityManager.flush();

		return breach;

	}

	public boolean update(IBreach breach) {
		entityManager.merge(breach);
		entityManager.flush();
		return true;
	}

	public boolean delete(IBreach breach) {
		Long id = breach.getId();
		try {
			breach = entityManager.getReference(Breach.class, id);
			entityManager.remove(breach);
			entityManager.flush();
			return true;
		} catch (EntityNotFoundException e) {
			logger.debug("breach[{}] not found", id);
			return false;
		}
	}

	public List<IBreach> getByTimeRange(IAgreement contract, String variable,
			Date begin, Date end) {
		TypedQuery<IBreach> query = entityManager.createNamedQuery(
				Breach.QUERY_FIND_BY_TIME_RANGE, IBreach.class);
		query.setParameter("uuid", contract.getAgreementId());
		query.setParameter("variable", variable);
		query.setParameter("begin", begin);
		query.setParameter("end", end);
		
	
		List<IBreach> breaches = null;
		breaches = query.getResultList();

		if (breaches != null) {
			logger.debug("Number of breaches given a contract and range of dates:" + breaches.size());
		} else {
			logger.debug("No Result found.");
		}

		return breaches;

	}

}
