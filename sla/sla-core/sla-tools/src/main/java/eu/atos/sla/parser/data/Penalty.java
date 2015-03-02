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
package eu.atos.sla.parser.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;
import eu.atos.sla.parser.DateTimeDeserializerJSON;
import eu.atos.sla.parser.DateTimeSerializerJSON;

/**
 * A POJO Object that stores all the information from a Penalty
 * 
 * @author rsosa
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "penalty")
public class Penalty  {

	@XmlElement(name = "uuid")
	private String uuid;
	@XmlElement(name = "agreement")
	private String agreementId;
	@JsonSerialize(using=DateTimeSerializerJSON.class)
	@JsonDeserialize(using=DateTimeDeserializerJSON.class)
	@XmlElement(name = "datetime")
	private Date datetime;
	@XmlElement(name = "definition")
	private eu.atos.sla.parser.data.wsag.custom.Penalty definition;

	public Penalty() {
	}
	
	public Penalty(IPenalty penalty) {
		this.uuid = penalty.getUuid();
		this.agreementId = penalty.getAgreementId();
		this.datetime = penalty.getDatetime();
		
		IPenaltyDefinition def = penalty.getDefinition();
		this.definition = new eu.atos.sla.parser.data.wsag.custom.Penalty(
				def.getAction(), def.getValueExpression(), def.getValueUnit(), def.getValidity());
	}

	public String getUuid() {
		return uuid;
	}

	public String getAgreementId() {
		return agreementId;
	}

	public Date getDatetime() {
		return datetime;
	}

	public eu.atos.sla.parser.data.wsag.custom.Penalty getDefinition() {
		return definition;
	}
}