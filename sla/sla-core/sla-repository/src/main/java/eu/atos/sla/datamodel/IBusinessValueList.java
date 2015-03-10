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

import java.util.List;

import eu.atos.sla.datamodel.ICompensationDefinition.IPenaltyDefinition;

public interface IBusinessValueList {

	/*
	 * Internally generated id
	 */
	Long getId();

	/**
	 * Relative importance of meeting an objective.
	 * 
	 * This core assumes the higher, the more important, with 0 as minimum value.
	 * @return
	 */
	public int getImportance();
	
	public List<IPenaltyDefinition> getPenalties();
	
	public void addPenalty(IPenaltyDefinition penalty);
}
