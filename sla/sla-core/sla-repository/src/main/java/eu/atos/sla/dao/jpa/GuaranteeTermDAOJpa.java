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
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;

@Repository("GuaranteeTermRepository")
public class GuaranteeTermDAOJpa implements IGuaranteeTermDAO {
    private static Logger logger = LoggerFactory.getLogger(GuaranteeTermDAOJpa.class);
    private EntityManager entityManager;

    @PersistenceContext(unitName = "slarepositoryDB")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public GuaranteeTerm getById(Long id) {
        return entityManager.find(GuaranteeTerm.class, id);
    }

    public List<IGuaranteeTerm> getAll() {

        TypedQuery<IGuaranteeTerm> query = entityManager.createNamedQuery(
                "GuaranteeTerm.findAll", IGuaranteeTerm.class);
        List<IGuaranteeTerm> guaranteeTerms = null;
        guaranteeTerms = query.getResultList();

        if (guaranteeTerms != null) {
            logger.debug("Number of guaranteeTerms:" + guaranteeTerms.size());
        } else {
            logger.debug("No Result found.");
        }

        return guaranteeTerms;
    }

    @Override
    public IGuaranteeTerm save(IGuaranteeTerm guaranteeTerm) {

        entityManager.persist(guaranteeTerm);
        entityManager.flush();

        return guaranteeTerm;
    }

    public boolean update(IGuaranteeTerm guaranteeTerm) {
        entityManager.merge(guaranteeTerm);
        entityManager.flush();
        return true;
    }

    public boolean delete(IGuaranteeTerm guaranteeTerm) {
        Long id = guaranteeTerm.getId();
        try {
            guaranteeTerm = entityManager.getReference(GuaranteeTerm.class, id);
            entityManager.remove(guaranteeTerm);
            entityManager.flush();
            return true;
        } catch (EntityNotFoundException e) {
            logger.debug("GuaranteeTerm[{}] not found", id);
            return false;
        }
    }

}
