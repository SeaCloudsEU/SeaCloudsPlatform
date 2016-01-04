package eu.seaclouds.monitor.datacollector;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MainDc extends Application<DCConfiguration>  {
    
    public static void main(String[] args) throws Exception {

        MainDc mainInstance = new MainDc();
        
        mainInstance.run(args);

    }
    
    @Override
    public void initialize(Bootstrap<DCConfiguration> bootstrap) {
        
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        
    }

    @Override
    public void run(DCConfiguration configuration, Environment environment) {
        
        
        environment.jersey().register(new DcService(configuration.getResources_keep_alive_period(),
                configuration.getDc_sync_period(),
                configuration.getManager_ip(),
                configuration.getManager_port()));
        
    }

}
