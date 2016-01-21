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

package eu.seaclouds.platform.dashboard.rest;


import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import eu.atos.sla.parser.data.wsag.Agreement;
import eu.atos.sla.parser.data.wsag.GuaranteeTerm;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationData;
import eu.seaclouds.platform.dashboard.model.SeaCloudsApplicationDataStorage;
import eu.seaclouds.platform.dashboard.proxy.SlaProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sla")
@Api("/sla")
public class SlaResource implements Resource{
    private static final Logger LOG = LoggerFactory.getLogger(SlaResource.class);

    private final SlaProxy sla;
    private final SeaCloudsApplicationDataStorage dataStore;

    public SlaResource(SlaProxy slaProxy) {
        this.sla = slaProxy;
        this.dataStore = SeaCloudsApplicationDataStorage.getInstance();
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("agreements/{seaCloudsId}")
    @ApiOperation(value="Get SLA Agreement for a particular SeaClouds Application Id")
    public Response getAgreement(@PathParam("seaCloudsId") String seaCloudsId) {
        if (seaCloudsId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seaCloudsId);
            if (seaCloudsApplicationData == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if(seaCloudsApplicationData.getAgreementId() == null){
                LOG.error("Application " + seaCloudsId + " doesn't contain any agreement");
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            return Response.ok(sla.getAgreement(seaCloudsApplicationData.getAgreementId())).build();
        }
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("agreements/{seaCloudsId}/terms/{termName}/violations")
    @ApiOperation(value="Get SLA Agreement Term Violations for a particular SeaClouds Application Id and Term Name")
    public Response getViolations(@PathParam("seaCloudsId") String seaCloudsId, @PathParam("termName") String termName) {
        if (seaCloudsId == null || termName == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seaCloudsId);

            if (seaCloudsApplicationData == null) {
                LOG.error("Application " + seaCloudsId + " not found");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if(seaCloudsApplicationData.getAgreementId() == null){
                LOG.error("Application " + seaCloudsId + " doesn't contain any agreement");
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Agreement agreement = sla.getAgreement(seaCloudsApplicationData.getAgreementId());
            GuaranteeTerm term = null;
            for (GuaranteeTerm termItem : agreement.getTerms().getAllTerms().getGuaranteeTerms()) {
                if(termItem.getName().equalsIgnoreCase(termName)){
                    term = termItem;
                    break;
                }
            }

            return Response.ok(sla.getGuaranteeTermViolations(agreement, term)).build();
        }
    }


    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("agreements/{seaCloudsId}/status")
    @ApiOperation(value="Get SLA Agreement Status for a particular SeaClouds Application Id")
    public Response getAgreementStatus(@PathParam("seaCloudsId") String seaCloudsId) {
        if (seaCloudsId == null) {
            LOG.error("Missing input parameters");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        } else {
            SeaCloudsApplicationData seaCloudsApplicationData = dataStore.getSeaCloudsApplicationDataById(seaCloudsId);

            if (seaCloudsApplicationData == null) {
                LOG.error("Application " + seaCloudsId + " not found");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if(seaCloudsApplicationData.getAgreementId() == null){
                LOG.error("Application " + seaCloudsId + " doesn't contain any agreement");
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Agreement agreement = sla.getAgreement(seaCloudsApplicationData.getAgreementId());
            return Response.ok(sla.getAgreementStatus(agreement)).build();
        }
    }
}