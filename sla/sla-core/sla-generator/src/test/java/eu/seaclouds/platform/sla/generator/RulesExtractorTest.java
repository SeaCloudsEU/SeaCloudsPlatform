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

import it.polimi.tower4clouds.rules.MonitoringRules;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.xml.bind.JAXBException;

import static org.testng.Assert.*;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;

import eu.seaclouds.platform.sla.generator.RulesExtractor;

public class RulesExtractorTest {

    @BeforeMethod
    public void beforeMethod() {
    }

    @BeforeClass
    public void beforeClass() {
    }

    @Test
    public void testExtract() throws IOException, JAXBException {
        
        RulesExtractor e = new RulesExtractor();
        Reader r = openFile("/DemoDAM.yml");
        Map<String, MonitoringRules> extractedRules = e.extract(r);
        
        assertEquals(2, extractedRules.size());
        assertEquals(6, extractedRules.get("java_ee_server").getMonitoringRules().size());
        assertEquals(4, extractedRules.get("db").getMonitoringRules().size());
    }
    
    public static Reader openFile(String path) throws IOException {
        InputStream is = RulesExtractorTest.class.getResourceAsStream(path);
        Reader reader = new InputStreamReader(is);

        return reader;
    }
}
