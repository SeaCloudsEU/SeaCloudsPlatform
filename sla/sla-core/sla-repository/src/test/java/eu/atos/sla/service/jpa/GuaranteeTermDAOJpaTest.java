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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.jpa.GuaranteeTermDAOJpa;
import eu.atos.sla.datamodel.IBreach;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.bean.Breach;
import eu.atos.sla.datamodel.bean.BusinessValueList;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.datamodel.bean.PenaltyDefinition;
import eu.atos.sla.datamodel.bean.Violation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class GuaranteeTermDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    IGuaranteeTermDAO guaranteeTermDAO;

    @Test
    public void notNull() {
        if (guaranteeTermDAO == null)
            fail();
    }

    @Test
    public void save() {

        String contractUUID = UUID.randomUUID().toString();

        Violation violation = new Violation();
        violation.setActualValue("value 1");
        violation.setContractUuid(contractUUID);
        violation.setDatetime(new Date(2323));
        violation.setExpectedValue("expected value 2");

        Breach breach = new Breach();
        breach.setKpiName("metric name");
        breach.setDatetime(new Date(12345));
        breach.setValue("6.0");
        breach.setAgreementUuid(contractUUID);

        String contractUUID2 = UUID.randomUUID().toString();

        IBreach breach2 = new Breach();
        breach2.setKpiName("metric name 2");
        breach2.setDatetime(new Date(12325));
        breach2.setValue("7.0");
        breach2.setAgreementUuid(contractUUID2);

        List<IBreach> breaches = new ArrayList<IBreach>();
        breaches.add(breach);
        breaches.add(breach2);

        IGuaranteeTerm guaranteeTerm = new GuaranteeTerm();
        guaranteeTerm.setName("name guarantee term");
//        guaranteeTerm.setBreaches(breaches);
        
        
        IPenaltyDefinition penalty = newPenalty(1, "%", "10");
        List<IPenaltyDefinition> compensations = Collections.singletonList(penalty);
        BusinessValueList bvl = new BusinessValueList(1, compensations);
        guaranteeTerm.setBusinessValueList(bvl);

        IGuaranteeTerm saved;
        saved = guaranteeTermDAO.save(guaranteeTerm);

        ((GuaranteeTermDAOJpa)guaranteeTermDAO).getEntityManager().detach(guaranteeTerm);
        
        IGuaranteeTerm loaded = guaranteeTermDAO.getById(saved.getId());
        
        assertEquals(guaranteeTerm.getKpiName(), loaded.getKpiName());
        assertEquals(guaranteeTerm.getName(), loaded.getName());
        assertEquals(guaranteeTerm.getServiceLevel(), loaded.getServiceLevel());
        assertEquals(guaranteeTerm.getServiceName(), loaded.getServiceScope());
        assertEquals(guaranteeTerm.getServiceScope(), loaded.getServiceScope());
        
        assertEquals(guaranteeTerm.getBusinessValueList(), loaded.getBusinessValueList());

    }

    @Test
    public void getById() {

        int size = guaranteeTermDAO.getAll().size();

        GuaranteeTerm guaranteeTerm = new GuaranteeTerm();

        guaranteeTerm.setName("guarantee term name");
        guaranteeTerm.setServiceName("service Name");

        @SuppressWarnings("unused")
        IGuaranteeTerm saved;
        try {
            saved = guaranteeTermDAO.save(guaranteeTerm);
        } catch (Exception e) {
            fail();
        }

        IGuaranteeTerm guaranteeFromDatabase = guaranteeTermDAO.getAll().get(
                size);
        Long id = guaranteeFromDatabase.getId();
        guaranteeFromDatabase = guaranteeTermDAO.getById(id);

        assertEquals("guarantee term name", guaranteeFromDatabase.getName());
        assertEquals("service Name", guaranteeFromDatabase.getServiceName());

        IGuaranteeTerm nullBreach = guaranteeTermDAO.getById(new Long(30000));
        assertEquals(null, nullBreach);

    }

    @Test
    public void detete() {

        @SuppressWarnings("unused")
        int size = guaranteeTermDAO.getAll().size();

        String contractUUID = UUID.randomUUID().toString();
        Violation violation = new Violation();
        violation.setActualValue("value 1");
        violation.setContractUuid(contractUUID);
        violation.setDatetime(new Date(2323));
        violation.setExpectedValue("expected value 2");

        Breach breach = new Breach();
        breach.setKpiName("metric name");
        breach.setDatetime(new Date(12345));
        breach.setValue("6.0");
        breach.setAgreementUuid(contractUUID);

        List<IBreach> breaches = new ArrayList<IBreach>();
        breaches.add(breach);

        GuaranteeTerm guaranteeTerm = new GuaranteeTerm();
        guaranteeTerm.setName("name guarantee term");
//        guaranteeTerm.setBreaches(breaches);
        guaranteeTerm.setServiceName("uptime sensors");

        IGuaranteeTerm saved = null;
        try {
            saved = guaranteeTermDAO.save(guaranteeTerm);
        } catch (Exception e) {
            fail();
        }

        boolean deleted = guaranteeTermDAO.delete(saved);
        assertTrue(deleted);

        deleted = guaranteeTermDAO.delete(saved);
        assertTrue(!deleted);

        saved = guaranteeTermDAO.getById(new Long(223232));
        assertEquals(null, saved);

    }

    @Test
    public void update() {

        int size = guaranteeTermDAO.getAll().size();

        @SuppressWarnings("unused")
        String uuid = UUID.randomUUID().toString();

        String contractUUID = UUID.randomUUID().toString();

        Violation violation = new Violation();
        violation.setActualValue("value 1");
        violation.setContractUuid(contractUUID);
        violation.setDatetime(new Date(2323));
        violation.setExpectedValue("expected value 2");

        Breach breach = new Breach();
        breach.setKpiName("metric name");
        breach.setDatetime(new Date(12345));
        breach.setValue("6.0");
        breach.setAgreementUuid(contractUUID);

        List<IBreach> breaches = new ArrayList<IBreach>();
        breaches.add(breach);

        IGuaranteeTerm guaranteeTerm = new GuaranteeTerm();
        guaranteeTerm.setName("name guarantee term");
//        guaranteeTerm.setBreaches(breaches);

        @SuppressWarnings("unused")
        IGuaranteeTerm saved;
        try {
            saved = guaranteeTermDAO.save(guaranteeTerm);
        } catch (Exception e) {
            fail();
        }

        IGuaranteeTerm guaranteeTermFromDatabase = guaranteeTermDAO.getAll()
                .get(size);
        Long id = guaranteeTermFromDatabase.getId();
        assertEquals("name guarantee term", guaranteeTermFromDatabase.getName());
        guaranteeTermFromDatabase.setName("name updated");
        boolean updated = guaranteeTermDAO.update(guaranteeTermFromDatabase);
        assertTrue(updated);

        guaranteeTermFromDatabase = guaranteeTermDAO.getById(id);
        assertEquals("name updated", guaranteeTermFromDatabase.getName());

    }
    
    private IPenaltyDefinition newPenalty(
            int count, String valueUnit, String valueExpression) {
        
        IPenaltyDefinition result;
        result = new PenaltyDefinition(count, valueUnit, valueExpression);
        return result;
    }

}
