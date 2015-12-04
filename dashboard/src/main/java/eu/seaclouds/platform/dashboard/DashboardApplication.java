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

import eu.seaclouds.platform.dashboard.rest.*;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.ws.rs.client.Client;

public class DashboardApplication extends Application<DashboardConfiguration> {
    @Override
    public String getName() {
        return "SeaCloudsDashboard";
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

        // Routing API documentation
        bootstrap.addBundle(new SwaggerBundle<DashboardConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(DashboardConfiguration configuration) {
                SwaggerBundleConfiguration swaggerBundleConfiguration = configuration.getSwaggerBundleConfiguration();
                swaggerBundleConfiguration.setTitle("SeaClouds REST API");
                swaggerBundleConfiguration.setDescription("This API allows to manage all the project functionality");
                swaggerBundleConfiguration.setResourcePackage("eu.seaclouds.platform.dashboard.rest");
                swaggerBundleConfiguration.setContact("dev@seaclouds-project.eu");
                swaggerBundleConfiguration.setLicense("Apache 2.0");
                swaggerBundleConfiguration.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0");
                return swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(DashboardConfiguration configuration, Environment environment) throws Exception {
        // Generating  HTTP Clients
        Client jerseyClient = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());

        // Link HTTP Clients with the Factories
        configuration.getDeployerProxy().setJerseyClient(jerseyClient);
        configuration.getMonitorProxy().setJerseyClient(jerseyClient);
        configuration.getSlaProxy().setJerseyClient(jerseyClient);
        configuration.getPlannerProxy().setJerseyClient(jerseyClient);

        // Configuring HealthChecks
        DashboardHealthCheck healthCheck = new DashboardHealthCheck(configuration.getDeployerProxy(),
                configuration.getMonitorProxy(), configuration.getSlaProxy(), configuration.getPlannerProxy());
        environment.healthChecks().register(healthCheck.getName(), healthCheck);

        // Registering REST API Endpoints
        environment.jersey().register(new CoreResource(configuration.getDeployerProxy(),
                configuration.getMonitorProxy(), configuration.getPlannerProxy(),
                configuration.getSlaProxy()));
        environment.jersey().register(new DeployerResource(configuration.getDeployerProxy(), configuration.getMonitorProxy(), configuration.getSlaProxy(), configuration.getPlannerProxy()));
        environment.jersey().register(new MonitorResource(configuration.getMonitorProxy(), configuration.getDeployerProxy()));
        environment.jersey().register(new PlannerResource(configuration.getPlannerProxy()));
        environment.jersey().register(new SlaResource(configuration.getSlaProxy()));
        environment.jersey().register(new AamWriterResource());
    }



    public static void main(String[] args) throws Exception {
        new DashboardApplication().run(args);
    }
}
