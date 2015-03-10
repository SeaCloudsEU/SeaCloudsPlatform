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
package eu.atos.sla.datamodel.bean;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import eu.atos.sla.datamodel.ICompensation;

@MappedSuperclass
public abstract class Compensation implements ICompensation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "uuid")
	private String uuid;

	@Column(name = "agreement_id")
	private String agreementId;
	
	@Column(name = "datetime")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date datetime;

	@Column(name = "kpi_name")
	private String kpiName;
	
	public Compensation() {
		uuid = "";
		
		agreementId = "";
		datetime = new Date(0);
		kpiName = "";
	}
	
	/**
	 * Creates a new Compensation with autogenerated uuid.
	 */
	public Compensation(String agreementId, Date datetime, String kpiName) {

		checkNotNull(agreementId, "agreementId");
		checkNotNull(datetime, "datetime");
		checkNotNull(kpiName, "kpiName");
		
		this.agreementId = agreementId;
		this.datetime = datetime;
		this.kpiName = kpiName;

		this.uuid = UUID.randomUUID().toString();
	}
	
	private void checkNotNull(Object o, String property) {
		if (o == null) {
			throw new NullPointerException(property + " cannot be null");
		}
	}

	@Override
	public Long getId() {
		
		return id;
	}

	@Override
	public String getUuid() {
		
		return uuid;
	}

	@Override
	public String getAgreementId() {
		
		return agreementId;
	}

	@Override
	public Date getDatetime() {
		
		return datetime;
	}
	
	@Override
	public String getKpiName() {
		return kpiName;
	}
	
	@Override
	public String toString() {
		return String.format(
				"Compensation [uuid=%s, agreementId=%s, datetime=%s, kpiName=%s]", 
				uuid, agreementId, datetime, kpiName);
	}
}
