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

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IPolicyDAO;
import eu.atos.sla.datamodel.IPolicy;
import eu.atos.sla.datamodel.bean.Policy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class PolicyDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    IPolicyDAO policyDAO;

    @Test
    public void notNull() {
        if (policyDAO == null)
            fail();
    }

    @Test
    public void save() {

        IPolicy policy = new Policy();
        policy.setCount(new Integer(2323));
        policy.setTimeInterval(new Date(12345));

        @SuppressWarnings("unused")
        IPolicy saved=new Policy();
        try {
            saved = policyDAO.save(policy);
        } catch (Exception e) {
            fail();
        }
        assertEquals(new Integer(2323), policy.getCount());
        assertEquals(new Date(12345), policy.getTimeInterval());

    }

    @Test
    public void getById() {

        int size = policyDAO.getAll().size();

        Policy policy = new Policy();
        policy.setCount(new Integer(2323));
        policy.setTimeInterval(new Date(1234));

        @SuppressWarnings("unused")
        IPolicy saved = new Policy();
        try {
            saved = policyDAO.save(policy);
        } catch (Exception e) {
            fail();
        }

        IPolicy guaranteeFromDatabase = policyDAO.getAll().get(size);
        Long id = guaranteeFromDatabase.getId();
        guaranteeFromDatabase = policyDAO.getById(id);

        assertEquals(new Integer(2323), policy.getCount());
        assertEquals(new Date(1234), policy.getTimeInterval());

        IPolicy nullPolicy = policyDAO.getById(new Long(30000));
        assertEquals(null, nullPolicy);

    }

    @Test
    public void detete() {

        int size = policyDAO.getAll().size();
        @SuppressWarnings("unused")
        String uuid = UUID.randomUUID().toString();
        IPolicy policy = new Policy();
        policy.setCount(new Integer(2323));
        policy.setTimeInterval(new Date(1234));

        @SuppressWarnings("unused")
        IPolicy saved= new Policy();
        try {
            saved = policyDAO.save(policy);
        } catch (Exception e) {
            fail();
        }

        IPolicy policyFromDatabase = policyDAO.getAll().get(size);
        Long id = policyFromDatabase.getId();

        policyFromDatabase = policyDAO.getById(id);
        boolean deleted = policyDAO.delete(policyFromDatabase);
        assertTrue(deleted);

        deleted = policyDAO.delete(policyFromDatabase);
        assertTrue(!deleted);

        policyFromDatabase = policyDAO.getById(new Long(223232));
        assertEquals(null, policyFromDatabase);
    }

    @Test
    public void update() {
        int size = policyDAO.getAll().size();

        Policy policy = new Policy();
        policy.setCount(new Integer(2323));
        policy.setTimeInterval(new Date(1234));

        @SuppressWarnings("unused")
        IPolicy saved=new Policy();
        try {
            saved = policyDAO.save(policy);
        } catch (Exception e) {
            fail();
        }

        IPolicy policyFromDatabase = policyDAO.getAll().get(size);
        Long id = policyFromDatabase.getId();
        assertEquals(new Integer(2323), policy.getCount());
        assertEquals(new Date(1234), policy.getTimeInterval());
        policyFromDatabase.setCount(new Integer(2324));
        policyFromDatabase.setTimeInterval(new Date(5678));
        boolean updated = policyDAO.update(policyFromDatabase);
        assertTrue(updated);

        policyFromDatabase = policyDAO.getById(id);
        assertEquals(new Integer(2324), policy.getCount());
        assertEquals(new Date(5678), policy.getTimeInterval());

    }

}
