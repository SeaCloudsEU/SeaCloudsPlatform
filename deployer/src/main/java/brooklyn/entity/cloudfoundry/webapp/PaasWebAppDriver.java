/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package brooklyn.entity.cloudfoundry.webapp;

import brooklyn.entity.cloudfoundry.PaasEntityDriver;

public interface PaasWebAppDriver extends PaasEntityDriver {

    //TODO delete?
    /**
     * Kills the process, ungracefully and immediately where possible (e.g. with `kill -9`).
     */
    void deleteApplication();

    /**
     * Allows to add a custim environment variable to an application
     * @param key
     * @param value
     */
    void setEnv(String key, String value);

    /**
     * Set the instances number that will be user by the web application
     * @param instancesNumber Number of instance that are being used by the application
     */
    void changeInstancesNumber(int instancesNumber);

    /**
     * Set the disk quota that will be used by the web application
     * @param diskQuota Disk amount that will be used by the web application
     */
    void updateApplicationDiskQuota(int diskQuota);

    /**
     * Set an Ram Memory limit for the web application
     * @param memory Disk amount that will be used by the web application
     */
    void updateApplicationMemory(int memory);

    /**
     * Return the number of instances that are used for an application.
     * @return
     */
    int getInstancesNumber();

    /**
     * Return the current disk quota used by the application.
     * @return
     */
    int getDisk();

    /**
     * Return the current assigned memory to the application.
     * @return
     */
    int getMemory();
}
