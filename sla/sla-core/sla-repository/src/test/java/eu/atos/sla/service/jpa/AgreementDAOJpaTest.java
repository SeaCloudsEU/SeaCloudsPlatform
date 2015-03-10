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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.datamodel.bean.Template;
import eu.atos.sla.datamodel.bean.Violation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-repository-db-JPA-test-context.xml")
public class AgreementDAOJpaTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	IAgreementDAO agreementDAO;

	@Test
	public void notNull() {
		if (agreementDAO == null)
			fail();
	}

	@Test
	public void getById() {

		String uuid = UUID.randomUUID().toString();

		StringBuilder agreementText = new StringBuilder();

		agreementText
				.append("<Agreement xmlns=\"http://www.ggf.org/namespaces/ws-agreement\" AgreementId=\""
						+ uuid + "\">\n");
		agreementText.append("  <Name>ExampleAgreement</name>\n");
		agreementText.append("  <Context>\n");
		agreementText
				.append("   <AggreementInitiator>RandomClient</AgreementInitiator>\n");
		agreementText
				.append("   <AgreementResponder>Provider02</AgreementResponder>\n");
		agreementText
				.append("   <ServiceProvider>AgreementResponder</ServiceProvider>\n");
		agreementText
				.append("   <ExpirationTime>2014-03-07-1200</ExpirationTime>\n");
		agreementText
				.append("   <TemplateId>contract-template-2007-12-04</<TemplateId>>\n");
		agreementText.append("  </Context>\n");
		agreementText.append("</Agreement>\n");

		IAgreement agreement = new Agreement();
		agreement.setAgreementId(uuid);
		agreement.setConsumer("consumer2");
		agreement.setProvider(new Provider(null, UUID.randomUUID().toString(),
				"provider3"));
		agreement.setText(agreementText.toString());

		@SuppressWarnings("unused")
		IAgreement agreementSaved = new Agreement();
		try {
			agreementSaved = agreementDAO.save(agreement);
		} catch (Exception e) {
			
		}

		IAgreement nullAgreement = agreementDAO.getById(new Long(30000));
		assertEquals(null, nullAgreement);
	}

	//@Test
	public void getByAgreementId() {

		StringBuilder agreementText = new StringBuilder();

		String uuid = UUID.randomUUID().toString();

		agreementText
				.append("<Agreement xmlns=\"http://www.ggf.org/namespaces/ws-agreement\" AgreementId=\""
						+ uuid + "\">\n");
		agreementText.append("  <Name>ExampleAgreement2</name>\n");
		agreementText.append("  <Context>\n");
		agreementText
				.append("   <AggreementInitiator>RandomClient2</AgreementInitiator>\n");
		agreementText
				.append("   <AgreementResponder>Provider02</AgreementResponder>\n");
		agreementText
				.append("   <ServiceProvider>AgreementResponder2</ServiceProvider>\n");
		agreementText
				.append("   <ExpirationTime>2014-03-07-1200</ExpirationTime>\n");
		agreementText
				.append("   <TemplateId>contract-template-2007-12-04</<TemplateId>>\n");
		agreementText.append("  </Context>\n");
		agreementText.append("</Agreement>\n");

		IAgreement agreement = new Agreement();
		agreement.setAgreementId(uuid);
		agreement.setConsumer("consumer jose");
		agreement.setProvider(new Provider(null, UUID.randomUUID().toString(),
				"provider pepito"));
		agreement.setText(agreementText.toString());

		try {
			agreementDAO.save(agreement);
		} catch (Exception e) {
			assertEquals(e, agreement);
		}

		IAgreement nullAgreement = agreementDAO.getById(new Long(30000));
		assertEquals(null, nullAgreement);
	}

	@Test
	public void save() {

		StringBuilder agreementText = new StringBuilder();

		String agreementId = UUID.randomUUID().toString();

		agreementText
				.append("<Agreement xmlns=\"http://www.ggf.org/namespaces/ws-agreement\" AgreementId=\""
						+ agreementId + "\">\n");
		agreementText.append("  <Name>ExampleAgreement</name>\n");
		agreementText.append("  <Context>\n");
		agreementText
				.append("   <AggreementInitiator>RandomClient</AgreementInitiator>\n");
		agreementText
				.append("   <AgreementResponder>Provider01</AgreementResponder>\n");
		agreementText
				.append("   <ServiceProvider>AgreementResponder</ServiceProvider>\n");
		agreementText
				.append("   <ExpirationTime>2014-03-07-1200</ExpirationTime>\n");
		agreementText
				.append("   <TemplateId>contract-template-2007-12-04</<TemplateId>>\n");
		agreementText.append("  </Context>\n");
		agreementText.append("</Agreement>\n");

		eu.atos.sla.datamodel.IAgreement.AgreementStatus status = eu.atos.sla.datamodel.IAgreement.AgreementStatus.PENDING;
		String templateUuid = UUID.randomUUID().toString();

		ITemplate template = new Template();
		template.setText("Template name 1");
		template.setUuid(templateUuid);

		// Guarantee terms

		IGuaranteeTerm guaranteeTerm = new GuaranteeTerm();
		guaranteeTerm.setName("guarantee term name");
		guaranteeTerm.setServiceName("service Name");

		List<IGuaranteeTerm> guaranteeTerms = new ArrayList<IGuaranteeTerm>();
		guaranteeTerms.add(guaranteeTerm);

		IAgreement agreement = new Agreement();
		agreement.setAgreementId(agreementId);
		agreement.setConsumer("Consumer2");
		agreement.setProvider(new Provider(null, UUID.randomUUID().toString(),
				"Provider2"));
		agreement.setStatus(status);
		agreement.setTemplate(template);
		agreement.setText(agreementText.toString());
		agreement.setGuaranteeTerms(guaranteeTerms);

		IViolation violation = new Violation();

		violation.setContractUuid(agreementId);
		violation.setActualValue("8.0");
		violation.setExpectedValue("5.0");
		@SuppressWarnings("unused")
		IAgreement agreementSaved = new Agreement();
		try {
			agreementSaved = agreementDAO.save(agreement);

		} catch (Exception e) {

		}

	}

}
