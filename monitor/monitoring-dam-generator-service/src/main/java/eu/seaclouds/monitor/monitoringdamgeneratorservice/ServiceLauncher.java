package eu.seaclouds.monitor.monitoringdamgeneratorservice;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ServiceLauncher extends Application<ServiceConfiguration> {

    public static void main(String[] args) throws Exception {

        new ServiceLauncher().run(args);

    }

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        // Setting configuration from env variables
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(ServiceConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new Service(configuration.getMonitorHost(), configuration.getMonitorPort(),
                configuration.getInfluxdbHost(), configuration.getInfluxdbPort()));

    }
}
