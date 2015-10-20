package eu.seaclouds.monitor.monitoringDamGeneratorService;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class ServiceLauncher extends Application<ServiceConfiguration>  {

    
    public static void main(String[] args) throws Exception  {

        new ServiceLauncher().run(args);

    }

    @Override
    public void run(ServiceConfiguration configuration, Environment environment)
            throws Exception {
        environment.jersey().register(new Service(configuration.getHost(), configuration.getPort()));

    }
}
