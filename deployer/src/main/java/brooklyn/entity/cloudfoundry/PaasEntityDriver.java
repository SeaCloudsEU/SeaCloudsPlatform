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
package brooklyn.entity.cloudfoundry;

import brooklyn.entity.drivers.EntityDriver;

public interface PaasEntityDriver extends EntityDriver {


    /**
     * Whether the entity components have started.
     */
    boolean isRunning();

    /**
     * Rebinds the driver to a pre-existing software process.
     */
    void rebind();

    /**
     * Performs software start (or queues tasks to do this)
     */
    void start();

    /**
     * Performs software restart (or queues tasks to do this).
     * Unlike stop/start implementations here are expected to update SERVICE_STATE for STOPPING and
     * STARTING as appropriate (but framework will set RUNNING afterwards, after detecting it is
     * running).
     *
     * @see brooklyn.entity.trait.Startable#restart()
     */
    void restart();

    /**
     * Performs software stop (or queues tasks to do this)
     *
     * @see brooklyn.entity.trait.Startable#stop()
     */
    void stop();


}
