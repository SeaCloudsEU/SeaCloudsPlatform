package eu.seaclouds.platform.repository;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import eu.seaclouds.platform.repository.resources.DataResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class RepositoryApplication extends Application<RepositoryConfiguration> {

    public static void main(String[] args) throws Exception {
        new RepositoryApplication().run(args);
    }
    
    public RepositoryApplication() {
    }
    
    @Override
    public void run(RepositoryConfiguration configuration, Environment environment) throws Exception {
        DataResource data = new DataResource();
        
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(data);
        
    }

    @Override
    public String getName() {
        return "repository-service";
    }
}
