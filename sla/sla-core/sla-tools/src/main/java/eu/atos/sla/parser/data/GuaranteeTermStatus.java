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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;


/*
 * 
 * GuaranteeTermStatus 

 * VIOLATED  -> 0
 * FULFILLED -> 1
 * NON_DETERMINED -> 2	


 <GuaranteeTermStatus name="$gt_name" value="[0,1,2]"/>

 }*/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "guaranteeterm")
@JsonRootName("GuaranteeTermStatus")
public class GuaranteeTermStatus {


	@XmlAttribute(name = "name")
	@JsonProperty("name")
	private String name;
	

	@XmlAttribute(name = "value")
	@JsonProperty("status")
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
