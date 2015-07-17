/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
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

import eu.atos.sla.datamodel.IServiceProperties;
import eu.atos.sla.datamodel.bean.ServiceProperties;

/**
 * DAO interface to access to the ServiceProperties information
 * 
 * 
 */
public interface IServicePropertiesDAO  {

    /**
     * Returns the ServiceProperties from the database by its Id
     * 
     * @param id
     *            of the ServiceProperties
     * @return the corresponding ServiceProperties from the database
     */
    public ServiceProperties getById(Long id);

    /**
     * Returns the ServiceProperties from the database by its name
     * 
     * @param id
     *            of the ServiceProperties
     * @return the corresponding Service from the database
     */
    public IServiceProperties getByName(String serviceName);

    /**
     * Returns all the ServiceProperties stored in the database
     * 
     * @return all the ServiceProperties stored in the database
     */
    public List<IServiceProperties> getAll();

    /**
     * Stores a ServiceProperties into the database
     * 
     * @param ServiceProperties to be saved.
     * @return <code>true</code> if the ServiceProperties was saved correctly
     * @throws Exception 
     */
    public IServiceProperties save(IServiceProperties serviceProperties);

    /**
     * Updates a ServiceProperties in the database
     * 
     * @param ServiceProperties
     *            - ServiceProperties to be updated
     * @return <code>true</code> if the Service was saved correctly
     */
    public boolean update(IServiceProperties serviceProperties);

    /**
     * Deletes a ServiceProperties from the database
     * 
     * @param ServiceProperties
     *            to be deleted
     * @return <code>true</code> if the ServiceProperties was deleted correctly
     */
    public boolean delete(IServiceProperties serviceProperties);

}
