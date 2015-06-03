/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.seaclouds.common.compose.objectmodel;

import eu.seaclouds.common.objectmodel.*;
import eu.seaclouds.common.objectmodel.apptypes.MySQL;
import eu.seaclouds.common.objectmodel.apptypes.Tomcat;
import eu.seaclouds.common.objectmodel.hosttypes.Compute;
import eu.seaclouds.common.objectmodel.hosttypes.Platform;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ObjectModelTests{

    @Test
    public void HelloTest(){
        assertEquals(true, true);
    }

    @Test
    public void AAMtest(){
        Application app = new Application("id", "appName", AppState.Idle, DesignState.Empty); //provide different constructors

        AppModule javaEEServer = new AppModule(app, "java_ee_server");
        javaEEServer.setHostType(Compute.class);

        AppModule db = new AppModule(app, "db");
        db.setHostType(Compute.class);
        db.addQoSRequirements(new Constraint("disk_size",
                ConstraintType.GreaterOrEqual, new Scalar(50, ScalarUnits.Gb)
        ));


        AppModule mysqlServer = new AppModule(app, "mysql_server");
        mysqlServer.setApplicationType(MySQL.class);

        mysqlServer.addProperty(new Feature<String>("name", "some_name"));
        mysqlServer.addProperty(new Feature<String>("user", "some_user"));
        mysqlServer.addProperty(new Feature<String>("password", "some_pwd"));
        mysqlServer.addProperty(new Feature<String>("version", "5.5.37"));

        Set<AppModule> mysqlLocationModules = Collections.EMPTY_SET;
        mysqlLocationModules.add(db);


        mysqlServer.setLocation(new Location(mysqlLocationModules)); //must be improved
        mysqlServer.addArtifact(new Artifact("createDB", Paths.get("files/db_create.sql"), ArtifactTypes.File));

        AppModule tomcatServer = new AppModule(app, "tomcat_server");
        tomcatServer.setApplicationType(Tomcat.class);

        tomcatServer.addProperty(new Feature("version", "7.0.53"));
        Set<AppModule> tomcatLocationModules = Collections.EMPTY_SET;
        tomcatLocationModules.add(javaEEServer);

        tomcatServer.setLocation(new Location(tomcatLocationModules));
        tomcatServer.addArtifact(new Artifact(null, Paths.get("chat-webApplication.war"), ArtifactTypes.War));


        Effector query = new Effector("query");
        query.addOwner(db);
        query.addQoSInfo(new Feature("execution_time", new Scalar(30, ScalarUnits.ms)));
        query.addQoSInfo(new Feature("benchmark_platform", "hp_cloud_services.2xl"));

        Effector operation = new Effector("operation", 2);
        operation.addQoSInfo(new Feature("execution_time", new Scalar(50, ScalarUnits.ms)));
        operation.addQoSInfo(new Feature("benchmark_platform", "hp_cloud_services.2xl"));

        operation.addQoSRequirements(new Constraint("response_time", ConstraintType.LessThan, new Scalar(2, ScalarUnits.sec)));
        operation.addQoSRequirements(new Constraint("availability", ConstraintType.GreaterOrEqual, 0.998));
        operation.addQoSRequirements(new Constraint("cost", ConstraintType.LessThan, new Scalar(200, ScalarUnits.EurosPerMonth)));
        operation.addQoSRequirements(new Constraint("workload", ConstraintType.LessOrEqual, new Scalar(50, ScalarUnits.ReqPerMin)));

        app.addModule(javaEEServer);
        app.addModule(db);
        app.addModule(mysqlServer);
        app.addModule(tomcatServer);
        app.addEffector(query);
        app.addEffector(operation);

    }

    @Test
    public void matchMakerExample(){
        Application app = new Application("id", "appName", AppState.Idle, DesignState.Empty);
        Set<Offering> offerings = Collections.EMPTY_SET;

        Map<AppModule, Collection<Offering>> matches = Collections.EMPTY_MAP;
        for(AppModule m : app.getModules()){
            if(m.getHostType().isAssignableFrom(Compute.class) ||
               m.getHostType().isAssignableFrom(Platform.class)){
                    Set<Offering> suitableOfferings = Collections.EMPTY_SET;
                    boolean validOffering = true;
                    for(Offering o : offerings){
                        for(Constraint c : m.getTechnicalRequirements()){
                            if(!c.checkConstraint(o.getFeature(c.getName()))) validOffering = false;
                        }
                        if(validOffering) suitableOfferings.add(o);
                    }
                    matches.put(m, suitableOfferings);
            }
        }

        //matches contains the suitable offering for each Compute and Platform module

    }

    @Test
    public void optimizerExample(){
        Application app = null;
        //TODO
        //a. [names of ] existing deployable modules in the application:
        // It has to be able to retrieve from the TOSCA model that the application is composed of two modules.
        // Expected outcome of the method: a list containing “PHP” and “database”.
        // Expected usage of the method: X(). (Part of the information depicted in Fig in 2.2.1 in D3.2)

        //not clear

        //b. Dependencies between modules:
        // It has to be able to retrieve from the TOSCA model the dependencies between modules,
        // for example, that module called “PHP” uses module called “database”.
        // Provided information: name of the module for which the dependencies are asked.
        // Expected usage of the method: X(“PHP”). [*] . (Part of the information depicted in Fig in 2.2.1 in D3.2)
        for(Effector e : app.getEffectors()){
            Collection<Dependency> dependencies = e.getDependencyInOtherEffectors();
            for(Dependency d:dependencies){
                //modules that depends on the effector e
                Collection<AppModule> dependentModules = d.getEffector().getOwners();
            }
        }

        //c. Operational profile of dependencies:
        // It has to be able to retrieve from the TOSCA model the operational profile between dependent modules.
        // For example, that module “PHP” makes 1 call in average to module “database”.
        // Provided information: name of requester module and name of requested module.
        // Expected usage of the method: X(“PHP”,”database”).(Part of the information depicted in Fig in 2.2.1 in D3.2)
        Effector e = null;
        e.getAvgUsageCount();

        //d. QoS properties of modules (benchmarks and scaling capabilities):
        // It has to be able to retrieve from the TOSCA model the QoS properties of modules,
        // such as its performance or possibility to scale horizontally/vertically.
        // Provided information: name of the module for which the QoS is requested.
        // Expected usage of the method X(“PHP”).

        //-> benchmarks are part of the effectors
        e.getQoSInfo(); //add direct access?

        AppModule m = null;
        m.getQoSRequirements();
        e.getQosRequirement();

        // e. QoS requirements of application:
        // It has to be able to retrieve from the TOSCA model the QoS requirements
        // of the application, such as its required response time, availability or
        // maximum cost. Expected usage of the method: X() if it is created a method
        // for each property, or X(“response_time”) if the method is generic for every
        // QoS requirement.
        for(AppModule mm : app.getModules()){
            mm.getQoSRequirements().get("response_time");
        }

        //f. Expected utilization/workload/numberOfRequestsPerMinute of the application (front-end).
        // It has to be able to retrieve from the TOSCA model the expected utilization
        // of the application, in requests per minute. Expected usage of the method X().
        for(AppModule mm : app.getModules()){
            mm.getQoSRequirements().get("workload");
        }

        //g. Number of cores of each IaaS cloud offer,
        // QoS properties of each cloud offer (availability, performance, cost):
        // It has to be able to retrieve from the TOSCA model the properties of
        // cloud offers, such as their cost, performance, availability
        // or number of cores for IaaS. Expected usage of the method:
        // if there is a method for each cloud property,
        // then X(cloudOffer) where cloudOffer is of the same class as items of
        // the set returned in previous bullet “e.” ; if the method is generic
        // for every cloud property, then X(cloudOffer, “availability”),
        // X(cloudOffer, “performance”), and so on.
        Offering o = null;
        o.getFeature("num_cpus");
        o.getFeature("availability");
        o.getFeature("cost");
        o.getFeature("performance");

        //h. Chosen cloud offer for module (for generating/reading information to/from DAM):
        // It has to be able to include on the TOSCA model the cloud offer chosen for executing
        // an application module. Expected usage of the method: X(“PHP”, cloudOffer) where
        // cloudOffer is of the same class as items of the set returned in previous bullet “e.”

        //see the matchmaker example

        //i. Number of instances to use for a given module:
        // It has to be able to include on the TOSCA model the number of instances
        // to acquire for the cloud offer chosen for a module.
        // Expected usage of the method: X(“PHP”, cloudOffer, numInstances)
        // where cloudOffer is of the same class as items of the set returned
        // in previous bullet “e.” and numInstances is an integer.
        // If convenient for TOSCA model managing purposes, this method may be
        // forced to be used after method in “i.” that has added a chosen cloud offer for a module.

        //this should be related to Location for the module. we have to check
        m.getHostedOn().getModules().size();
        //or m.getNumberOfInstances() defined as above

        //j. Arrival rate limits for which it is expected that the current configuration does no
        // longer satisfies the performance requirement if the cloud QoS behaves as expected
        // during planning: It has to be able to include on the TOSCA model the arrival rate
        // threshold values for each module for which it is expected that the application stops
        // satisfying its performance requirements. Expected usage of the method
        // X(“PHP”, cloudOffer, listOfThrestholds) where cloudOffer is of the same
        // class as items of the set returned in previous bullet “e.” and listOfThresholds
        // is a list of real/double values.

        //not clear
    }

    @Test
    public void adpExample(){

    }

    @Test
    public void damExample(){
        //TODO
    }
}