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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IBreachDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IBreach;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.Breach;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.datamodel.bean.Template;
import eu.atos.sla.datamodel.bean.Violation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class BreachDAOJpaTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	IBreachDAO breachDAO;

	@Autowired
	IProviderDAO providerDAO;

	@Autowired
	ITemplateDAO templateDAO;

	@Autowired
	IAgreementDAO agreementDAO;

	@Test
	public void notNull() {
		if (breachDAO == null)
			fail();
	}

	@Test
	public void save() {

		String contractUUID = UUID.randomUUID().toString();

		IViolation violation = new Violation();
		violation.setActualValue("value 1");
		violation.setContractUuid(contractUUID);
		violation.setDatetime(new Date(2323));
		violation.setExpectedValue("expected value 1");

		IBreach breach = new Breach();
		breach.setKpiName("metric name");
		breach.setDatetime(new Date(12345));
		breach.setValue("6.0");

		breach.setAgreementUuid(contractUUID);

		IBreach breachSaved = new Breach();
		try {
			breachSaved = breachDAO.save(breach);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertEquals("6.0", breachSaved.getValue());
		assertEquals(new Date(12345), breachSaved.getDatetime());
	}

	@Test
	public void getById() {
		int size = breachDAO.getAll().size();

		String contractUUID = UUID.randomUUID().toString();

		Violation violation = new Violation();
		violation.setActualValue("44");
		violation.setContractUuid(contractUUID);
		violation.setDatetime(new Date(2323));
		violation.setExpectedValue("55");

		Breach breach = new Breach();
		breach.setKpiName("breach name");
		breach.setDatetime(new Date(12345));
		breach.setValue("8.0");

		@SuppressWarnings("unused")
		IBreach breachSaved = new Breach();
		try {
			breachSaved = breachDAO.save(breach);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IBreach breachFromDatabase = breachDAO.getAll().get(size);
		Long id = breachFromDatabase.getId();
		breachFromDatabase = breachDAO.getById(id);

		assertEquals("breach name", breachFromDatabase.getKpiName());
		assertEquals(new Date(12345), breachFromDatabase.getDatetime());
		assertEquals("8.0", breachFromDatabase.getValue());

		IBreach nullBreach = breachDAO.getById(new Long(30000));
		assertEquals(null, nullBreach);
	}

	@Test
	public void delete() {

		String contractUUID = UUID.randomUUID().toString();

		Violation violation = new Violation();
		violation.setActualValue("65");
		violation.setContractUuid(contractUUID);
		violation.setDatetime(new Date(2323));
		violation.setExpectedValue("90");

		IBreach breach = new Breach();
		breach.setKpiName("uptime");
		// breach.setViolation(violation);
		breach.setDatetime(new Date(12345));
		breach.setValue("9.0");

		IBreach breachSaved = new Breach();
		try {
			breachSaved = breachDAO.save(breach);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (breachSaved != null) {

			boolean deleted = breachDAO.delete(breachSaved);
			assertTrue(deleted);

			deleted = breachDAO.delete(breachSaved);
			assertTrue(!deleted);
		} else {
			fail();
		}
		breachSaved = breachDAO.getById(new Long(223232));
		assertEquals(null, breachSaved);
	}

	@Test
	public void update() {

		String contractUUID = UUID.randomUUID().toString();

		Violation violation = new Violation();
		violation.setActualValue("6");
		violation.setContractUuid(contractUUID);
		violation.setDatetime(new Date(2323));
		violation.setExpectedValue("10");

		Breach breach = new Breach();
		breach.setKpiName("response time");
		// breach.setViolation(violation);
		breach.setDatetime(new Date(12345));
		breach.setValue("3");
		breach.setAgreementUuid(contractUUID);

		IBreach breachSaved = new Breach();
		try {
			breachSaved = breachDAO.save(breach);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertEquals("3", breachSaved.getValue());
		assertEquals(new Date(12345), breachSaved.getDatetime());
		assertEquals(contractUUID, breachSaved.getAgreementUuid());

		String new_contractUUID = UUID.randomUUID().toString();
		breachSaved.setAgreementUuid(new_contractUUID);

		boolean updated = breachDAO.update(breachSaved);
		assertTrue(updated);

	}

	@Test
	public void getByTimeRange() {

		String contractUUID = UUID.randomUUID().toString();

		IAgreement agreement = new Agreement();
		agreement.setAgreementId(contractUUID);

		IAgreement agreementSaved = new Agreement();
		Provider provider = new Provider();
		provider.setName("provider01");
		provider.setUuid("providerUUID");
		Template template = new Template();
		template.setServiceId("serviceid");
		template.setText("text");
		template.setUuid("serviceUUID");
		template.setProvider(provider);
		agreement.setProvider(provider);
		agreement.setTemplate(template);
		try {
			providerDAO.save(provider);
			templateDAO.save(template);
			agreementSaved = agreementDAO.save(agreement);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IBreach breach = new Breach();
		breach.setKpiName("availability");
		breach.setValue("9.0");
		breach.setDatetime(new Date(25000L));
		breach.setAgreementUuid(agreementSaved.getAgreementId());

		@SuppressWarnings("unused")
		IBreach breachSaved = new Breach();

		try {
			breachSaved = breachDAO.save(breach);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		IBreach breach2 = new Breach();
		breach2.setKpiName("availability");
		breach2.setValue("8.0");
		breach2.setDatetime(new Date(20000L));
		breach2.setAgreementUuid(agreementSaved.getAgreementId());

		@SuppressWarnings("unused")
		IBreach breachSaved2 = new Breach();
		try {
			breachSaved2 = breachDAO.save(breach2);
		} catch (Exception e) {
			fail();
		}
		List<IBreach> listBreaches = new ArrayList<IBreach>();

		Date begin = new Date(10000L);
		Date end = new Date(30000L);
		
		// varible,begin,end are not provided
		listBreaches = breachDAO.getByTimeRange(agreement, null, null, null);

		assertEquals(2, listBreaches.size());

		// begin and variable are not provided
		listBreaches = breachDAO.getByTimeRange(agreement, null, null, end);

		assertEquals(2, listBreaches.size());
		
		// variable,end are not provided
		listBreaches = breachDAO.getByTimeRange(agreement, null, begin, null);

		assertEquals(2, listBreaches.size());

		// variable is not provided
		listBreaches = breachDAO.getByTimeRange(agreement, null, begin, end);

		assertEquals(2, listBreaches.size());


		// begin, end are not provided
		listBreaches = breachDAO.getByTimeRange(agreement, "availability",null, null);

		assertEquals(2, listBreaches.size());

		// begin is not provided
		listBreaches = breachDAO.getByTimeRange(agreement, "availability",null, end);

		assertEquals(2, listBreaches.size());

		// end is not provided
		listBreaches = breachDAO.getByTimeRange(agreement, "availability",begin, null);

		assertEquals(2, listBreaches.size());

		listBreaches = breachDAO.getByTimeRange(agreement, "availability",begin, end);

		assertEquals(2, listBreaches.size());

	}

}
