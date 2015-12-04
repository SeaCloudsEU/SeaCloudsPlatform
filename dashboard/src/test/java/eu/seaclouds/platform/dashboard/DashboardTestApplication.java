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

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import eu.seaclouds.platform.dashboard.rest.*;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;

public class DashboardTestApplication extends Application<DashboardTestConfiguration> {
    @Override
    public String getName() {
        return "SeaCloudsDashboard-Test";
    }

    @Override
    public void initialize(Bootstrap<DashboardTestConfiguration> bootstrap) {
    }

    @Override
    public void run(DashboardTestConfiguration configuration, Environment environment) throws Exception {
        // Generating  HTTP Clients
        Client jerseyClient = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());

        // Configuring HealthChecks
        environment.healthChecks().register(getName(), new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });

        // Link HTTP Clients with the Factories
        configuration.getDeployerProxy().setJerseyClient(jerseyClient);
        configuration.getMonitorProxy().setJerseyClient(jerseyClient);
        configuration.getSlaProxy().setJerseyClient(jerseyClient);
        configuration.getPlannerProxy().setJerseyClient(jerseyClient);
    }
}
