package eu.seaclouds.platform.planner.service;

import alien4cloud.tosca.parser.ParsingException;
import com.codahale.metrics.annotation.Timed;
import eu.seaclouds.platform.planner.core.Planner;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

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
    private final String optimizerURL;

    public RePlanResource(String discovererURL, String optimizerURL)
    {
        this.discovererURL = discovererURL;
        this.optimizerURL = optimizerURL;
    }

    @GET
    @Timed
    public PlannerResponse replan(@QueryParam("aam") String aam, @QueryParam("dam") String dam){
       return getPlans(aam, dam);
    }


    private PlannerResponse getPlans(String aam, String dam){
        Planner p = new Planner(discovererURL, optimizerURL, aam, dam);
        String resp = "{}";
        try {
            resp = p.rePlan();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return new PlannerResponse(aam, resp);
    }

}
