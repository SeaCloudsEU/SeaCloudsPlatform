/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.platform.dashboard.rest;


import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.parser.data.GuaranteeTermsStatus;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationData;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import eu.seaclouds.platform.dashboard.proxy.DeployerProxy;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/deployer")
@Api("/deployer")
public class DeployerResource implements Resource {
    private static final Logger LOG = LoggerFactory.getLogger(DeployerResource.class);

    private final DeployerProxy deployer;
    private final SlaProxy sla;
    private final SeaCloudsApplicationDataStorage dataStore;

    public DeployerResource(DeployerProxy deployerProxy, SlaProxy slaProxy) {
        this.deployer = deployerProxy;
        this.sla = slaProxy;
        this.dataStore = SeaCloudsApplicationDataStorage.getInstance();
    }

    @POST
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications")
    @ApiOperation("Adds new application to SeaClouds Platform based on a SeaClouds-compliant TOSCA DAM specification")
    public Response addApplication(@ApiParam() String dam) {
        SeaCloudsApplicationData seaCloudsApplication = null;

        if (dam == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            try {
                LOG.debug("Deploy new application process started");
                seaCloudsApplication = new SeaCloudsApplicationData(dam);
                TaskSummary taskSummary = deployer.deployApplication(dam);
                seaCloudsApplication.setDeployerApplicationId(taskSummary);

                dataStore.addSeaCloudsApplicationData(seaCloudsApplication);
                return Response.ok(seaCloudsApplication).build();
            } catch (Exception e) {
                LOG.error(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications")
    @ApiOperation(value = "List all SeaClouds deployed Applications")
    public Response listApplications() throws IOException {
        List<SeaCloudsApplicationData> applications = dataStore.listSeaCloudsApplicationData();

        for (SeaCloudsApplicationData application : applications) {
            ApplicationSummary applicationSummary = deployer.getApplication(application.getDeployerApplicationId());
            application.setDeploymentStatus(applicationSummary.getStatus());

            if (application.getAgreementId() != null) {
                GuaranteeTermsStatus agreementStatus = sla.getAgreementStatus(application.getAgreementId());
                application.setAgreementStatus(IGuaranteeTerm.GuaranteeTermStatusEnum.valueOf(agreementStatus.getValue()));
            }

        }
        return Response.ok(applications).build();
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications/{seaCloudsId}")
    @ApiOperation(value = "Get SeaClouds Application details from the Deployer Component")
    public Response getApplication(@PathParam("seaCloudsId") String seaCloudsId) throws IOException {
        if (seaCloudsId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seaCloudsId);

            if (seaCloudsApplicationData == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            for (JsonNode application : deployer.getApplicationsTree()) {
                if(application.get("id").asText().equals(seaCloudsApplicationData.getDeployerApplicationId())){
                    return Response.ok(application).build();
                }
            }

            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("applications/{seaCloudsId}")
    @ApiOperation(value = "Remove SeaClouds Application from SeaClouds")
    public Response removeApplication(@PathParam("seaCloudsId") String seaCloudsId) {
        if (seaCloudsId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seaCloudsId);

        if (seaCloudsApplicationData == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        dataStore.removeSeaCloudsApplicationDataById(seaCloudsApplicationData.getSeaCloudsApplicationId());

        return Response.ok().build();
    }


}
