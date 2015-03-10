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
package eu.atos.sla.dao;

import java.util.List;

import eu.atos.sla.datamodel.IService;

/**
 * DAO interface to access to the Service information
 * 
 * @author Pedro Rey - Atos
 * 
 */
public interface IServiceDAO  {

	/**
	 * Returns the Service from the database by its Id
	 * 
	 * @param id
	 *            of the Service
	 * @return the corresponding Service from the database
	 */
	public IService getById(Long id);

	/**
	 * Returns the Service from the database by its UUID
	 * 
	 * @param id
	 *            of the Service
	 * @return the corresponding Service from the database
	 */
	public IService getByUUID(String uuid);

	/**
	 * Returns the Service from the database by its name
	 * 
	 * @param id
	 *            of the Service
	 * @return the corresponding Service from the database
	 */
	public IService getByName(String serviceName);

	/**
	 * Returns all the Service stored in the database
	 * 
	 * @return all the Service stored in the database
	 */
	public List<IService> getAll();

	/**
	 * Stores a Service into the database
	 * 
	 * @param Service
	 *            - Service to be saved.
	 * @return <code>true</code> if the ServiceType was saved correctly
	 * @throws Exception 
	 */
	public IService save(IService service);

	/**
	 * Updates a Service in the database
	 * 
	 * @param Service
	 *            - Service to be updated
	 * @return <code>true</code> if the Service was saved correctly
	 */
	public boolean update(IService service);

	/**
	 * Deletes a Service from the database
	 * 
	 * @param Service
	 *            to be deleted
	 * @return <code>true</code> if the Service was deleted correctly
	 */
	public boolean delete(IService service);

}
