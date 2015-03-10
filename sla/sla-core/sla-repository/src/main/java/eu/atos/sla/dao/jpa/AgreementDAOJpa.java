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

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.bean.Agreement;

@Repository("AgreementRepository")
public class AgreementDAOJpa implements IAgreementDAO {
	private static Logger logger = LoggerFactory.getLogger(AgreementDAOJpa.class);
	private EntityManager entityManager;

	@PersistenceContext(unitName = "slarepositoryDB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;

	}

	public EntityManager getEntityManager() {
		return entityManager;
	}


	public Agreement getById(Long id) {
		return entityManager.find(Agreement.class, id);
	}


	public IAgreement getByAgreementId(String agreementId) {
		try {
			Query query = entityManager.createNamedQuery(Agreement.QUERY_FIND_BY_AGREEMENT_ID);
			query.setParameter("agreementId", agreementId);
			Agreement agreement = null;

			agreement = (Agreement) query.getSingleResult();

			return agreement;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	
	public List<IAgreement> getByConsumer(String consumerId) {
		TypedQuery<IAgreement> query = entityManager.createNamedQuery(
				Agreement.QUERY_FIND_BY_CONSUMER, IAgreement.class);
		query.setParameter("consumerId", consumerId);
		List<IAgreement> agreements = query.getResultList();

		if (agreements != null) {
			logger.debug("Number of agreements:" + agreements.size());
		} else {
			logger.debug("No Result found.");
		}

		return agreements;
	}
	
	public List<IAgreement> getByActiveAgreements(long date) {

		TypedQuery<IAgreement> query = entityManager.createNamedQuery(
				Agreement.QUERY_ACTIVE_AGREEMENTS, IAgreement.class);
		Date actualDate = new Date(date);
		query.setParameter("actualDate", actualDate);
		List<IAgreement> agreements = query.getResultList();

		if (agreements != null) {
			logger.debug("Number of active agreements:" + agreements.size());
		} else {
			logger.debug("No Result found.");
		}

		return agreements;
	}

	public List<IAgreement> getByTemplate(String templateUUID) {

		TypedQuery<IAgreement> query = entityManager.createNamedQuery(
				Agreement.QUERY_FIND_BY_TEMPLATEUUID, IAgreement.class);
		query.setParameter("templateUUID", templateUUID);
		List<IAgreement> agreements = query.getResultList();

		if (agreements != null) {
			logger.debug("Number of agreement per template "+templateUUID+" :" + agreements.size());
		} else {
			logger.debug("No Result found.");
		}

		return agreements;
	}

	
	public IAgreement save(IAgreement agreement) {

		entityManager.persist(agreement);
		entityManager.flush();

		return agreement;
	}

	
	public boolean delete(IAgreement agreement) {
		try {
			agreement = entityManager.getReference(Agreement.class,
					agreement.getId());
			entityManager.remove(agreement);
			entityManager.flush();
			return true;
		} catch (EntityNotFoundException e) {
			logger.debug("agreement[{}] not found", agreement.getAgreementId());
			return false;
		}
	}


	public List<IAgreement> getAll() {
		TypedQuery<IAgreement> query = entityManager.createNamedQuery(
				Agreement.QUERY_FIND_ALL, IAgreement.class);
		List<IAgreement> agreements = query.getResultList();

		if (agreements != null) {
			logger.debug("Number of agreements:" + agreements.size());
		} else {
			logger.debug("No Result found.");
		}

		return agreements;
	}

	public List<IAgreement> search(String consumerId, String providerId, String templateId, Boolean active) {

		TypedQuery<IAgreement> query = entityManager.createNamedQuery(Agreement.QUERY_SEARCH, IAgreement.class);
		query.setParameter("consumerId", consumerId);
		query.setParameter("providerId", providerId);
		query.setParameter("templateId", templateId);
		query.setParameter("active", active);
		List<IAgreement> agreements = query.getResultList();
		
		return agreements;
	}

	public List<IAgreement> getByProvider(String providerUuid) {

		try {
			TypedQuery<IAgreement> query = entityManager.createNamedQuery(Agreement.QUERY_FIND_BY_PROVIDER, IAgreement.class);
			query.setParameter("providerUuid", providerUuid);
			List<IAgreement> agreements = query.getResultList();
			return agreements;
		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	@Override
	public List<IAgreement> searchPerTemplateAndConsumer(String consumerId,	String templateUUID) {
		try {
			TypedQuery<IAgreement> query = entityManager.createNamedQuery(Agreement.QUERY_FIND_BY_TEMPLATEUUID_AND_CONSUMER, IAgreement.class);
			query.setParameter("consumerId", consumerId);
			query.setParameter("templateUUID", templateUUID);
			List<IAgreement> agreements = query.getResultList();
			return agreements;
		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}
	
}
