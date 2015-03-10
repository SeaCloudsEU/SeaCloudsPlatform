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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;

import eu.atos.sla.datamodel.IBusinessValueList;
import eu.atos.sla.datamodel.ICompensation.IPenalty;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.datamodel.IPolicy;
import eu.atos.sla.datamodel.IViolation;

/**
 * A POJO Object that stores all the information from a GuaranteeTerm
 * 
 * @author Pedro Rey - Atos
 */

@Entity
@Table(name = "guarantee_term")
@NamedQueries({ @NamedQuery(name = "GuaranteeTerm.findAll", query = "SELECT p FROM GuaranteeTerm p") })
public class GuaranteeTerm implements IGuaranteeTerm, Serializable {
	
	private static final long serialVersionUID = -8140757088864002129L;
	private Long id;
	private String name;
	private String serviceName;
	private String serviceScope;
	private String kpiName;
	private String serviceLevel;
	private List<IViolation> violations;
	private List<IPenalty> penalties;
	private List<IPolicy> policies;
	private GuaranteeTermStatusEnum status;
	private IBusinessValueList businessValueList;

	private Date lastSampledDate;
	private Integer  samplingPeriodFactor;
	
	public GuaranteeTerm() {

		this.status = GuaranteeTermStatusEnum.NON_DETERMINED;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	@Column(name = "name")
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@Column(name = "service_name")
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override
	@Column(name = "service_scope")
	public String getServiceScope() {
		return serviceScope;
	}

	@Override
	public void setServiceScope(String serviceScope) {
		this.serviceScope = serviceScope;
	}

	@Override
	@Column(name = "kpi_name")
	public String getKpiName() {
		return kpiName;
	}

	@Override
	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}

	@Override
	@Column(name = "service_level")
	public String getServiceLevel() {
		return serviceLevel;
	}

	@Override
	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	@Override
	@Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	@OneToMany(targetEntity = Policy.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "guarantee_term_id", referencedColumnName = "id", nullable = true)
	public List<IPolicy> getPolicies() {
		return policies;
	}

	@Override
	public void setPolicies(List<IPolicy> policies) {
		this.policies = policies;
	}

	@Override
	@Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	@OneToMany(targetEntity = Violation.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "guarantee_term_id", referencedColumnName = "id", nullable = true)
	public List<IViolation> getViolations() {
		return violations;
	}

	@Override
	public void setViolations(List<IViolation> violations) {
		this.violations = violations;
	}

	@Override
	@Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	@OneToMany(targetEntity = Penalty.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "guarantee_term_id", referencedColumnName = "id", nullable = true)
	public List<IPenalty> getPenalties() {
		return penalties;
	}

	public void setPenalties(List<IPenalty> penalties) {
		this.penalties = penalties;
	}

	@Override
	@Column(name = "status", nullable = false)
	public GuaranteeTermStatusEnum getStatus() {
		return status;
	}

	@Override
	public void setStatus(GuaranteeTermStatusEnum status) {
		this.status = status;
	}

	@Override
	@OneToOne(targetEntity = BusinessValueList.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "business_value_id")
	public IBusinessValueList getBusinessValueList() {
		return businessValueList;
	}

	@Override
	public void setBusinessValueList(IBusinessValueList businessValueList) {
		this.businessValueList = businessValueList;
	}

	@Override
	@Column(name = "lastSampledDate", nullable = true)
	public Date getLastSampledDate() {
		return lastSampledDate;
	}

	@Override
	public void setLastSampledDate(Date lastSampledDate) {
		this.lastSampledDate = lastSampledDate;
	}

	@Override
	@Column(name = "samplingPeriodFactor", nullable = true)
	public Integer getSamplingPeriodFactor() {
		return samplingPeriodFactor;
	}

	@Override
	public void setSamplingPeriodFactor(Integer samplingPeriodFactor) {
		this.samplingPeriodFactor = samplingPeriodFactor;
	}
	
	
}
