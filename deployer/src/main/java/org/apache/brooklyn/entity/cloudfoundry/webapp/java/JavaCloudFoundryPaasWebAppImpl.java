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
package org.apache.brooklyn.entity.cloudfoundry.webapp.java;


import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebApp;
import org.apache.brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import org.apache.brooklyn.feed.function.FunctionFeed;
import org.apache.brooklyn.feed.function.FunctionPollConfig;
import org.apache.brooklyn.feed.http.HttpFeed;
import org.apache.brooklyn.feed.http.HttpPollConfig;
import org.apache.brooklyn.feed.http.HttpValueFunctions;
import org.apache.brooklyn.util.collections.MutableMap;
import org.apache.brooklyn.util.guava.Functionals;
import org.apache.brooklyn.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.Callable;


public class JavaCloudFoundryPaasWebAppImpl extends CloudFoundryWebAppImpl implements JavaCloudFoundryPaasWebApp {

    private static final Logger log = LoggerFactory.getLogger(JavaCloudFoundryPaasWebAppImpl.class);

    private volatile HttpFeed httpFeed;
    private FunctionFeed serverLatencyFeed, resourceLatencyFeed, requestPerSecondLatencyFeed;
    private Double lastTotalRequest = 0.0;

    public JavaCloudFoundryPaasWebAppImpl() {
        super(MutableMap.of(), null);
    }

    public JavaCloudFoundryPaasWebAppImpl(Entity parent) {
        this(MutableMap.of(), parent);
    }

    public JavaCloudFoundryPaasWebAppImpl(Map properties) {
        this(properties, null);
    }

    public JavaCloudFoundryPaasWebAppImpl(Map properties, Entity parent) {
        super(properties, parent);
    }

    @Override
    public Class getDriverInterface() {
        return JavaPaasWebAppDriver.class;
    }

    @Override
    public JavaPaasWebAppDriver getDriver() {
        return (JavaPaasWebAppDriver) super.getDriver();
    }

    @Override
    public String getBuildpack() {
        return getConfig(BUILDPACK);
    }

    @Override
    public Integer resize(Integer integer) {
        getDriver().changeInstancesNumber(integer);
        return getCurrentSize();
    }

    @Override
    public Integer getCurrentSize() {
        return getDriver().getInstancesNumber();
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();

        String managementUri = String.format("%s/monitoring?format=json",
                getAttribute(CloudFoundryWebApp.ROOT_URL));

        setAttribute(JavaCloudFoundryPaasWebApp.MONITOR_URL, managementUri);
        String monitorResource = this.getConfig(MAIN_MONITOR_RESOURCE);

        httpFeed = HttpFeed.builder()
                .entity(this)
                .period(200)
                .baseUri(managementUri)
                .poll(new HttpPollConfig<Long>(USED_MEMORY)
                        .checkSuccess(HttpValueFunctions.responseCodeEquals(200))
                        .onSuccess(HttpValueFunctions.<Long>jsonContentsFromPath("$.list[12].memoryInformations.usedMemory")))
                .poll(new HttpPollConfig<Double>(DURATION_SUM).onSuccess(
                        Functionals.chain(
                                HttpValueFunctions.stringContentsFunction(),
                                findAMonitorData(monitorResource, "durationsSum"))))
                .poll(new HttpPollConfig<Double>(RESOURCE_HITS).onSuccess(
                        Functionals.chain(
                                HttpValueFunctions.stringContentsFunction(),
                                findAMonitorData(monitorResource, "hits"))))
                .poll(new HttpPollConfig<Double>(SERVER_PROCESSING_TIME)
                        .checkSuccess(HttpValueFunctions.responseCodeEquals(200))
                        .onSuccess(HttpValueFunctions.<Double>jsonContentsFromPath("$.list[12].tomcatInformationsList[0].processingTime")))
                .poll(new HttpPollConfig<Double>(SERVER_REQUESTS)
                        .checkSuccess(HttpValueFunctions.responseCodeEquals(200))
                        .onSuccess(HttpValueFunctions.<Double>jsonContentsFromPath("$.list[12].tomcatInformationsList[0].requestCount")))
                .build();

        connectServiceLatencySensor();
        connectResourceLatencySensor();
        connectServiceRequestPerSecondSensor();
    }

    private void connectServiceLatencySensor() {
        serverLatencyFeed = FunctionFeed.builder()
                .entity(this)
                .period(Duration.seconds(2))
                .poll(new FunctionPollConfig<Double, Double>(SERVER_LATENCY)
                        .onException(Functions.constant(0.0))
                        .callable(new Callable<Double>() {
                            public Double call() {

                                Double total = getAttribute(SERVER_PROCESSING_TIME);
                                Double num = getAttribute(SERVER_REQUESTS);
                                return total / num;
                            }
                        }))
                .build();
    }

    private void connectServiceRequestPerSecondSensor() {
        requestPerSecondLatencyFeed = FunctionFeed.builder()
                .entity(this)
                .period(Duration.seconds(1))
                .poll(new FunctionPollConfig<Double, Double>(REQUEST_PER_SECOND)
                        .onException(Functions.constant(0.0))
                        .callable(new Callable<Double>() {
                            public Double call() {
                                Double currentRequestPerSecond = getAttribute(SERVER_REQUESTS) - lastTotalRequest;
                                if (currentRequestPerSecond >= 0) {
                                    lastTotalRequest = getAttribute(SERVER_REQUESTS);
                                    return currentRequestPerSecond;
                                } else {
                                    return getAttribute(SERVER_REQUESTS);
                                }
                            }
                        }))
                .build();
    }

    private void connectResourceLatencySensor() {
        resourceLatencyFeed = FunctionFeed.builder()
                .entity(this)
                .period(Duration.seconds(2))
                .poll(new FunctionPollConfig<Double, Double>(RESOURCE_LATENCY)
                        .onException(Functions.constant(0.0))
                        .callable(new Callable<Double>() {
                            public Double call() {
                                Double total = getAttribute(DURATION_SUM);
                                Double num = getAttribute(RESOURCE_HITS);
                                return total / num;
                            }
                        }))
                .build();
    }

    public Function<String, Double> findAMonitorData(final String resourceId, final String data) {
        return new Function<String, Double>() {
            @Nullable
            @Override
            public Double apply(@Nullable String jsonContent) {
                Double result = 0.0;
                Map resourceDescription =
                        getResourceDescription(jsonContent, resourceId);
                if (resourceDescription != null) {
                    result = new Double(resourceDescription.get(data).toString());
                }
                return result;
            }
        };
    }

    private Map getResourceDescription(String json, String resourceId) {
        JSONArray resource = findResourceById(json, resourceId);
        Map resourceDescription = null;

        if ((resource != null) && (resource.size() > 1)) {
            resourceDescription = (Map) resource.get(1);
        }
        return resourceDescription;
    }

    private JSONArray findResourceById(String json, String resourceId) {
        String path = "$.list[0].requests";
        JSONArray pathResult = JsonPath.read(json, path);

        if ((pathResult != null) && (pathResult.size() > 0)) {
            for (Object resourceDescription : pathResult) {
                if (((JSONArray) resourceDescription).get(0).equals(resourceId)) {
                    return ((JSONArray) resourceDescription);
                }
            }
        }
        return null;
    }


}
