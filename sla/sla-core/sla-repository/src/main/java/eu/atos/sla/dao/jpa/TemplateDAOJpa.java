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
import java.util.Arrays;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.bean.Template;

@Repository("TemplateRepository")
public class TemplateDAOJpa implements ITemplateDAO {
    private static Logger logger = LoggerFactory.getLogger(TemplateDAOJpa.class);
    private EntityManager entityManager;

    @PersistenceContext(unitName = "slarepositoryDB")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Template getById(Long id) {
        return entityManager.find(Template.class, id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Template getByUuid(String uuid) {
        try {
            Query query = entityManager
                    .createNamedQuery(Template.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            Template template = null;
            template = (Template) query.getSingleResult();
            return template;
        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
            return null;
        }
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<ITemplate> search(String providerId, String []serviceIds) {
        TypedQuery<ITemplate> query = entityManager.createNamedQuery(
                Template.QUERY_SEARCH, ITemplate.class);
        query.setParameter("providerId", providerId);
        query.setParameter("serviceIds", (serviceIds!=null)?Arrays.asList(serviceIds):null);
        query.setParameter("flagServiceIds", (serviceIds!=null)?"flag":null);
        logger.debug("providerId:{} - serviceIds:{}" , providerId, (serviceIds!=null)?Arrays.asList(serviceIds):null);
        List<ITemplate> templates = new ArrayList<ITemplate>();
        templates = (List<ITemplate>) query.getResultList();

        if (templates != null) {
            logger.debug("Number of templates:" + templates.size());
        } else {
            logger.debug("No Result found.");
        }

        return templates;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<ITemplate> getByAgreement(String agreement) {

        TypedQuery<ITemplate> query = entityManager.createNamedQuery(
                Template.QUERY_FIND_BY_AGREEMENT, ITemplate.class);
        query.setParameter("agreement", agreement);
        List<ITemplate> templates = new ArrayList<ITemplate>();
        templates = (List<ITemplate>) query.getResultList();

        if (templates != null) {
            logger.debug("Number of templates:" + templates.size());
        } else {
            logger.debug("No Result found.");
        }

        return templates;
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<ITemplate> getAll() {

        TypedQuery<ITemplate> query = entityManager.createNamedQuery(
                Template.QUERY_FIND_ALL, ITemplate.class);
        List<ITemplate> templates = new ArrayList<ITemplate>();
        templates = (List<ITemplate>) query.getResultList();
        if (templates != null) {
            logger.debug("Number of templates:" + templates.size());
        } else {
            logger.debug("No Result found.");
        }

        return templates;

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ITemplate save(ITemplate template) {
        logger.info("template.getUuid() "+template.getUuid());
        entityManager.persist(template);
        entityManager.flush();
        return template;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(String uuid, ITemplate template) {
        Template templateDB = null;
        try {
            Query query = entityManager.createNamedQuery(Template.QUERY_FIND_BY_UUID);
            query.setParameter("uuid", uuid);
            templateDB = (Template)query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("No Result found: " + e);
        }
        
        if (templateDB!=null){
            template.setId(templateDB.getId());
            logger.info("template to update with id"+template.getId());        
            entityManager.merge(template);
            entityManager.flush();
        }else
            return false;
        return true;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean delete(ITemplate template) {
        try {
            Template templateDeleted = entityManager.getReference(Template.class, template.getId());
            entityManager.remove(templateDeleted);
            entityManager.flush();
            return true;
        } catch (EntityNotFoundException e) {
            logger.debug("Template[{}] not found", template.getId());
            return false;
        }
    }

}
