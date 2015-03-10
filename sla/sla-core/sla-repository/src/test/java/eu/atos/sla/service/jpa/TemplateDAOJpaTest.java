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
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.datamodel.bean.Template;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class TemplateDAOJpaTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	final static String PROVIDER_UUID = "provider10";
	
	@Autowired
	ITemplateDAO templateDAO;

	@Autowired
	IProviderDAO providerDAO;
	

	@Test
	public void notNull() {
		if (templateDAO == null)
			fail();
	}

	@Test
	public void save() {

		String templateUuid = UUID.randomUUID().toString();
		IProvider psaved = new Provider();
		try {
			IProvider provider = new Provider();
			provider.setName(PROVIDER_UUID);
			provider.setUuid(PROVIDER_UUID);
			psaved = providerDAO.save(provider);
		} catch (Exception e) {
			//it might be in the db and fail
			psaved = providerDAO.getByUUID(PROVIDER_UUID);
		}		

		ITemplate template = new Template();
		template.setText("Template name 1");
		template.setUuid(templateUuid);
		template.setServiceId("serviceTest");
		template.setProvider(psaved);

		
		
		@SuppressWarnings("unused")
		ITemplate saved = new Template();
		try {
			saved = templateDAO.save(template);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void getById() {
		@SuppressWarnings("unused")
		int size = templateDAO.getAll().size();

		String templateUuid = UUID.randomUUID().toString();

		IProvider psaved = new Provider();
		try {
			IProvider provider = new Provider();
			provider.setName(PROVIDER_UUID);
			provider.setUuid(PROVIDER_UUID);
			psaved = providerDAO.save(provider);
		} catch (Exception e) {
			//it might be in the db and fail
			psaved = providerDAO.getByUUID(PROVIDER_UUID);
		}		
		
		
		Template template = new Template();
		template.setText("Template name 1");
		template.setUuid(templateUuid);
		template.setServiceId("serviceTest");
		template.setProvider(psaved);

		ITemplate saved = new Template();
		try {
			saved = templateDAO.save(template);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(templateUuid, saved.getUuid());
		assertEquals("Template name 1", saved.getText());
		assertEquals(templateUuid, saved.getUuid());

		ITemplate nullTemplate = templateDAO.getById(new Long(30000));
		assertEquals(null, nullTemplate);

	}

	@Test
	public void detete() {

		String uuid = UUID.randomUUID().toString();
		
		IProvider psaved = new Provider();
		try {
			IProvider provider = new Provider();
			provider.setName(PROVIDER_UUID);
			psaved = providerDAO.save(provider);
		} catch (Exception e) {
			//it might be in the db and fail
			psaved = providerDAO.getByUUID(PROVIDER_UUID);
		}		
		
		ITemplate template = new Template();
		template.setUuid(uuid);
		template.setText("template text");
		template.setServiceId("serviceTest");
		template.setProvider(psaved);

		ITemplate saved = new Template();
		try {
			saved = templateDAO.save(template);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		Long id = saved.getId();
		ITemplate templateFromDatabase = templateDAO.getById(id);
		boolean deleted = templateDAO.delete(templateFromDatabase);
		assertTrue(deleted);

		deleted = templateDAO.delete(templateFromDatabase);
		assertTrue(!deleted);

		
		try {
			providerDAO.delete(psaved);
		} catch (Exception e) {
		}		
		templateFromDatabase = templateDAO.getById(id);
		assertEquals(null, templateFromDatabase);
	}

	@Test
	public void update() {

		int size = templateDAO.getAll().size();
		String uuid = UUID.randomUUID().toString();

		IProvider psaved = new Provider();
		IProvider provider = new Provider();
		provider.setName(PROVIDER_UUID);
		try {
			psaved = providerDAO.save(provider);
		} catch (Exception e) {
			//it might be in the db and fail
			psaved = providerDAO.getByUUID(PROVIDER_UUID);
		}		

		Template template = new Template();
		template.setText("template text");
		template.setServiceId("serviceTest");
		template.setUuid(uuid);
		template.setProvider(psaved);


		@SuppressWarnings("unused")
		ITemplate saved = new Template();
		try {
			saved = templateDAO.save(template);
		} catch (Exception e) {
			fail();
		}

		ITemplate templateFromDatabase = templateDAO.getAll().get(size);
		Long id = templateFromDatabase.getId();
		assertEquals("template text", templateFromDatabase.getText());
		assertEquals(uuid, templateFromDatabase.getUuid());

		templateFromDatabase.setText("text updated");
		boolean updated = templateDAO.update(uuid, templateFromDatabase);
		assertTrue(updated);

		templateFromDatabase = templateDAO.getById(id);
		assertEquals("text updated", templateFromDatabase.getText());

	}

}
