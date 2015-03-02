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

public interface IServiceProperty {

	/*
	 * Internal generated id
	 */
	Long getId();
	
	/**
	 * ServiceName (from the ServiceProperties element)
	 */
	public String getServiceName();
	
	/**
	 * Name of this ServiceProperty
	 */
	public String getName();
	
	/**
	 * Xsd type of this property
	 */
	public String getMetric();
	
	/**
	 * Reference to a field in the service terms. In our sla, it is a "conceptual" reference.
	 * @return
	 */
	public String getLocation();
	
}
