/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.seaclouds.platform.planner.optimizer.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Optimizer;
import eu.seaclouds.platform.planner.optimizer.heuristics.SearchMethodName;

@Path("/optimizer")
public class OptimizerService {

   private static final Logger log = LoggerFactory.getLogger(OptimizerService.class.getName());
   private String NL = System.getProperty("line.separator");
   
   @POST
   @Path("/optimize")
   @Produces("application/x-yaml")
   public Response optimize(@FormParam(value="aam") String appModel, @FormParam(value="offers") String suitableCloudOffer, @FormParam(value="optmethod") String optimizationMethod) {
       Optimizer optimizer;
       if(optimizationMethod!=null){
          optimizer=  new Optimizer(SearchMethodName.valueOf((String)optimizationMethod));
         }
         else{
            optimizer=  new Optimizer();  
         }
       String[] outputPlans = new String[]{"Plan generation was not possible"};
       try {
       
          outputPlans = optimizer.optimize(appModel, suitableCloudOffer);
       }
       catch (Error E) {
           log.error("Error optimizing");
       }
       String out = "";
       for (String s : outputPlans) {
           out = String.valueOf(out) + s + this.NL + this.NL + "---" + this.NL + this.NL;
       }
     
       return Response.status((int)200).entity((Object)out).build();
   }

   @GET
   @Path("/heartbeat")
   @Produces(MediaType.TEXT_PLAIN)
   public String test() {
 
      return "Optimizer alive";
 
     
 
   }
   
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String check() {
 
      return test();
 
     
 
   }
   
}
