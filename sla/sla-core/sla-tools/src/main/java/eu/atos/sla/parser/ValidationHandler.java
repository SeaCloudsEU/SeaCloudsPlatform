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
package eu.atos.sla.parser;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ValidationHandler implements ValidationEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ValidationHandler.class);

	@Override
	public boolean handleEvent(ValidationEvent validationEvent) {
		if (validationEvent.getLinkedException()==null){
			logger.warn("detected " +validationEvent.getMessage() +"  it will be ignored");			
			return true;
		}
		return false;
	}

}
