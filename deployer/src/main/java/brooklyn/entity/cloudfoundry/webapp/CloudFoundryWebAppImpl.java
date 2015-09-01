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


import brooklyn.entity.Entity;
import brooklyn.entity.annotation.Effector;
import brooklyn.entity.annotation.EffectorParam;
import brooklyn.entity.cloudfoundry.CloudFoundryEntityImpl;
import brooklyn.event.feed.function.FunctionFeed;
import brooklyn.event.feed.function.FunctionPollConfig;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.time.Duration;
import com.google.common.base.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class CloudFoundryWebAppImpl extends CloudFoundryEntityImpl
        implements CloudFoundryWebApp {

    private static final Logger log = LoggerFactory.getLogger(CloudFoundryWebAppImpl.class);

    private volatile FunctionFeed usedNumberOfInstances;
    private volatile FunctionFeed usedDisk;
    private volatile FunctionFeed usedMemory;

    public CloudFoundryWebAppImpl() {
        super(MutableMap.of(), null);
    }

    public CloudFoundryWebAppImpl(Entity parent) {
        this(MutableMap.of(), parent);
    }

    public CloudFoundryWebAppImpl(Map properties) {
        this(properties, null);
    }

    public CloudFoundryWebAppImpl(Map properties, Entity parent) {
        super(properties, parent);
    }

    @Override
    public abstract Class getDriverInterface();

    @Override
    public PaasWebAppDriver getDriver() {
        return (PaasWebAppDriver) super.getDriver();
    }

    @Override
     public void init() {
        super.init();
        initAttributesValues();
    }

    private void initAttributesValues(){
        setAttribute(BOUND_SERVICES, new LinkedList<String>());
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();

        connectInstancesNumberSensor();
        connectDiskQuotaSensor();
        connectMemorySensor();
    }

    @Override
    protected void disconnectSensors(){
        super.disconnectSensors();

        disconnectInstancesNumberSensor();
        disconnectDiskQuotaSensor();
        disconnectMemorySensor();
    }

    /**
     * For connecting the {@link #INSTANCES_NUM} sensor.
     * <p>
     * Should be called inside {@link #connectSensors()}.
     *
     * @see #disconnectInstancesNumberSensor()
     */
    protected void connectInstancesNumberSensor() {
        usedNumberOfInstances = FunctionFeed.builder()
                .entity(this)
                .period(Duration.seconds(2))
                .poll(new FunctionPollConfig<Integer, Integer>(INSTANCES_NUM)
                        .onException(Functions.constant(0))
                        .callable(new Callable<Integer>() {
                            public Integer call() {
                                return getDriver().getInstancesNumber();
                            }
                        }))
                .build();
    }

    /**
     * For disconnecting the {@link #INSTANCES_NUM} feed.
     * <p>
     * Should be called from {@link #disconnectSensors()}.
     *
     * @see #connectInstancesNumberSensor()
     */
    protected void disconnectInstancesNumberSensor() {
        if (usedNumberOfInstances != null){
            usedNumberOfInstances.stop();
        }
        setAttribute(INSTANCES_NUM, 0);
    }

    /**
     * For connecting the {@link #DISK} sensor.
     * <p>
     * Should be called inside {@link #connectSensors()}.
     *
     * @see #disconnectDiskQuotaSensor()
     */
    protected void connectDiskQuotaSensor() {
        usedDisk = FunctionFeed.builder()
                .entity(this)
                .period(Duration.seconds(2))
                .poll(new FunctionPollConfig<Integer, Integer>(DISK)
                        .onException(Functions.constant(0))
                        .callable(new Callable<Integer>() {
                            public Integer call() {
                                return getDriver().getDisk();
                            }
                        }))
                .build();
    }

    /**
     * For disconnecting the {@link #DISK} feed.
     * <p>
     * Should be called from {@link #disconnectSensors()}.
     *
     * @see #connectDiskQuotaSensor()
     */
    protected void disconnectDiskQuotaSensor() {
        if (usedDisk != null){
            usedDisk.stop();
        }
        setAttribute(DISK, 0);
    }

    /**
     * For connecting the {@link #MEMORY} sensor.
     * <p>
     * Should be called inside {@link #connectSensors()}.
     *
     * @see #disconnectMemorySensor()
     */
    protected void connectMemorySensor() {
        usedMemory = FunctionFeed.builder()
                .entity(this)
                .period(Duration.seconds(2))
                .poll(new FunctionPollConfig<Integer, Integer>(MEMORY)
                        .onException(Functions.constant(0))
                        .callable(new Callable<Integer>() {
                            public Integer call() {
                                return getDriver().getMemory();
                            }
                        }))
                .build();
    }

    /**
     * For disconnecting the {@link #MEMORY} feed.
     * <p>
     * Should be called from {@link #disconnectSensors()}.
     *
     * @see #connectMemorySensor()
     */
    protected void disconnectMemorySensor() {
        if (usedMemory != null){
            usedMemory.stop();
        }
        setAttribute(MEMORY, 0);
    }

    @Override
    @Effector(description="Set an environment variable that can be retrieved by the web application")
    public void setEnv(@EffectorParam(name = "name", description = "Name of the variable") String key,
                       @EffectorParam(name = "value", description = "Value of the environment variable") String value) {
        PaasWebAppDriver driver = getDriver();
        if (driver != null) {
            driver.setEnv(key, value);   
        } else {
            log.error("Error setting environment variable {} = {} on entity {}", key, this.getEntityTypeName());
        }
    }

    @Override
    @SuppressWarnings("all")
    @Effector(description="Set the instances number that will be user by the web application")
    public void setInstancesNumber(@EffectorParam(name = "instancesNumber", description = "Number of " +
            "instances that are being used by the application") int instancesNumber){
        if(instancesNumber<=0){
            log.info("The number of instances should be greater than 0 in effector " +
                    "setInstancesNumbre of {} but was received {}", this, instancesNumber);
        }
        getDriver().changeInstancesNumber(instancesNumber);
    }

    @Override
    @SuppressWarnings("all")
    @Effector(description="Set the disk quota that will be used by the web application")
    public void setDiskQuota(@EffectorParam(name = "diskQuota", description = "Disk amount" +
            " that will be used by the web application") int diskQuota){
        if(diskQuota<=0){
            log.info("The disk amount should be greater than 0 in effector " +
                    "setInstancesNumbre of {} but was received {}", this, diskQuota);
        }
        getDriver().updateApplicationDiskQuota(diskQuota);
    }

    @Override
    @SuppressWarnings("all")
    @Effector(description="Set an Ram Memory limit for the web application")
    public void setAmountMemory(@EffectorParam(name = "memory", description = "Disk amount" +
            " that will be used by the web application") int memory){
        if(memory<=0){
            log.info("The memory amount should be greater than 0 in effector " +
                    "setInstancesNumbre of {} but was received {}", this, memory);
        }
        getDriver().updateApplicationMemory(memory);
    }


}
