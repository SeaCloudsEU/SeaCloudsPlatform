package eu.seaclouds.platform.planner.service;

import com.codahale.metrics.annotation.Timed;
import eu.seaclouds.platform.planner.core.DamGenerator;
import eu.seaclouds.platform.planner.core.DamGeneratorConfigBag;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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

    private final String[] genURLs;
    private final String monitorGeneratorPort;

    private final String influxdbURL;
    private final String influxdbPort;
    private final String influxdbDatabase;
    private final String influxdbUsername;
    private final String influxdbPassword;

    private final String grafanaEndpoint;
    private final String grafanaUsername;
    private final String grafanaPassword;

    public DamGenResource(String monitorGeneratorURL,
                          String monitorGeneratorPort,
                          String slaGeneratorURL,
                          String influxdbURL,
                          String influxdbPort,
                          String influxdbDatabase,
                          String influxdbUsername,
                          String influxdbPassword,
                          String grafanaUsername,
                          String grafanaPassword,
                          String grafanaEndpoint) {
        this.monitorGeneratorURL = monitorGeneratorURL;
        this.slaGeneratorURL = slaGeneratorURL;
        this.monitorGeneratorPort = monitorGeneratorPort;
        this.influxdbURL = influxdbURL;
        this.influxdbPort = influxdbPort;
        this.influxdbDatabase = influxdbDatabase;
        this.influxdbUsername = influxdbUsername;
        this.influxdbPassword = influxdbPassword;
        this.grafanaUsername = grafanaUsername;
        this.grafanaPassword = grafanaPassword;
        this.grafanaEndpoint = grafanaEndpoint;

        this.genURLs = new String[]{
                monitorGeneratorURL,
                slaGeneratorURL,
        };
    }

    private DamGeneratorConfigBag getDamGeneratorConfigBag(){
        return new DamGeneratorConfigBag.Builder()
                .monitorUrl(monitorGeneratorURL)
                .monitorPort(monitorGeneratorPort)
                .slaUrl(slaGeneratorURL)
                .influxdbUrl(influxdbURL)
                .influxdbPort(influxdbPort)
                .influxdbDatabase(influxdbDatabase)
                .influxdbUsername(influxdbUsername)
                .influxdbPassword(influxdbPassword)
                .grafanaUsername(grafanaUsername)
                .grafanaPassword(grafanaPassword)
                .grafanaEndpoint(grafanaEndpoint)
                .build();
    }

    @POST
    @Timed
    public DamGeneratorResponse damGenPost(String adp) {
        DamGenerator damGenerator = new DamGenerator(getDamGeneratorConfigBag());
        return new DamGeneratorResponse(damGenerator.generateDam(adp));
    }

    @GET
    @Timed
    public DamGeneratorResponse damgen(@QueryParam("adp") String adp) {
        DamGenerator damGenerator = new DamGenerator(getDamGeneratorConfigBag());
        return new DamGeneratorResponse(damGenerator.generateDam(adp));
    }

}
