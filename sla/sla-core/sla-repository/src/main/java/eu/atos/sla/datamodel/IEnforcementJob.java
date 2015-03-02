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

import java.util.Date;

public interface IEnforcementJob {

	/*
	 * Internally generated id
	 */
	Long getId();
	
	/**
	 * Date of last enforcement start
	 */
	Date getFirstExecuted();
	
	void setFirstExecuted(Date date);
	
	/**
	 * Last datetime where the job was executed
	 */
	Date getLastExecuted();
	
	void setLastExecuted(Date date);
	
	/**
	 * EnforcementJob enabled or not 
	 */
	boolean getEnabled();
	
	void setEnabled(boolean enabled);
	
	/**
	 * Agreement being enforced.
	 */
	IAgreement getAgreement();
	
	void setAgreement(IAgreement agreement);

}
