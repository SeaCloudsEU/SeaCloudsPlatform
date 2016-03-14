/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.platform.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.seaclouds.platform.dashboard.proxy.*;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DashboardConfiguration extends Configuration {

    @Valid
    @NotNull
    private final JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @NotNull
    private SwaggerBundleConfiguration swaggerBundleConfiguration = new SwaggerBundleConfiguration();

    @Valid
    @NotNull
    private PlannerProxy planner = new PlannerProxy();

    @Valid
    @NotNull
    private DeployerProxy deployer = new DeployerProxy();

    @Valid
    @NotNull
    private GrafanaProxy grafana;

    @Valid
    @NotNull
    private SlaProxy sla = new SlaProxy();

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("swaggerApi")
    public SwaggerBundleConfiguration getSwaggerBundleConfiguration() {
        return swaggerBundleConfiguration;
    }

    @JsonProperty("planner")
    public PlannerProxy getPlannerProxy() {
        return planner;
    }

    @JsonProperty("planner")
    public void setPlannerProxy(PlannerProxy factory) {
        planner = factory;
    }

    @JsonProperty("deployer")
    public DeployerProxy getDeployerProxy() {
        return deployer;
    }

    @JsonProperty("deployer")
    public void setDeployerProxy(DeployerProxy factory) {
        deployer = factory;
    }

    @JsonProperty("monitor.grafana")
    public void setGrafanaProxy(GrafanaProxy factory) {
        grafana = factory;
    }

    @JsonProperty("monitor.grafana")
    public GrafanaProxy getGrafanaProxy() {
        return grafana;
    }

    @JsonProperty("sla")
    public SlaProxy getSlaProxy() {
        return sla;
    }

    @JsonProperty("sla")
    public void setSlaProxy(SlaProxy factory) {
        sla = factory;
    }
}
