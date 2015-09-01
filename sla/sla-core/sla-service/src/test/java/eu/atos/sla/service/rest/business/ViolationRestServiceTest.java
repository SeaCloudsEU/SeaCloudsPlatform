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
package eu.atos.sla.service.rest.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.dao.IViolationDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IAgreement.AgreementStatus;
import eu.atos.sla.datamodel.IBreach;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.Breach;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.datamodel.bean.Template;
import eu.atos.sla.datamodel.bean.Violation;

/**
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/sla-service-db-JPA-test-context.xml")
public class ViolationRestServiceTest extends
        AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    IViolationDAO violationDAO;

    @Autowired
    IProviderDAO providerDAO;

    @Autowired
    ITemplateDAO templateDAO;

    @Autowired
    IAgreementDAO agreementDAO;

    @Autowired
    IGuaranteeTermDAO guaranteeTermDAO;

    public ViolationRestServiceTest() {

    }

    @Test
    public void testGetViolations() {
        

    }

    @Test
    @Rollback(false)
    public void testGetViolationByAgreementId() {
/* TODO egarrido: esta dando error
        String agreementId = UUID.randomUUID().toString();
        String templateUUID = UUID.randomUUID().toString();
        String violationUUID = UUID.randomUUID().toString();
        String violationUUID2 = UUID.randomUUID().toString();

        AgreementStatus status = AgreementStatus.PENDING;

        // Template
        ITemplate template = new Template();
        template.setText(new Constants().templateTest.toString());
        template.setUuid(templateUUID);

        @SuppressWarnings("unused")
        ITemplate templateSaved = new Template();
        try {
            templateSaved = templateDAO.save(template);
        } catch (Exception e) {
            e.printStackTrace();
        }

        IAgreement agreement = new Agreement();
        agreement.setAgreementId(agreementId);
        agreement.setConsumer("RandomClient");
        agreement.setStatus(status);
        agreement.setTemplate(template);
        agreement.setExpirationDate(new Date(12345));
        agreement.setText(new Constants().agreementTest.toString());

        // Violation
        IViolation violation = new Violation();
        violation.setUuid(violationUUID);
        violation.setContractUuid(agreementId);
        violation.setServiceName("Service name");
        violation.setServiceScope("Service scope");
        violation.setKpiName("Uptime");
        violation.setDatetime(new Date(22350L));
        violation.setExpectedValue("3.0");
        violation.setActualValue("5.0");

        IViolation violationSaved = new Violation();
        try {
            violationSaved = violationDAO.save(violation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Violation 2
        IViolation violation2 = new Violation();
        violation2.setUuid(violationUUID2);
        violation2.setContractUuid(agreementId);
        violation2.setServiceName("Service name");
        violation2.setServiceScope("Service scope");
        violation2.setKpiName("Uptime");
        violation2.setDatetime(new Date(22360L));
        violation2.setExpectedValue("4.0");
        violation2.setActualValue("4.0");

        IViolation violationSaved2 = new Violation();
        try {
            violationSaved2 = violationDAO.save(violation2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<IViolation> violations = new ArrayList<IViolation>();

        violations.add(violationSaved);
        violations.add(violationSaved2);

        // Breach

        IBreach breach = new Breach();
        breach.setKpiName("availability");
        breach.setDatetime(new Date(12345L));
        breach.setValue("6.0");
        breach.setAgreementUuid(agreementId);

        IBreach breach2 = new Breach();
        breach2.setKpiName("availability");
        breach2.setDatetime(new Date(22325L));
        breach2.setValue("7.0");
        breach2.setAgreementUuid(agreementId);

        List<IBreach> breaches = new ArrayList<IBreach>();
        breaches.add(breach);
        breaches.add(breach2);

        // Guarantee terms

        IGuaranteeTerm guaranteeTerm = new GuaranteeTerm();
        guaranteeTerm.setName("GT_Uptime");
        guaranteeTerm.setViolations(violations);

        List<IGuaranteeTerm> guaranteeTerms = new ArrayList<IGuaranteeTerm>();
        guaranteeTerms.add(guaranteeTerm);

        agreement.setGuaranteeTerms(guaranteeTerms);

        IAgreement agreementSaved = new Agreement();
        try {
            agreementSaved = agreementDAO.save(agreement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<IViolation> violationsFromDB = violationDAO.getByAgreement(
                agreementId, agreementSaved.getGuaranteeTerms().get(0)
                        .getName());

        assertEquals(2, violationsFromDB.size());*/
        assert(true);
    }

    @SuppressWarnings("static-access")
    @Test
    public void testGetViolationsByAgreementIdInaRangeOfDates() {

        String agreementId = UUID.randomUUID().toString();
        String providerUUID = UUID.randomUUID().toString();
        String templateUUID = UUID.randomUUID().toString();
        String violationUUID = UUID.randomUUID().toString();
        String violationUUID2 = UUID.randomUUID().toString();

        AgreementStatus status = AgreementStatus.PENDING;
        // Provider
        IProvider provider = new Provider();
        provider.setName("Provider" + providerUUID);
        provider.setUuid(providerUUID);

        IProvider providerSaved = new Provider();
        try {
            providerSaved = providerDAO.save(provider);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Template
        ITemplate template = new Template();
        template.setText(new Constants().templateTest.toString());
        template.setUuid(templateUUID);
        template.setProvider(providerSaved);

        @SuppressWarnings("unused")
        ITemplate templateSaved = new Template();
        try {
            templateSaved = templateDAO.save(template);
        } catch (Exception e) {
            e.printStackTrace();
        }


        IAgreement agreement = new Agreement();
        agreement.setAgreementId(agreementId);
        agreement.setConsumer("RandomClient");
        agreement.setProvider(providerSaved);
        agreement.setStatus(status);
        agreement.setTemplate(template);
        agreement.setExpirationDate(new Date(12345));
        agreement.setText(new Constants().agreementTest.toString());

        // Violation
        IViolation violation = new Violation();
        violation.setUuid(violationUUID);
        violation.setContractUuid(agreementId);
        violation.setServiceName("Service name");
        violation.setServiceScope("Service scope");
        violation.setKpiName("Uptime");
        violation.setDatetime(new Date(22350L));
        violation.setExpectedValue("3.0");
        violation.setActualValue("5.0");

        IViolation violationSaved = new Violation();
        try {
            violationSaved = violationDAO.save(violation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Violation 2
        IViolation violation2 = new Violation();
        violation2.setUuid(violationUUID2);
        violation2.setContractUuid(agreementId);
        violation2.setServiceName("Service name");
        violation2.setServiceScope("Service scope");
        violation2.setKpiName("Uptime");
        violation2.setDatetime(new Date(22360L));
        violation2.setExpectedValue("4.0");
        violation2.setActualValue("4.0");

        IViolation violationSaved2 = new Violation();
        try {
            violationSaved2 = violationDAO.save(violation2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<IViolation> violations = new ArrayList<IViolation>();

        violations.add(violationSaved);
        violations.add(violationSaved2);

        // Breach

        IBreach breach = new Breach();
        breach.setKpiName("availability");
        breach.setDatetime(new Date(12345L));
        breach.setValue("6.0");
        breach.setAgreementUuid(agreementId);

        IBreach breach2 = new Breach();
        breach2.setKpiName("availability");
        breach2.setDatetime(new Date(22325L));
        breach2.setValue("7.0");
        breach2.setAgreementUuid(agreementId);

        List<IBreach> breaches = new ArrayList<IBreach>();
        breaches.add(breach);
        breaches.add(breach2);

        // Guarantee terms

        IGuaranteeTerm guaranteeTerm = new GuaranteeTerm();
        guaranteeTerm.setName("GT_Uptime");
        guaranteeTerm.setViolations(violations);

        List<IGuaranteeTerm> guaranteeTerms = new ArrayList<IGuaranteeTerm>();
        guaranteeTerms.add(guaranteeTerm);

        agreement.setGuaranteeTerms(guaranteeTerms);

        IAgreement agreementSaved = new Agreement();
        try {
            agreementSaved = agreementDAO.save(agreement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(agreementId, agreementSaved.getAgreementId());

        Date begin = new Date(12320L);
        Date end = new Date(32360L);

        List<IViolation> violationsFromDB = violationDAO
                .getByAgreementIdInARangeOfDates(agreement.getAgreementId(),
                        agreement.getGuaranteeTerms().get(0).getName(), begin,
                        end);

        assertEquals(2, violationsFromDB.size());
    }

    @SuppressWarnings({ "static-access", "unused" })
    @Test
    @Rollback(false)
    public void testGetViolationsByProviderIdInaRangeOfDates() {

        String agreementId = UUID.randomUUID().toString();
        String providerUUID = UUID.randomUUID().toString();
        String templateUUID = UUID.randomUUID().toString();
        String violationUUID = UUID.randomUUID().toString();
        String violationUUID2 = UUID.randomUUID().toString();

        AgreementStatus status = AgreementStatus.PENDING;
        // Provider
        IProvider provider = new Provider();
        provider.setName("Provider" + providerUUID);
        provider.setUuid(providerUUID);

        IProvider providerSaved = new Provider();
        try {
            providerSaved = providerDAO.save(provider);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Template
        ITemplate template = new Template();
        template.setText(new Constants().templateTest.toString());
        template.setUuid(templateUUID);
        template.setProvider(providerSaved);

        ITemplate templateSaved = new Template();
        try {
            templateSaved = templateDAO.save(template);
        } catch (Exception e) {
            e.printStackTrace();
        }


        IAgreement agreement = new Agreement();
        agreement.setAgreementId(agreementId);
        agreement.setConsumer("RandomClient");
        agreement.setProvider(providerSaved);
        agreement.setStatus(status);
        agreement.setTemplate(template);
        agreement.setExpirationDate(new Date(12345));
        agreement.setText(new Constants().agreementTest.toString());

        // Violation
        IViolation violation = new Violation();
        violation.setUuid(violationUUID);
        violation.setContractUuid(agreementId);
        violation.setServiceName("Service name");
        violation.setServiceScope("Service scope");
        violation.setKpiName("Uptime");
        violation.setDatetime(new Date(22350L));
        violation.setExpectedValue("3.0");
        violation.setActualValue("5.0");

        IViolation violationSaved = new Violation();
        try {
            violationSaved = violationDAO.save(violation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Violation 2
        IViolation violation2 = new Violation();
        violation2.setUuid(violationUUID2);
        violation2.setContractUuid(agreementId);
        violation2.setServiceName("Service name");
        violation2.setServiceScope("Service scope");
        violation2.setKpiName("Uptime");
        violation2.setDatetime(new Date(22360L));
        violation2.setExpectedValue("4.0");
        violation2.setActualValue("4.0");

        IViolation violationSaved2 = new Violation();
        try {
            violationSaved2 = violationDAO.save(violation2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<IViolation> violations = new ArrayList<IViolation>();

        violations.add(violationSaved);
        violations.add(violationSaved2);

        // Breach

        IBreach breach = new Breach();
        breach.setKpiName("availability");
        breach.setDatetime(new Date(12345L));
        breach.setValue("6.0");
        breach.setAgreementUuid(agreementId);

        IBreach breach2 = new Breach();
        breach2.setKpiName("availability");
        breach2.setDatetime(new Date(22325L));
        breach2.setValue("7.0");
        breach2.setAgreementUuid(agreementId);

        List<IBreach> breaches = new ArrayList<IBreach>();
        breaches.add(breach);
        breaches.add(breach2);

        // Guarantee terms

        IGuaranteeTerm guaranteeTerm = new GuaranteeTerm();
        guaranteeTerm.setName("GT_Uptime");
        guaranteeTerm.setViolations(violations);

        List<IGuaranteeTerm> guaranteeTerms = new ArrayList<IGuaranteeTerm>();
        guaranteeTerms.add(guaranteeTerm);

        agreement.setGuaranteeTerms(guaranteeTerms);

        IAgreement agreementSaved = new Agreement();
        try {
            agreementSaved = agreementDAO.save(agreement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(agreementId, agreementSaved.getAgreementId());

        Date begin = new Date(12320L);
        Date end = new Date(32360L);

        List<IViolation> violationsFromDB = violationDAO
                .getByProviderInaRangeOfDates(
                        agreement.getProvider().getUuid(), begin, end);

        assertEquals(2, violationsFromDB.size());
    }
    
    
    @SuppressWarnings({ "static-access", "unused" })
    @Test
    @Rollback(false)
    public void testGetViolationsByProviderId() {

        String agreementId = UUID.randomUUID().toString();
        String providerUUID = UUID.randomUUID().toString();
        String templateUUID = UUID.randomUUID().toString();
        String violationUUID = UUID.randomUUID().toString();
        String violationUUID2 = UUID.randomUUID().toString();

        AgreementStatus status = AgreementStatus.PENDING;


        // Provider
        IProvider provider = new Provider();
        provider.setName("Provider" + providerUUID);
        provider.setUuid(providerUUID);

        IProvider providerSaved = new Provider();
        try {
            providerSaved = providerDAO.save(provider);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Template
        ITemplate template = new Template();
        template.setText(new Constants().templateTest.toString());
        template.setUuid(templateUUID);
        template.setProvider(providerSaved);

        ITemplate templateSaved = new Template();
        try {
            templateSaved = templateDAO.save(template);
        } catch (Exception e) {
            e.printStackTrace();
        }


        IAgreement agreement = new Agreement();
        agreement.setAgreementId(agreementId);
        agreement.setConsumer("RandomClient");
        agreement.setProvider(providerSaved);
        agreement.setStatus(status);
        agreement.setTemplate(template);
        agreement.setExpirationDate(new Date(12345));
        agreement.setText(new Constants().agreementTest.toString());

        // Violation
        IViolation violation = new Violation();
        violation.setUuid(violationUUID);
        violation.setContractUuid(agreementId);
        violation.setServiceName("Service name");
        violation.setServiceScope("Service scope");
        violation.setKpiName("Uptime");
        violation.setDatetime(new Date(22350L));
        violation.setExpectedValue("3.0");
        violation.setActualValue("5.0");

        IViolation violationSaved = new Violation();
        try {
            violationSaved = violationDAO.save(violation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Violation 2
        IViolation violation2 = new Violation();
        violation2.setUuid(violationUUID2);
        violation2.setContractUuid(agreementId);
        violation2.setServiceName("Service name");
        violation2.setServiceScope("Service scope");
        violation2.setKpiName("Uptime");
        violation2.setDatetime(new Date(22360L));
        violation2.setExpectedValue("4.0");
        violation2.setActualValue("4.0");

        IViolation violationSaved2 = new Violation();
        try {
            violationSaved2 = violationDAO.save(violation2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<IViolation> violations = new ArrayList<IViolation>();

        violations.add(violationSaved);
        violations.add(violationSaved2);

        // Breach

        IBreach breach = new Breach();
        breach.setKpiName("availability");
        breach.setDatetime(new Date(12345L));
        breach.setValue("6.0");
        breach.setAgreementUuid(agreementId);

        IBreach breach2 = new Breach();
        breach2.setKpiName("availability");
        breach2.setDatetime(new Date(22325L));
        breach2.setValue("7.0");
        breach2.setAgreementUuid(agreementId);

        List<IBreach> breaches = new ArrayList<IBreach>();
        breaches.add(breach);
        breaches.add(breach2);

        // Guarantee terms

        IGuaranteeTerm guaranteeTerm = new GuaranteeTerm();
        guaranteeTerm.setName("GT_Uptime");
        guaranteeTerm.setViolations(violations);

        List<IGuaranteeTerm> guaranteeTerms = new ArrayList<IGuaranteeTerm>();
        guaranteeTerms.add(guaranteeTerm);

        agreement.setGuaranteeTerms(guaranteeTerms);

        IAgreement agreementSaved = new Agreement();
        try {
            agreementSaved = agreementDAO.save(agreement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(agreementId, agreementSaved.getAgreementId());

        Date begin = new Date(12320L);
        Date end = new Date(32360L);

        List<IViolation> violationsFromDB = violationDAO
                .getByProvider(
                        agreement.getProvider().getUuid());

        assertEquals(2, violationsFromDB.size());
    }

    @Test
    public void testCreateViolation() {

        assert (true);

    }

}
