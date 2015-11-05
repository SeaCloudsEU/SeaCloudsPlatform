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


import eu.seaclouds.platform.dashboard.config.PlannerFactory;
import eu.seaclouds.platform.dashboard.http.HttpGetRequestBuilder;
import eu.seaclouds.platform.dashboard.http.HttpPostRequestBuilder;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

@Path("/planner")
@Produces(MediaType.APPLICATION_JSON)
public class PlannerResource {
    static Logger log = LoggerFactory.getLogger(PlannerResource.class);

    private PlannerFactory planner;

    public PlannerResource(PlannerFactory planner) {
        this.planner = planner;
    }


    @POST
    @Path("plan")
    public Response getPlan(String aam) {
        try {
            if (aam == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {

                String plannerResponse = new HttpPostRequestBuilder()
                        .entity(new StringEntity(aam))
                        .host(planner.getEndpoint())
                        .path("/planner/plan")
                        .build();

                return Response.ok(plannerResponse.toString()).build();
            }
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }
}
