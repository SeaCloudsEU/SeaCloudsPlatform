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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IPolicyDAO;
import eu.atos.sla.datamodel.IPolicy;
import eu.atos.sla.datamodel.bean.Policy;

@Repository("PolicyRepository")
public class PolicyDAOJpa implements IPolicyDAO {
	private static Logger logger = LoggerFactory.getLogger(PolicyDAOJpa.class);
	private EntityManager entityManager;

	@PersistenceContext(unitName = "slarepositoryDB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public Policy getById(Long id) {
		return entityManager.find(Policy.class, id);
	}

	@Override
	public List<IPolicy> getAll() {
		TypedQuery<IPolicy> query = entityManager.createNamedQuery(
				"Policy.findAll", IPolicy.class);
		List<IPolicy> policys = null;
		policys = query.getResultList();

		if (policys != null) {
			logger.debug("Number of policys:" + policys.size());
		} else {
			logger.debug("No Result found.");
		}

		return policys;
	}

	@Override
	public IPolicy save(IPolicy policy) {

		entityManager.persist(policy);
		entityManager.flush();

		return policy;
	}

	@Override
	public boolean update(IPolicy policy) {
		entityManager.merge(policy);
		entityManager.flush();
		return true;
	}

	@Override
	public boolean delete(IPolicy policy) {
		Long id = policy.getId();
		try {
			policy = entityManager.getReference(Policy.class, id);
			entityManager.remove(policy);
			entityManager.flush();
			return true;
		} catch (EntityNotFoundException e) {
			logger.debug("Policy[{}] not found", id);
			return false;
		}
	}

}
