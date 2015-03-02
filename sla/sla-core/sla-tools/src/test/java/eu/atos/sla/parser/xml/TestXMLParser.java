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
package eu.atos.sla.parser.xml;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.atos.sla.parser.ParserException;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.Template;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/tools-test-context.xml")
public class TestXMLParser {
	private static Logger logger = LoggerFactory.getLogger(TestXMLParser.class);

	@Test
	public void testTemplateXMLtoJSON() {
		String path = "/samples/template01.xml";
		File file = new File(this.getClass().getResource(path).getFile());
		boolean error = false;
		try {
			String serializedData = getStringFromInputStream(new FileInputStream(file));
			TemplateParser templateParser = new TemplateParser();
			Template wsagObject = templateParser.getWsagObject(serializedData);
			logger.error("Readed template:"+wsagObject);
			String wsagData = templateParser.getWsagAsSerializedData(serializedData);
			templateParser.getSerializedData(wsagData);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			logger.error("", e1);
		} catch (ParserException e) {
			e.printStackTrace();
			error = true;
			logger.error("", e);
		}
		
		assert(!error);
	}
	
	
	@Test
	public void testAgreementXMLParser() {
		File file = new File("../samples/agreement01.xml");
		boolean error = false;
		try {
			String serializedData = getStringFromInputStream(new FileInputStream(file));
			AgreementParser agreementParser = new AgreementParser();
			Agreement wsagObject = agreementParser.getWsagObject(serializedData);
			logger.error("Readed agreement:"+wsagObject);
			String wsagData = agreementParser.getWsagAsSerializedData(serializedData);
			agreementParser.getSerializedData(wsagData);
		} catch (FileNotFoundException e1) {
			logger.error("", e1);
		} catch (ParserException e) {
			error = true;
			logger.error("", e);
		}
		
		assert(!error);
	}

	@Test
	public void testTemplateXMLParser() {
		File file = new File("../samples/template01.xml");
		boolean error = false;
		try {
			String serializedData = getStringFromInputStream(new FileInputStream(file));
			TemplateParser templateParser = new TemplateParser();
			Template wsagObject = templateParser.getWsagObject(serializedData);
			logger.error("Readed template:"+wsagObject);
			String wsagData = templateParser.getWsagAsSerializedData(serializedData);
			templateParser.getSerializedData(wsagData);
		} catch (FileNotFoundException e1) {
			logger.error("",e1);
		} catch (ParserException e) {
			error = true;
			logger.error("", e);
		}
	
		assert(!error);
	}
	
	

	
	private String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}	
}
