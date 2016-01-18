package eu.seaclouds.platform.discoverer.service;

import com.mongodb.MongoClient;
import eu.seaclouds.platform.discoverer.api.*;
import eu.seaclouds.platform.discoverer.core.Discoverer;
import eu.seaclouds.platform.discoverer.core.DiscovererConfiguration;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class DiscovererApplication extends Application<DiscovererConfiguration> {

    public static void main(String[] args) throws Exception {
        new DiscovererApplication().run(args);
    }

    @Override
    public void run(DiscovererConfiguration configuration, Environment environment) {

        MongoClient mongoClient = new MongoClient(configuration.getDatabaseURL(), configuration.getDatabasePort());

        Discoverer discoverer = new Discoverer(mongoClient, configuration.getActiveCrawlers());

        environment.jersey().register(new FetchAPI(discoverer));          /* /fetchOffer */
        environment.jersey().register(new FetchAllAPI(discoverer));       /* /fetch_all */
        environment.jersey().register(new DeleteAPI(discoverer));         /* /delete */
        environment.jersey().register(new StatisticsAPI(discoverer));     /* /statistics */
        environment.jersey().register(new RefreshAPI(discoverer));        /* /refresh */
        environment.jersey().register(new FetchIfAPI(discoverer));
    }
}
