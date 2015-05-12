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
 * @author MBarrientos
 */
public class RemoveApplicationsServlet extends HttpServlet {


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BrooklynApi BROOKLYN_API = new DeployerConnector().getConnection();
        String application = request.getParameter("application");
        if (application != null & BROOKLYN_API != null) {

            Response res = BROOKLYN_API.getApplicationApi().delete(application);

            if (res.getStatus() >=  400) {
                response.sendError(500, "Connection error: couldn't reach Deployer endpoint");
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(res.getStatus()));
            }

        } else if (BROOKLYN_API == null) {
            response.sendError(500, "Connection error: couldn't reach Deployer endpoint");
        } else {
            response.sendError(404, "Resource not found");
        }
    }
}
