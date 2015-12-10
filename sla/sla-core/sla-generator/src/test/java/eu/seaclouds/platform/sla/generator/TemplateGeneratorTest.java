/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
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
package eu.seaclouds.platform.sla.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import it.polimi.tower4clouds.rules.MonitoringRules;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.atos.sla.parser.data.wsag.KPITarget;
import eu.atos.sla.parser.data.wsag.ServiceLevelObjective;
import eu.atos.sla.parser.data.wsag.ServiceScope;
import eu.atos.sla.parser.data.wsag.Template;
import eu.seaclouds.platform.sla.generator.ContextInfo;
import eu.seaclouds.platform.sla.generator.JaxbUtils;
import eu.seaclouds.platform.sla.generator.SlaInfo;
import eu.seaclouds.platform.sla.generator.TemplateGenerator;
import eu.seaclouds.platform.sla.generator.ContextInfo.Validity;

public class TemplateGeneratorTest {

    @Test
    public void testGenerate() throws JAXBException {
        ContextInfo context = new ContextInfo("provider", "consumer", "service", new Validity(2, 0, 0));
        MonitoringRules rules = JaxbUtils.load(
                MonitoringRules.class, this.getClass().getResourceAsStream("/seacloudsRules.xml"));
        
        SlaInfo slaInfo = new SlaInfo(context, Collections.singletonMap("frontend", rules));
        TemplateGenerator g = new TemplateGenerator(slaInfo);
        
        Template t = g.generate();
        JaxbUtils.print(t);
        assertEquals(context.getProvider(), t.getContext().getAgreementResponder());
        assertEquals(context.getConsumer(), t.getContext().getAgreementInitiator());
        assertEquals(context.getService(), t.getContext().getService());
        assertEquals(2, t.getTerms().getAllTerms().getGuaranteeTerms().size());
        
        List<GuaranteeTerm> expectedGts = new ArrayList<GuaranteeTerm>();
        expectedGts.add(newGuaranteeTerm(
                "tomcat_server", 
                "tomcat_server/AvarageResponseTimeInternalComponent", 
                "AvarageResponseTime_tomcat_server_Violation NOT_EXISTS"));
        
        expectedGts.add(newGuaranteeTerm(
                "tomcat_server", 
                "tomcat_server/AppAvailable", 
                "AvarageAppAvailability_tomcat_server_Violation NOT_EXISTS"));

        List<GuaranteeTerm> actualGts = t.getTerms().getAllTerms().getGuaranteeTerms();

        for (int i = 0; i < actualGts.size(); i++) {
            GuaranteeTerm expected = expectedGts.get(i);
            GuaranteeTerm actual = expectedGts.get(i);
            
            checkGuaranteeTerm(expected, actual);
        }
    }
    
    private GuaranteeTerm newGuaranteeTerm(String serviceScope, String kpiName, String customServiceLevel) {
        GuaranteeTerm result = new GuaranteeTerm();
        result.setServiceScope(new ServiceScope());
        result.getServiceScope().setValue(serviceScope);
        
        result.setServiceLevelObjetive(new ServiceLevelObjective());
        result.getServiceLevelObjetive().setKpitarget(new KPITarget());
        result.getServiceLevelObjetive().getKpitarget().setKpiName(kpiName);
        result.getServiceLevelObjetive().getKpitarget().setCustomServiceLevel(customServiceLevel);
        
        return result;
    }
    
    private void checkGuaranteeTerm(GuaranteeTerm expected, GuaranteeTerm actual) {
        assertEquals(
                expected.getServiceScope().getValue(), actual.getServiceScope().getValue());
        checkKpiTarget(
                expected.getServiceLevelObjetive().getKpitarget(), actual.getServiceLevelObjetive().getKpitarget());
    }
    
    private void checkKpiTarget(KPITarget expected, KPITarget actual) {
        assertEquals(expected.getKpiName(), actual.getKpiName());
        assertTrue(actual.getCustomServiceLevel().contains(expected.getCustomServiceLevel()));
    }
}
