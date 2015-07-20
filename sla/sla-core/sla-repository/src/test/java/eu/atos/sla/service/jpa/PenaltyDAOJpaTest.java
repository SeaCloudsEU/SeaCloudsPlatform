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
package eu.atos.sla.service.jpa;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IPenaltyDAO;
import eu.atos.sla.dao.IPenaltyDAO.SearchParameters;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Penalty;
import eu.atos.sla.datamodel.bean.PenaltyDefinition;
import eu.atos.sla.datamodel.bean.Violation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class PenaltyDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    private EntityManager entityManager;
    
    @Autowired
    IPenaltyDAO penaltyDao;

    @Autowired
    IAgreementDAO agreementDAO;

    @Autowired
    IViolationDAO violationDao;
    
    @PersistenceContext(unitName = "slarepositoryDB")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Test
    public void getById() {

        Date dateTime = new Date();
        
        IPenaltyDefinition def = new PenaltyDefinition(1, "euro", "100");
        savePenaltyDefinition(def);
        IViolation violation = new Violation();
        violationDao.save(violation);
        
        Penalty expected = new Penalty("agreement-id", dateTime, "kpiname", def, violation);

        IPenalty saved = penaltyDao.save(expected);

        IPenalty actual = penaltyDao.getById(saved.getId());

        /*
         * All these should succeed, as expected and actual are the same object after save()
         */
        assertEquals(expected.getAgreementId(), actual.getAgreementId());
        assertEquals(expected.getDatetime().getTime(), actual.getDatetime().getTime());
        assertEquals(expected.getKpiName(), actual.getKpiName());
        assertEquals(expected.getUuid(), actual.getUuid());

        Assert.assertTrue(expected == actual);
    }

    private void savePenaltyDefinition(IPenaltyDefinition def) {
        entityManager.persist(def);
        entityManager.flush();
    }
    
    @Test
    public void testSearch() {
        
        SearchParameters params = new IPenaltyDAO.SearchParameters();
        params.setAgreementId(null);
        params.setGuaranteeTermName(null);
        params.setBegin(null);
        params.setEnd(null);
        
        penaltyDao.search(params);
    }
}
