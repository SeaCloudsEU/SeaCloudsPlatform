package eu.seaclouds.platform.planner.service;

import com.codahale.metrics.annotation.Timed;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.Planner;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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

@Path("/damgen")
@Produces(MediaType.APPLICATION_JSON)
public class DamGenResource {

    private final String monitorGeneratorURL;
    private final String slaGeneratorURL;
    private final String influxdbURL;

    private final String[] genURLs;
    private final String monitorGeneratorPort;
    private final String influxdbPort;

    public DamGenResource(String monitorGeneratorURL,
                          String monitorGeneratorPort,
                          String slaGeneratorURL,
                          String influxdbURL,
                          String influxdbPort)
    {
        this.monitorGeneratorURL = monitorGeneratorURL;
        this.slaGeneratorURL = slaGeneratorURL;
        this.monitorGeneratorPort = monitorGeneratorPort;
        this.influxdbURL = influxdbURL;
        this.influxdbPort = influxdbPort;

        this.genURLs = new String[] {
                monitorGeneratorURL,
                slaGeneratorURL,
        };

    }

    @POST
    @Timed
    public DamGeneratorResponse damGenPost(String adp){
        return new DamGeneratorResponse(DamGenerator.generateDam(adp, monitorGeneratorURL, monitorGeneratorPort, slaGeneratorURL, influxdbURL, influxdbPort));
    }

    @GET
    @Timed
    public DamGeneratorResponse damgen(@QueryParam("adp") String adp){
        return new DamGeneratorResponse(DamGenerator.generateDam(adp, monitorGeneratorURL, monitorGeneratorPort, slaGeneratorURL, influxdbURL, influxdbPort));
    }

}
