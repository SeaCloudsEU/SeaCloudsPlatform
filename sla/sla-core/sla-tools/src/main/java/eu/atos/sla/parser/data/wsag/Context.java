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
package eu.atos.sla.parser.data.wsag;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.atos.sla.parser.DateTimeDeserializerJSON;
import eu.atos.sla.parser.DateTimeSerializerJSON;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Context")
public class Context {
	
	/**
	 * ServiceProvider element must be one of these.
	 */
	public enum ServiceProvider {
		AGREEMENT_INITIATOR("AgreementInitiator"), 
		AGREEMENT_RESPONDER("AgreementResponder");
		
		String label;
		private ServiceProvider(String label) {
			
			this.label = label;
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
	
	@XmlElement(name = "AgreementInitiator")
	private String agreementInitiator;
	@XmlElement(name = "AgreementResponder")
	private String agreementResponder;
	@XmlElement(name = "ServiceProvider")
	private String serviceProvider;

	@JsonSerialize(using=DateTimeSerializerJSON.class)
	@JsonDeserialize(using=DateTimeDeserializerJSON.class)
	@XmlElement(name = "ExpirationTime")
	private Date  expirationTime;
	@XmlElement(name = "TemplateId")
	private String templateId;
	@XmlElement(name = "Service", namespace="http://sla.atos.eu")
	private String service;

	public Context() {
	}

	public String getAgreementInitiator() {
		return agreementInitiator;
	}

	public void setAgreementInitiator(String agreementInitiator) {
		this.agreementInitiator = agreementInitiator;
	}

	public String getAgreementResponder() {
		return agreementResponder;
	}

	public void setAgreementResponder(String agreementResponder) {
		this.agreementResponder = agreementResponder;
	}

	public String getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

}
