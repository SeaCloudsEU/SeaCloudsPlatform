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
package eu.atos.sla.enforcement.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IEnforcementJob;
import eu.atos.sla.enforcement.AgreementEnforcement;

@Scope("prototype")
public class EnforcementTask implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(EnforcementTask.class);

	private IEnforcementJob job;
	@Autowired
	AgreementEnforcement agreementEnforcement;

	public EnforcementTask(){
		
	}

	public EnforcementTask(IEnforcementJob job) {
		this.job = job;
	}

	@Override
	public void run() {
		try{
            IAgreement agreement = job.getAgreement();

            agreementEnforcement.enforce(agreement, job.getLastExecuted(), false);
		}catch(Exception e){
			logger.error("Error with thread " + Thread.currentThread().getName(), e);
		}
	}
	

}	
