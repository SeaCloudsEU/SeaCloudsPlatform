package eu.seaclouds.platform.dashboard.servlets;

import brooklyn.rest.client.BrooklynApi;
import eu.seaclouds.platform.dashboard.ConfigParameters;

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
    final static BrooklynApi BROOKLKYN_API = new BrooklynApi(ConfigParameters.DEPLOYER_ENDPOINT);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String yaml = request.getParameter("yaml");
        if (yaml != null) {
            Response res = BROOKLKYN_API.getApplicationApi().createFromYaml(yaml);

            if (res.getStatus() >=  400) {
                response.sendError(500, "Connection error: couldn't reach SeaClouds endpoint");
            } else {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(res.toString());
            }
        }else{
            response.sendError(400, "Missing yaml file");
        }
    }

}
