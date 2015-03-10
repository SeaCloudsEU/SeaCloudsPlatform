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
package eu.atos.sla.datamodel;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import eu.atos.sla.datamodel.bean.Template;

public class TemplateTest {

	@Ignore
	@Test
	public void testPojo() {

		
		String templateUuid = UUID.randomUUID().toString();
		
		Template template = new Template();
		template.setText("Template name 1");
		template.setUuid(templateUuid);

        
        
        
	}
}
