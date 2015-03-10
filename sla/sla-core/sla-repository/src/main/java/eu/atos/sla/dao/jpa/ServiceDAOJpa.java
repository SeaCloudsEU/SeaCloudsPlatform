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

import java.util.ArrayList;
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

import eu.atos.sla.dao.IServiceDAO;
import eu.atos.sla.datamodel.IService;
import eu.atos.sla.datamodel.bean.Service;

@Repository("ServiceRepository")
public class ServiceDAOJpa implements IServiceDAO {
	private static Logger logger = LoggerFactory.getLogger(ServiceDAOJpa.class);
	private EntityManager entityManager;

	@PersistenceContext(unitName = "slarepositoryDB")
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public IService getById(Long id) {
		return entityManager.find(Service.class, id);
	}

	@Override
	public IService getByUUID(String uuid) {
		try {
			Query query = entityManager
					.createNamedQuery(Service.QUERY_FIND_BY_UUID);
			query.setParameter("uuid", uuid);
			IService service = new Service();

			service = (Service) query.getSingleResult();
			System.out.println("Service name:" + service.getName());

			return service;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	@Override
	public IService getByName(String name) {
		try {
			TypedQuery<IService> query = entityManager.createNamedQuery(
					Service.QUERY_FIND_BY_NAME, IService.class);
			query.setParameter("name", name);
			IService service = new Service();

			service = (Service) query.getSingleResult();
			System.out.println("Service uuid:" + service.getUuid());

			return service;

		} catch (NoResultException e) {
			logger.debug("No Result found: " + e);
			return null;
		}
	}

	@Override
	public List<IService> getAll() {

		TypedQuery<IService> query = entityManager.createNamedQuery(
				Service.QUERY_FIND_ALL, IService.class);
		List<IService> services = new ArrayList<IService>();
		services = query.getResultList();

		if (services != null) {
			logger.debug("Number of services:" + services.size());
		} else {
			logger.debug("No Result found.");
		}

		return services;
	}

	@Override
	public IService save(IService service){
		
			entityManager.persist(service);
			entityManager.flush();
		
		return service;
	}

	@Override
	public boolean update(IService service) {
		entityManager.merge(service);
		entityManager.flush();
		return true;
	}

	@Override
	public boolean delete(IService service) {
		try {
			service = entityManager
					.getReference(Service.class, service.getId());
			entityManager.remove(service);
			entityManager.flush();
			return true;
		} catch (EntityNotFoundException e) {
			logger.debug("Service[{}] not found", service.getId());
			return false;
		}
	}
}
