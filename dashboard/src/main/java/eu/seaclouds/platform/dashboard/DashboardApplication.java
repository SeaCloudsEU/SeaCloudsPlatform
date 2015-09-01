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

import eu.seaclouds.platform.dashboard.resources.DeployerResource;
import eu.seaclouds.platform.dashboard.resources.MonitorResource;
import eu.seaclouds.platform.dashboard.resources.PlannerResource;
import eu.seaclouds.platform.dashboard.resources.SlaResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DashboardApplication extends Application<DashboardConfiguration> {
    public static void main(String[] args) throws Exception {
        new DashboardApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<DashboardConfiguration> bootstrap) {
        // Setting configuration from env variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        // Routing static assets files
        bootstrap.addBundle(new AssetsBundle("/webapp", "/", "index.html"));
        
    }

    @Override
    public void run(DashboardConfiguration configuration, Environment environment) throws Exception {
        ((DefaultServerFactory) configuration.getServerFactory()).setJerseyRootPath("/api/*");

        environment.jersey().register(new DeployerResource(configuration.getDeployerFactory()));
        environment.jersey().register(new MonitorResource(configuration.getMonitorFactory()));
        environment.jersey().register(new PlannerResource(configuration.getPlannerFactory()));
        environment.jersey().register(new SlaResource(configuration.getSlaFactory()));
    }
}
