package eu.seaclouds.monitor.nuroDc.metrics;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import it.polimi.tower4clouds.model.ontology.Resource;
import eu.seaclouds.monitor.nuroDc.Metric;

public class NuroMetric extends Metric {


    @Override
    public Number getSample(Resource resource) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(this.getResponse());
        String value;
        Number toReturn;
        
        switch (this.getMonitoredMetric()) {
        case "NUROServerLastMinuteAverageRunTime":
             value = actualObj.get("request_analytics").get("minute")
                    .get("avg_run_time").toString();
             toReturn = Double.parseDouble(value.substring(1,
                    value.length() - 1));
            break;
        case "NUROServerLastMinuteAverageThroughput":
             value = actualObj.get("request_analytics").get("minute")
                    .get("request_count").toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1)) / 60;

            break;
        case "NUROServerLastMinutePlayerCount":
             value = actualObj.get("request_analytics").get("minute")
                    .get("player_count").toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastMinuteRequestCount":
             value = actualObj.get("request_analytics").get("minute")
                    .get("request_count").toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastTenSecondsAverageRunTime":
             value = actualObj.get("request_analytics").get("10seconds")
                    .get("avg_run_time").toString();
             toReturn = Double.parseDouble(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastTenSecondsAverageThroughput":
             value = actualObj.get("request_analytics").get("10seconds")
                    .get("request_count").toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1)) / 10;

            break;
        case "NUROServerLastTenSecondsPlayerCount":
             value = actualObj.get("request_analytics").get("10seconds")
                    .get("player_count").toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        case "NUROServerLastTenSecondsRequestCount":
             value = actualObj.get("request_analytics").get("10seconds")
                    .get("request_count").toString();
             toReturn = Integer.parseInt(value.substring(1,
                    value.length() - 1));

            break;
        default:
            toReturn=null;
            break;
        }
        
        return toReturn;
    }

}
