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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Adrian Nieto
 */
public class GetAvailableMetricsServlet extends HttpServlet {
    final static BrooklynApi BROOKLKYN_API = new BrooklynApi(ConfigParameters.MONITOR_ENDPOINT);


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




            for(EntitySummary entitySummary : BROOKLKYN_API.getEntityApi().list(application)) {
                JsonObject entitySumaryJson = new JsonObject();
                entitySumaryJson.addProperty("id", entitySummary.getId());
                entitySumaryJson.addProperty("name", entitySummary.getName());
                entitySumaryJson.addProperty("type", entitySummary.getType());

                JsonArray entityMetricsJsonArray = new JsonArray();
                entitySumaryJson.add("metrics", entityMetricsJsonArray);

                List<SensorSummary> sensorSummaryList = BROOKLKYN_API.getSensorApi().list(application, entitySummary.getId());
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
