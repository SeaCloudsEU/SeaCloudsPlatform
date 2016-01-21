/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.seaclouds.platform.dashboard.proxy.DeployerProxy;
import eu.seaclouds.platform.dashboard.proxy.MonitorProxy;
import eu.seaclouds.platform.dashboard.proxy.PlannerProxy;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DashboardTestConfiguration extends Configuration {
    @Valid
    @NotNull
    private final JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @Valid
    @NotNull
    private PlannerProxy planner = new PlannerProxy();

    @Valid
    @NotNull
    private DeployerProxy deployer = new DeployerProxy();

    @Valid
    @NotNull
    private MonitorProxy monitor = new MonitorProxy();

    @Valid
    @NotNull
    private SlaProxy sla = new SlaProxy();

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("planner")
    public PlannerProxy getPlannerProxy() {
        return planner;
    }

    @JsonProperty("planner")
    public void setPlannerProxy(PlannerProxy factory) {
        this.planner = factory;
    }

    @JsonProperty("deployer")
    public DeployerProxy getDeployerProxy() {
        return deployer;
    }

    @JsonProperty("deployer")
    public void setDeployerProxy(DeployerProxy factory) {
        this.deployer = factory;
    }

    @JsonProperty("monitor.manager")
    public MonitorProxy getMonitorProxy() {
        return monitor;
    }

    @JsonProperty("monitor.manager")
    public void setMonitorConfigProxy(MonitorProxy factory) {
        this.monitor = factory;
    }

    @JsonProperty("sla")
    public SlaProxy getSlaProxy() {
        return sla;
    }

    @JsonProperty("sla")
    public void setSlaProxy(SlaProxy factory) {
        this.sla = factory;
    }
}
