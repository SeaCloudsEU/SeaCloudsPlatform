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
import eu.seaclouds.platform.dashboard.config.DeployerFactory;
import eu.seaclouds.platform.dashboard.config.MonitorFactory;
import eu.seaclouds.platform.dashboard.config.PlannerFactory;
import eu.seaclouds.platform.dashboard.config.SlaFactory;
import io.dropwizard.Configuration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DashboardConfiguration extends Configuration {

    @Valid
    @NotNull
    private PlannerFactory planner = new PlannerFactory();
    
    @Valid
    @NotNull
    private DeployerFactory deployer = new DeployerFactory();
    
    @Valid
    @NotNull
    private MonitorFactory monitor = new MonitorFactory();
    
    @Valid
    @NotNull
    private SlaFactory sla = new SlaFactory();

    @JsonProperty("planner")
    public PlannerFactory getPlannerFactory() {
        return planner;
    }

    @JsonProperty("planner")
    public void setPlannerFactory(PlannerFactory factory) {
        this.planner = factory;
    }
    
    @JsonProperty("deployer")
    public DeployerFactory getDeployerFactory() {
        return deployer;
    }

    @JsonProperty("deployer")
    public void setDeployerFactory(DeployerFactory factory) {
        this.deployer = factory;
    }

    @JsonProperty("monitor")
    public MonitorFactory getMonitorFactory() {
        return monitor;
    }

    @JsonProperty("monitor")
    public void setMonitorConfigFactory(MonitorFactory factory) {
        this.monitor = factory;
    }

    @JsonProperty("sla")
    public SlaFactory getSlaFactory() {
        return sla;
    }

    @JsonProperty("sla")
    public void setSlaFactory(SlaFactory factory) {
        this.sla = factory;
    }
}
