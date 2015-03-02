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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

/*
 * 
 *  * GuaranteeTermStatus Status

 * VIOLATED  -> 0
 * FULFILLED -> 1
 * NON_DETERMINED -> 2	
 * 
 * 
 * GET /agreements/{agreementId}/status
 Accept: application/xml

 <GuaranteeTermsStatus AgreementId="$agreementId" value="">
 <GuaranteeTermsStatus>
 <GuaranteeTermStatus name="$gt_name" value="[0,1,2]"/>
 <GuaranteeTermStatus name="$gt_name" value="[0,1,2]"/>
 </GuaranteeTermsStatus>

 }*/


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "guaranteestatus")
@JsonPropertyOrder({ "AgreementId", "guaranteestatus", "guaranteeterms" })
@JsonRootName(value = "GuaranteeStatus")
public class GuaranteeTermsStatus {


	@XmlAttribute(name = "AgreementId")
	@JsonProperty("AgreementId")
	private String agreementId;

	@XmlAttribute(name = "value")
	@JsonProperty("guaranteestatus")
	private String value;

	@XmlElement(name = "guaranteetermstatus")
	@JsonProperty("guaranteeterms")
	private List<GuaranteeTermStatus> guaranteeTermsStatus;

	public String getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<GuaranteeTermStatus> getGuaranteeTermsStatus() {
		return guaranteeTermsStatus;
	}

	public void setGuaranteeTermsStatus(
			List<GuaranteeTermStatus> guaranteeTermsStatus) {
		this.guaranteeTermsStatus = guaranteeTermsStatus;
	}

}
