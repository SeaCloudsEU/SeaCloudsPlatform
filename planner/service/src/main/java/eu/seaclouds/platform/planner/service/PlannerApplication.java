package eu.seaclouds.platform.planner.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class PlannerApplication extends Application<PlannerConfiguration> {

    public static void main(String[] args) throws Exception {
        new PlannerApplication().run(args);
    }

    @Override
    public void run(PlannerConfiguration plannerConfiguration, Environment environment) throws Exception {
        PlanResource pr = new PlanResource( plannerConfiguration );
        RePlanResource rpr = new RePlanResource(plannerConfiguration.getDiscovererURL(),
                                                plannerConfiguration.getDeployableProviders());
        DamGenResource dgr = new DamGenResource(plannerConfiguration.getMonitorGeneratorURL(),
                                                plannerConfiguration.getMonitorGeneratorPort(),
                                                plannerConfiguration.getSlaGeneratorURL(),
                                                plannerConfiguration.getInfluxdbURL(),
                                                plannerConfiguration.getInfluxdbPort());

        environment.jersey().register(pr);
        environment.jersey().register(rpr);
        environment.jersey().register(dgr);

        //TODO: add health ckecks
    }
}
