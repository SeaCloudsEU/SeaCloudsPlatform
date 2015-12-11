package eu.seaclouds.platform.planner.service;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seaclouds.monitor.monitoringdamgenerator.MonitoringInfo;
import eu.seaclouds.platform.planner.core.DamGenerator;
import it.polimi.tower4clouds.rules.MonitoringRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@Path("/monitoringrules")
@Produces(MediaType.APPLICATION_XML)
public class RulesResource {

    public RulesResource() {}

    @GET
    @Path("/{id}")
    @Timed
    public MonitoringRules getMonitoingRules(@PathParam("id") String id){
        return DamGenerator.monitoringInfoByApplication.get(id).getApplicationMonitoringRules();
    }
}
