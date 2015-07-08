/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.resources;


import com.google.gson.JsonObject;
import eu.seaclouds.platform.dashboard.config.PlannerFactory;
import eu.seaclouds.platform.dashboard.http.HttpPostRequestBuilder;
import eu.seaclouds.platform.planner.optimizer.Optimizer;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/planner")
@Produces(MediaType.APPLICATION_JSON)
public class PlannerResource {
    static Logger log = LoggerFactory.getLogger(PlannerResource.class);
    
    public static int OPTIMIZER_MAX_PLANS = 3;
    
    private PlannerFactory planner;
    
    public PlannerResource(PlannerFactory planner){
        this.planner = planner;
    }
    
    @POST
    @Path("matchmake")
    public Response matchmake(String yaml) {
        
        if (yaml != null) {

            String plannerResponse = null;
            try {

                JsonObject plannerInput = new JsonObject();
                plannerInput.addProperty("yaml", yaml);
                plannerResponse = new HttpPostRequestBuilder()
                        .host(planner.getEndpoint())
                        .path("/service")
                        .addParam("aam_json", plannerInput.toString())
                        .build();
                return Response.ok(plannerResponse).build();

            } catch (IOException | URISyntaxException e) {
                return Response.serverError().build();
            }


        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    @POST
    @Path("optimize")
    public Response optimize(String plan) {
        try {
            checkNotNull(plan);
            Optimizer optimizer = new Optimizer(OPTIMIZER_MAX_PLANS, SearchMethodName.BLINDSEARCH);
            String[] candidateDams = optimizer.optimize(plan, planner.getServiceOfferings());
            return Response.ok(candidateDams).build();
        } catch (URISyntaxException | IOException e) {
            return Response.serverError().build();
        }
    }
}
