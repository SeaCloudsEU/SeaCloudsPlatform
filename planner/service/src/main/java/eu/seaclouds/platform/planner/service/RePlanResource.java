package eu.seaclouds.platform.planner.service;

import alien4cloud.tosca.parser.ParsingException;
import com.codahale.metrics.annotation.Timed;
import eu.seaclouds.platform.planner.core.Planner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Arrays;

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

@Path("/replan")
@Produces(MediaType.APPLICATION_JSON)
public class RePlanResource {

    private final String discovererURL;
    private final String[] deployableProviders;

    static Logger log = LoggerFactory.getLogger(RePlanResource.class);

    private Planner planner;

    public RePlanResource(String discovererURL, String[] deployableProviders)
    {
        this.discovererURL = discovererURL;
        this.deployableProviders = deployableProviders;
        this.planner = new Planner(discovererURL, Arrays.asList(deployableProviders));
    }

    @GET
    @Timed
    public PlannerResponse replan(@QueryParam("aam") String aam, @QueryParam("adp") String adp, String[] failingModules){
       return getPlans(aam, adp, failingModules);
    }


    private PlannerResponse getPlans(String aam, String adp, String[] failingModules){
        String[] resp = new String[0];
        try {
            resp = planner.fetchAndRePlan(aam, Arrays.asList(failingModules));
        } catch (IOException e) {
            log.error(e.getCause().getMessage(), e);
        } catch (ParsingException e) {
            log.error(e.getCause().getMessage(), e);
        }
        return new PlannerResponse(aam, resp);
    }

}
