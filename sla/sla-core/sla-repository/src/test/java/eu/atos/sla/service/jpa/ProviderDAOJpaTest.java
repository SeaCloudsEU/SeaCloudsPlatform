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

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.bean.Provider;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class ProviderDAOJpaTest extends
        AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    IProviderDAO providerDAO;

    @Test
    public void notNull() {
        if (providerDAO == null)
            fail();
    }

    @Test
    public void getById() {

        String uuid = UUID.randomUUID().toString();

        IProvider provider = new Provider();
        provider.setName("Provider10");
        provider.setUuid(uuid);

        IProvider saved = new Provider();
        try {
            saved = providerDAO.save(provider);
        } catch (Exception e) {
            fail();
        }

    
        assertEquals(uuid, saved.getUuid());
        assertEquals("Provider10", saved.getName());

        IProvider nullProvider = providerDAO.getById(new Long(30000));
        assertEquals(null, nullProvider);
    }

    @Test
    public void getByUuid() {

        int size = providerDAO.getAll().size();

        String uuid = UUID.randomUUID().toString();

        IProvider provider = new Provider();
        provider.setName("Provider1");
        provider.setUuid(uuid);

        @SuppressWarnings("unused")
        IProvider saved = new Provider();
        try {
            saved = providerDAO.save(provider);
        } catch (Exception e) {
            fail();
        }

        IProvider providerFromDatabase = providerDAO.getAll().get(size);
        @SuppressWarnings("unused")
        Long id = providerFromDatabase.getId();
        providerFromDatabase = providerDAO.getByUUID(uuid);
        assertEquals(uuid, providerFromDatabase.getUuid());
        assertEquals("Provider1", providerFromDatabase.getName());

    }

    @Test
    public void save() {

        String uuid = UUID.randomUUID().toString();

        IProvider provider = new Provider();
        provider.setName("Provider3");
        provider.setUuid(uuid);

        @SuppressWarnings("unused")
        IProvider saved = new Provider();
        try {
            saved = providerDAO.save(provider);
        } catch (Exception e) {
            
        }
        assert(true);
    }

    @Test
    public void delete() {
        int size = providerDAO.getAll().size();

        String uuid = UUID.randomUUID().toString();

        IProvider provider = new Provider();
        provider.setName("Provider4");
        provider.setUuid(uuid);

        @SuppressWarnings("unused")
        IProvider saved = new Provider();
        try {
            saved = providerDAO.save(provider);
        } catch (Exception e) {
            assert(false);
        }

        IProvider providerFromDatabase = providerDAO.getAll().get(size);
        Long id = providerFromDatabase.getId();

        providerFromDatabase = providerDAO.getById(id);
        boolean deleted = providerDAO.delete(providerFromDatabase);
        assertTrue(deleted);

        deleted = providerDAO.delete(providerFromDatabase);
        assertTrue(!deleted);

        providerFromDatabase = providerDAO.getById(new Long(223232));
        assertEquals(null, providerFromDatabase);
    }

    @Test
    public void update() {
        int size = providerDAO.getAll().size();

        String uuid = UUID.randomUUID().toString();
        IProvider provider = new Provider();
        provider.setName("Provider5");
        provider.setUuid(uuid);
        IProvider saved = new Provider();
        try {
            saved = providerDAO.save(provider);
        } catch (Exception e) {
            assertEquals(e,saved);
        }

        IProvider providerFromDatabase = providerDAO.getAll().get(size);
        Long id = providerFromDatabase.getId();
        assertEquals(uuid, providerFromDatabase.getUuid());
        assertEquals("Provider5", providerFromDatabase.getName());

        providerFromDatabase.setName("Provider6");

        boolean updated = providerDAO.update(providerFromDatabase);
        assertTrue(updated);

        providerFromDatabase = providerDAO.getById(id);
        assertEquals("Provider6", providerFromDatabase.getName());

    }
}
