package eu.seaclouds.platform.dashboard.servlets;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.ApplicationSummary;
import brooklyn.rest.domain.EntitySummary;
import brooklyn.rest.domain.LocationSummary;
import brooklyn.rest.domain.SensorSummary;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

            JsonArray jsonResult = new JsonArray();

            for (ApplicationSummary application : applicationSummaries) {

                JsonObject jsonApplication = new JsonObject();
                jsonResult.add(jsonApplication);

                jsonApplication.addProperty("id", application.getId());
                jsonApplication.addProperty("status", application.getStatus().toString());

                JsonObject jsonSpec = new JsonObject();
                jsonApplication.add("spec", jsonSpec);

                jsonSpec.addProperty("name", application.getSpec().getName());
                jsonSpec.addProperty("type", application.getSpec().getName());

                JsonArray jsonDescendantsEntities = new JsonArray();
                jsonApplication.add("descendants", jsonDescendantsEntities);

                List<EntitySummary> descendants = BROOKLKYN_API.getEntityApi().list(application.getId());

                if(descendants != null){
                    for (EntitySummary childEntity : descendants) {
                        JsonObject jsonDescendantEntity = new JsonObject();
                        jsonDescendantsEntities.add(jsonDescendantEntity);


                        jsonDescendantEntity.addProperty("id",childEntity.getId());
                        jsonDescendantEntity.addProperty("name",childEntity.getName());
                        jsonDescendantEntity.addProperty("type",childEntity.getType());

                        JsonArray jsonArrayLocations = new JsonArray();
                        jsonDescendantEntity.add("locations", jsonArrayLocations);

                        List<LocationSummary> locations = BROOKLKYN_API.getEntityApi().getLocations(application.getId(), childEntity.getId());
                        if(locations != null){

                            for (LocationSummary locationSummary : locations) {
                                    LocationSummary locationDetails = BROOKLKYN_API.getLocationApi().get(locationSummary.getId(), null);

                                    if(locationDetails != null){
                                        JsonObject jsonLocation = new JsonObject();
                                        jsonArrayLocations.add(jsonLocation);
                                        jsonLocation.addProperty("id", locationDetails.getId());
                                        jsonLocation.addProperty("name", locationDetails.getName());
                                        jsonLocation.addProperty("type", locationDetails.getType());
                                        jsonLocation.addProperty("spec", locationDetails.getSpec());
                                    }

                            }


                        }else{
                                response.sendError(500, "Connection error: couldn't reach SeaClouds endpoint");

                        }

                    }

                }else{
                    response.sendError(500, "Connection error: couldn't reach SeaClouds endpoint");
                }
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(jsonResult));
        }
    }
}
