package eu.seaclouds.platform.dashboard.servlets;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.SensorSummary;
import com.google.gson.Gson;
import eu.seaclouds.platform.dashboard.ConfigParameters;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Adrian Nieto
 */
public class ListApplicationsServlet extends HttpServlet {
    final static BrooklynApi BROOKLKYN_API = new BrooklynApi(ConfigParameters.DEPLOYER_ENDPOINT);


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ApplicationSummary> applicationSummaries = BROOKLKYN_API.getApplicationApi().list();

        Collections.sort(applicationSummaries, new Comparator<ApplicationSummary>() {
            @Override
            public int compare(ApplicationSummary s1, ApplicationSummary s2) {
                return s1.getId().compareTo(s2.getId());
            }
        });


        if (applicationSummaries == null){
            response.sendError(500, "Connection error: couldn't reach SeaClouds endpoint");
        }else{
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(applicationSummaries));
        }
    }
}
