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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IProviderDAO;
import eu.atos.sla.dao.ITemplateDAO;
import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IProvider;
import eu.atos.sla.datamodel.ITemplate;
import eu.atos.sla.datamodel.IViolation;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.EnforcementJob;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.datamodel.bean.Provider;
import eu.atos.sla.datamodel.bean.Template;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/enforcement-test-context.xml")
@Transactional
public class AgreementEnforcementTest {

    @Autowired
    AgreementEnforcement agreementEnforcement;
    
    @Autowired
    IAgreementDAO agreementDao;
    
    @Autowired
    ITemplateDAO templateDao;
    
    @Autowired
    IEnforcementService enforcementService;
    
    @Autowired
    IProviderDAO providerDao;
    
    IAgreement agreement;
    
    @Before
    @Transactional
    public void setUp() throws Exception {
        String kpiName = "LATENCY";
        String constraint = kpiName + " LT 0.5";

        IProvider provider = newProvider();
        IProvider psaved = providerDao.save(provider);
        
        ITemplate template = newTemplate(psaved);
        
        templateDao.save(template);
        
        agreement = new Agreement();
        agreement.setAgreementId("test-agreement");
        agreement.setProvider(provider);
        agreement.setTemplate(template);
        
        agreement.setGuaranteeTerms(Collections.singletonList(newGuaranteeTerm(kpiName, constraint)));
        agreement.getGuaranteeTerms().get(0).setViolations(new ArrayList<IViolation>());
        
        agreementDao.save(agreement);
        
        IEnforcementJob job = new EnforcementJob();
        job.setAgreement(agreement);
        
        enforcementService.createEnforcementJob(job);
    }

    @Test
    public void testEnforce() {
        Date now = new Date();
        Date since = new Date(now.getTime() - 10000);
        
        agreementEnforcement.enforce(agreement, since, false);
        
        IAgreement a = agreementDao.getByAgreementId(agreement.getAgreementId());
        assertEquals(agreement.getGuaranteeTerms().get(0).getStatus(), a.getGuaranteeTerms().get(0).getStatus());
    }

//    @Test
    public void testEnforceWithMetrics() {
        fail("Not yet implemented");
    }
    
    private IProvider newProvider() {
        IProvider provider = new Provider();
        provider.setName("provider-name");
        provider.setUuid("provider-uuid");
        
        return provider;
    }

    private ITemplate newTemplate(IProvider provider) {
        ITemplate template = new Template();
        template.setUuid("templateId");
        template.setText("");
        template.setProvider(provider);
        return template;
    }
    private IGuaranteeTerm newGuaranteeTerm(String kpiName, String constraint) {
        
        GuaranteeTerm t = new GuaranteeTerm();
        t.setKpiName(kpiName);
        t.setServiceLevel(constraint);
        
        return t;
    }

    
}
