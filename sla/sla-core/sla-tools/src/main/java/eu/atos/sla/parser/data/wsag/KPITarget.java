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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "KPITarget")
public class KPITarget {

	@XmlElement(name = "KPIName", required=true, nillable=false)
	private String kpiName;
	@XmlElement(name = "CustomServiceLevel", required=true, nillable=false)
	private String customServiceLevel;
/*egarrido, a borrar por no se usa	@XmlElement(name = "target") 
	private String target;
	*/

	public String getKpiName() {
		return kpiName;
	}

	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}

	public String getCustomServiceLevel() {
		return customServiceLevel;
	}

	public void setCustomServiceLevel(String customServiceLevel) {
		this.customServiceLevel = customServiceLevel;
	}
/*
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}*/

}
