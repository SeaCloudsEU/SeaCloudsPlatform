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
import java.util.List;

import eu.atos.sla.datamodel.ICompensation.IPenalty;

/**
 * A GuaranteeTerm in ws-agreement is defined as:
 * <pre>{@code
 * <wsag:GuaranteeTerm Name="xs:string" Obligated="wsag:ServiceRoleType">
 *   <wsag:ServiceScope ServiceName="xs:string">xs:any?</wsag:ServiceScope>*
 *   <wsag:QualifyingCondition>xs:any</wsag:QualifyingCondition>?
 *   <wsag:ServiceLevelObjective>
 *     <wsag:KPITarget>
 *       <wsag:KPIName>xs:string</wsag:KPIName>
 *       <wsag:CustomServiceLevel>xs:any</wsag:CustomServiceLevel>
 *     </wsag:KPITarget>
 *   </wsag:ServiceLevelObjective>
 * </wsag:GuaranteeTerm>
 * }</pre>
 * 
 * The GT is "interpreted" in the core as:
 * <ul>
 * <li>Name: is a name to identify the term between all the terms.
 * <li>Obligated: it is always considered as "Provider", despite the attribute value.
 * <li>ServiceScope: the scope where the term takes into account. The serviceName refers 
 *   to a serviceName defined in the agreement. The text, if set, describe the element/service term
 *   this guarantee term applies to. <b>It is a restriction of the core to have one ServiceScope per
 *   GT as much</b>.
 * <li>QualifyingCondition. When this GT is going to be enforced. This is provider domain value.
 * <li>KPIName: The name of a kpi
 * <li>CustomServiceLevel: the constraint that the KPI has to fulfill
 * </ul>
 * 
 * Valid constraints:
 * <ul>
 * <li>current-response-time < desired-response-time
 * <li>current-response-time < 100
 * </ul>
 * 
 * In these constraints, current-response-time" and "desired-response-time" are service 
 * properties (wsag:ServiceProperties/Variable). The changing value is considered to be retrieved from
 * a external monitoring module. The location of the static value (wsag:Variable/Location) is considered 
 * to be the place where it is defined in the ServiceDescriptionTerms. 
 * 
 * The constraints are given a name with the KPIName. In the constraints above, a possible KPIName could be 
 * "response-time".
 * 
 * <b>It is a restriction of the core that there can be only one "changing variable" per ServiceLevelObjective</b>.
 * 
 * @author rsosa
 *
 */
public interface IGuaranteeTerm {

	public static enum GuaranteeTermStatusEnum {
		FULFILLED, VIOLATED, NON_DETERMINED
	}
	
	public static int ENFORCED_AT_END = -1;

	/*
	 * Internal generated ID
	 */
	Long getId();

	/**
	 * Name of this guarantee term.
	 */
	void setName(String name);

	/**
	 * Name of this guarantee term.
	 */
	String getName();

	/**
	 * Name of service this guarantee term applies to.
	 */
	void setServiceName(String name);

	/**
	 * Name of service this guarantee term applies to.
	 */
	String getServiceName();

	/**
	 * Describes to what service element specifically a guarantee term applies.
	 */
	String getServiceScope();

	void setServiceScope(String serviceScope);

	/**
	 * KPI of the Service level objective associated with this guarantee term.
	 */
	String getKpiName();

	void setKpiName(String kpiName);

	/**
	 * The service level (the constraint) of this guarantee term.
	 */
	String getServiceLevel();

	void setServiceLevel(String serviceLevel);

	/**
	 * The violations detected for this guarantee term.
	 */
	List<IViolation> getViolations();

	void setViolations(List<IViolation> violations);

	/**
	 * The policies for this guarantee term.
	 * 
	 * @return
	 */
	List<IPolicy> getPolicies();

	void setPolicies(List<IPolicy> policies);

	/**
	 * Guarantee term status
	 */
	GuaranteeTermStatusEnum getStatus();

	void setStatus(GuaranteeTermStatusEnum status);
	
	/**
	 * BusinessValueList: business values, each expressing a different value aspect of the objective.
	 */
	IBusinessValueList getBusinessValueList();
	
	void setBusinessValueList(IBusinessValueList businessValueList);
	
	
	/**
	 * LastSampledDate: date when the variable was read.
	 */
	public Date getLastSampledDate() ;
	public void setLastSampledDate(Date lastSampledDate) ;

	/**
	 * Desired sampling period: multiple of the sampling configuration.
	 * it should be a integer 
	 */	
	public Integer getSamplingPeriodFactor() ;
	public void setSamplingPeriodFactor(Integer samplingPeriodFactor) ;

	/**
	 * List of penalties that have been generated for this guarantee term
	 */
	List<IPenalty> getPenalties();
	
}