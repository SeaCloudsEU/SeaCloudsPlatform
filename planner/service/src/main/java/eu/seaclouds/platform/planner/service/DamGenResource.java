package eu.seaclouds.platform.planner.service;

import com.codahale.metrics.annotation.Timed;
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
    private final String credentialGeneratorURL;

    private final String[] genURLs;

    public DamGenResource(String monitorGeneratorURL,
                          String slaGeneratorURL,
                          String credentialGeneratorURL) //TODO: add configuration url
    {
        this.monitorGeneratorURL = monitorGeneratorURL;
        this.credentialGeneratorURL = credentialGeneratorURL;
        this.slaGeneratorURL = slaGeneratorURL;

        this.genURLs = new String[] {
                credentialGeneratorURL,
                monitorGeneratorURL,
                slaGeneratorURL,
        };

    }

    @GET
    @Timed
    public DamGeneratorResponse damgen(@QueryParam("adp") String adp){
        return new DamGeneratorResponse(new Planner(genURLs, adp).generateDam());
    }

}
