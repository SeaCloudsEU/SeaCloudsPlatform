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
import com.google.gson.Gson;
import eu.seaclouds.platform.dashboard.connectors.DeployerConnector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Adrian Nieto
 */
public class AddApplicationsServlet extends HttpServlet {
    static BrooklynApi BROOKLYN_API = DeployerConnector.getConnection();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String yaml = request.getParameter("yaml");
        if (yaml != null) {
            Response res = BROOKLYN_API.getApplicationApi().createFromYaml(yaml);

            if (res.getStatus() >=  400) {
                response.sendError(500, "Connection error: couldn't reach SeaClouds endpoint");
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(res.getStatus()));
            }
        }else{
            response.sendError(400, "Missing yaml file");
        }
    }

}
