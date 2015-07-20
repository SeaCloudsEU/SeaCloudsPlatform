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
package eu.atos.sla.evaluation;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.DummyBreachRepository;
import eu.atos.sla.evaluation.guarantee.TestMetricsGenerator;
import eu.atos.sla.monitoring.IMonitoringMetric;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/enforcement-test-context.xml")
public class AgreementEvaluatorTest {

    @Autowired
    AgreementEvaluator agreementEval;
    @Autowired
    DummyBreachRepository breachRepository;
    
    List<IMonitoringMetric> allMetrics;
    Date now;
    IAgreement agreement;
    Map<IGuaranteeTerm, List<IMonitoringMetric>> metricsMap;
    
    @Before
    public void setUp() throws Exception {
        String kpiName = "LATENCY";
        String constraint = kpiName + " LT 100";
        
        now = new Date();
        String[] values = new String[] { "102", "99", "99", "99", "101", "100", "99", "99" };
        TestMetricsGenerator metricsGenerator = new TestMetricsGenerator(now, values);
        
        allMetrics = metricsGenerator.getMetrics(kpiName);
        
        agreement = new Agreement();
        agreement.setAgreementId("test-agreement");
        
        IGuaranteeTerm term = newGuaranteeTerm(kpiName, constraint);
        agreement.setGuaranteeTerms(Collections.singletonList(term));
        
        metricsMap = new HashMap<IGuaranteeTerm, List<IMonitoringMetric>>();
        metricsMap.put(term, allMetrics);
        
        breachRepository.init(kpiName, Collections.<IMonitoringMetric>emptyList());
    }

    @Test
    public void testEvaluate() {
    }

    @Test
    public void testEvaluateWithMetrics() {
        agreementEval.evaluate(agreement, metricsMap);
    }
    
    private IGuaranteeTerm newGuaranteeTerm(String kpiName, String constraint) {
        
        GuaranteeTerm t = new GuaranteeTerm();
        t.setKpiName(kpiName);
        t.setServiceLevel(constraint);
        
        return t;
    }
    
    public 
    AgreementEvaluator getAgreementEval() {
        return agreementEval;
    }

    public void setAgreementEval(AgreementEvaluator agreementEval) {
        this.agreementEval = agreementEval;
    }

}
