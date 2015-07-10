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
package eu.atos.sla.enforcement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.dao.IGuaranteeTermDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.EnforcementJob;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.datamodel.bean.Template;
import eu.atos.sla.monitoring.IMonitoringMetric;
import eu.atos.sla.monitoring.simple.MonitoringMetric;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/enforcement-test-context.xml")
public class EnforcementServiceTest  {

    @Autowired
    IEnforcementJobDAO enforcementJobDao;

    @Autowired
    IAgreementDAO agreementDao;

    @Autowired
    IGuaranteeTermDAO guaranteeTermDao;

    @Autowired
    IProviderDAO providerDao;
    
    @Autowired
    ITemplateDAO templateDao;
    
    @Autowired
    IEnforcementService service;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testGetEnforcementJobs() {

        List<IEnforcementJob> jobs = service.getEnforcementJobs();
        int oldSize = jobs.size();
        
        IAgreement agreementStored = saveAgreement();        
        
        IEnforcementJob expected = newEnforcementJob(agreementStored);
        try {
            enforcementJobDao.save(expected);
        } catch (Exception e) {
            e.printStackTrace();
        }

        jobs = service.getEnforcementJobs();
        
        assertEquals(oldSize + 1, jobs.size());
        
        for (IEnforcementJob actual : jobs) {
            if (expected.getId().equals(actual.getId())) {
                assertTrue(equalJobs(expected, actual));
                return;
            }
        }
        fail("new job not found in jobs");
    }
    
    
    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testGetEnforcementJob() {

        IAgreement agreementStored = saveAgreement();        
        
        EnforcementJob expected = newEnforcementJob(agreementStored);
        try {
            enforcementJobDao.save(expected);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        IEnforcementJob actual = service.getEnforcementJob(expected.getId());
        assertTrue(equalJobs(expected, actual));
        actual = service.getEnforcementJob(-1L);
        assertNull(actual);
    }

    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testGetEnforcementJobByAgreementId() throws Exception {
        Provider provider = new Provider(null, UUID.randomUUID().toString(),
                "Provider 2");
        
        Template template = new Template();
        template.setServiceId("service");
        template.setText("asadsad");
        template.setUuid(UUID.randomUUID().toString());
        template.setProvider(provider);
        providerDao.save(provider);
        templateDao.save(template);
        
        
        IAgreement agreement = newAgreement(provider, template);
        agreementDao.save(agreement);
        
        IEnforcementJob expected = newEnforcementJob(agreement);
        enforcementJobDao.save(expected);
        
        IEnforcementJob actual;
        
        actual = service.getEnforcementJobByAgreementId(agreement.getAgreementId());
        assertTrue(equalJobs(expected, actual));
        
        actual = service.getEnforcementJobByAgreementId("noexiste");
        assertNull(actual);
    }

    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testStartEnforcement() throws Exception {
        
        IEnforcementJob job = saveAgreementAndEnforcementJob();
        boolean result = service.startEnforcement(job.getAgreement().getAgreementId());
        assertTrue(result);
        
        job = service.getEnforcementJob(job.getId());
        assertTrue(job.getEnabled());
        
        result = service.startEnforcement("noexiste");
        assertFalse(result);
        
    }

    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testStopEnforcement() throws Exception {
        
        IEnforcementJob job = saveAgreementAndEnforcementJob();
        job.setEnabled(true);
        enforcementJobDao.save(job);
        
        boolean result = service.stopEnforcement(job.getAgreement().getAgreementId());
        assertTrue(result);
        
        job = service.getEnforcementJob(job.getId());
        assertFalse(job.getEnabled());
        
        result = service.startEnforcement("noexiste");
        assertFalse(result);
    }

    @Test
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void testDeleteEnforcement() throws Exception {

        IEnforcementJob job = saveAgreementAndEnforcementJob();
        
        Long id = job.getId();
        
        boolean result = 
                service.deleteEnforcementJobByAgreementId(job.getAgreement().getAgreementId());
        
        assertTrue(result);
        
        job = service.getEnforcementJob(id);
        assertNull(job);
        
        result = service.deleteEnforcementJobByAgreementId("noexiste");
        assertFalse(result);
    }
    
    @Test
    @Transactional
    public void testCreateEnforcementJob() throws Exception {
        Date firstExecuted = new Date();

        IAgreement agreement = saveAgreement();
        EnforcementJob job = new EnforcementJob();

        job.setAgreement(agreement);
        job.setEnabled(true);
        job.setFirstExecuted(firstExecuted);

        Date lastExecuted = new Date();
        job.setLastExecuted(lastExecuted);
        
        IEnforcementJob stored = service.createEnforcementJob(job);
        assertTrue(stored.getId() != null);
        stored = enforcementJobDao.getById(stored.getId());
        assertEquals(job.getEnabled(), stored.getEnabled());
        assertEquals(job.getFirstExecuted(), stored.getFirstExecuted());
        assertEquals(job.getLastExecuted(), stored.getLastExecuted());
    }
    
    @Test
    @Transactional
    public void testCreateEnforcementJob2() throws Exception {
        
        IAgreement agreement = saveAgreement();
        IEnforcementJob stored = service.createEnforcementJob(agreement.getAgreementId());
        assertTrue(stored.getId() != null);
        assertFalse(stored.getEnabled());
        assertNull(stored.getFirstExecuted());
        assertNull(stored.getLastExecuted());
    }
    
    @Test
    @Transactional
    public void testEnforceReceivedMetrics() throws Exception {
        
        String kpiName = "responsetime";

        /*
         * create provider
         */
        IProvider provider = new Provider(null, "provider-test", "provider-test");
        providerDao.save(provider);
        
        ITemplate template = new Template();
        template.setUuid(UUID.randomUUID().toString());
        template.setText("");
        provider.addTemplate(template);
        template.setProvider(provider);
        templateDao.save(template);
        
        /* create agreement */
        IGuaranteeTerm term = TestAgreementFactory.newGuaranteeTerm(kpiName, kpiName + " LT 100");
        IAgreement agreement = TestAgreementFactory.newAgreement(
            UUID.randomUUID().toString(),
            provider,
            "consumer",
            Arrays.asList(
                term,
                TestAgreementFactory.newGuaranteeTerm("performance", "performance GT 0.9")
            )
        );
        agreement.setTemplate(template);
        agreementDao.save(agreement);
        
        /* create ejob */
        IEnforcementJob job = new EnforcementJob();
        job.setAgreement(agreement);
        service.createEnforcementJob(job);
        
        
            
        List<IMonitoringMetric> metrics = Arrays.asList(
                (IMonitoringMetric) new MonitoringMetric(kpiName, 99, new Date()),
                (IMonitoringMetric) new MonitoringMetric(kpiName, 100, new Date()) 
        );
        Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap = 
                new HashMap<IGuaranteeTerm, List<IMonitoringMetric>>();
        metricsMap.put(term, metrics);
        
        service.doEnforcement(agreement, metricsMap);
    }

    private IAgreement saveAgreement() {
        Provider provider = new Provider(null, UUID.randomUUID().toString(), "Provider 2");
        
        Template template = new Template();
        template.setServiceId("service");
        template.setText("asadsad");
        template.setUuid(UUID.randomUUID().toString());
        template.setProvider(provider);
        providerDao.save(provider);
        templateDao.save(template);
        
        
        IAgreement agreement = newAgreement(provider, template);
        IAgreement agreementStored = agreementDao.save(agreement);
        return agreementStored;
    }
    
    private EnforcementJob saveAgreementAndEnforcementJob() throws Exception {
        IAgreement agreementStored = saveAgreement();
        
        EnforcementJob expected = newEnforcementJob(agreementStored);
        enforcementJobDao.save(expected);
        
        return expected;
    }

    private EnforcementJob newEnforcementJob(IAgreement agreement) {
        
        EnforcementJob job = new EnforcementJob();
        job.setAgreement(agreement);
        job.setEnabled(true);
        job.setLastExecuted(new Date());
        return job;
    }
    
    private Agreement newAgreement(Provider provider, Template template) {
        Agreement agreement = new Agreement();
        agreement.setAgreementId(UUID.randomUUID().toString());
        agreement.setProvider(provider);
        agreement.setTemplate(template);
        agreement.setGuaranteeTerms(Collections.<IGuaranteeTerm>emptyList());
        return agreement;
    }
    
    private boolean equalJobs(IEnforcementJob expected, IEnforcementJob o2) {
        
        return (expected.getEnabled() == o2.getEnabled() && expected.getLastExecuted() == o2.getLastExecuted());
            
    }

    public void setEnforcementJobDao(IEnforcementJobDAO enforcementJobDao) {
        this.enforcementJobDao = enforcementJobDao;
    }

    public void setService(EnforcementService service) {
        this.service = service;
    }
}
