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
package eu.atos.sla.service.rest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.bean.Agreement;
import eu.atos.sla.datamodel.bean.GuaranteeTerm;
import eu.atos.sla.evaluation.constraint.IConstraintEvaluator;
import eu.atos.sla.evaluation.constraint.simple.SimpleConstraintEvaluator;
import eu.atos.sla.monitoring.IMonitoringMetric;

public class ModacloudsTranslatorTest {

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
    public void testTranslate() throws IOException {
        String json = readFile("/violation_duke.json");
        IConstraintEvaluator constraintEvaluator = new SimpleConstraintEvaluator();
        
        ModacloudsTranslator translator = new ModacloudsTranslator(constraintEvaluator);
        Agreement agreement = new Agreement();
        GuaranteeTerm term = new GuaranteeTerm();
        term.setKpiName("responsetime");
        term.setName("rt");
        term.setServiceLevel("ResponseTimeViolated NOT_EXISTS");
        agreement.setGuaranteeTerms(Arrays.asList(new IGuaranteeTerm[] { term}));
        
        Map<IGuaranteeTerm, List<IMonitoringMetric>> map = translator.translate(agreement, json);
        
        assertEquals(1, map.get(term).size());
    }
    
    @Test
    public void testTranslateWrongAgreement() throws IOException {
        String json = readFile("/violation_ofbiz_2.0.1.json");
        IConstraintEvaluator constraintEvaluator = new SimpleConstraintEvaluator();
        
        ModacloudsTranslator translator = new ModacloudsTranslator(constraintEvaluator);
        Agreement agreement = new Agreement();
        GuaranteeTerm term = new GuaranteeTerm();
        term.setKpiName("responsetime");
        term.setName("rt");
        term.setServiceLevel("WinVMCPUUtilization NOT_EXISTS");
        agreement.setGuaranteeTerms(Arrays.asList(new IGuaranteeTerm[] { term}));
        
        Map<IGuaranteeTerm, List<IMonitoringMetric>> map = translator.translate(agreement, json);
        
        List<IMonitoringMetric> list = map.get(term);
        if (list == null) {
            assertNull(list);
        }
        else {
            assertEquals(0, map.get(term).size());
        }
    }

    private String readFile(String resourcePath) throws FileNotFoundException {
        File f = new File(this.getClass().getResource(resourcePath).getFile());
        Scanner scanner = new Scanner(f);
        String json = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return json;
    }

}
