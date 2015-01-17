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
import eu.seaclouds.platform.dashboard.ConfigParameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Adrian Nieto
 */
public class GetMetricValueServlet extends HttpServlet {
    final static BrooklynApi BROOKLKYN_API = new BrooklynApi(ConfigParameters.DEPLOYER_ENDPOINT);


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String application = request.getParameter("application");
        String entity = request.getParameter("entity");
        String sensor = request.getParameter("sensor");

        Object sensorValue = BROOKLKYN_API.getSensorApi().get(application, entity, sensor, true);

        if (sensorValue == null){
            response.sendError(500, "Connection error: couldn't reach SeaClouds endpoint");
        }else{
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(sensorValue));

        }

    }
}
