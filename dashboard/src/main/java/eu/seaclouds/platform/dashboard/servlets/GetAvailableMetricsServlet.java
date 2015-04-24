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
package eu.seaclouds.platform.dashboard.servlets;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.EntitySummary;
import brooklyn.rest.domain.SensorSummary;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.seaclouds.platform.dashboard.connectors.DeployerConnector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Adrian Nieto
 */
public class GetAvailableMetricsServlet extends HttpServlet {
    static BrooklynApi BROOKLYN_API = DeployerConnector.getConnection();

    private boolean isNumberType(SensorSummary sensor){
        return sensor.getType().equals("java.lang.Integer")
                || sensor.getType().equals("java.lang.Double")
                || sensor.getType().equals("java.lang.Float")
                || sensor.getType().equals("java.lang.Long")
                || sensor.getType().equals("java.lang.Short")
                || sensor.getType().equals("java.lang.BigDecimal")
                || sensor.getType().equals("java.lang.BigInteger")
                || sensor.getType().equals("java.lang.Byte");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String application =  request.getParameter("application");
        if(application != null){

            JsonArray parentJson = new JsonArray();

            for(EntitySummary entitySummary : BROOKLYN_API.getEntityApi().list(application)) {
                JsonObject entitySumaryJson = new JsonObject();
                entitySumaryJson.addProperty("id", entitySummary.getId());
                entitySumaryJson.addProperty("name", entitySummary.getName());
                entitySumaryJson.addProperty("type", entitySummary.getType());

                JsonArray entityMetricsJsonArray = new JsonArray();
                entitySumaryJson.add("metrics", entityMetricsJsonArray);

                List<SensorSummary> sensorSummaryList = BROOKLYN_API.getSensorApi().list(application, entitySummary.getId());
                Collections.sort(sensorSummaryList, new Comparator<SensorSummary>() {
                    @Override
                    public int compare(SensorSummary s1, SensorSummary s2) {
                        return s1.getName().compareTo(s2.getName());
                    }
                });

                for (SensorSummary sensorSummary : sensorSummaryList) {
                    if(isNumberType(sensorSummary)) {
                        entityMetricsJsonArray.add(new Gson().toJsonTree(sensorSummary));
                    }

                }

                parentJson.add(entitySumaryJson);
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(parentJson.toString());

        }else{
              response.sendError(404, "Connection error resource not found");

         }

    }
}
