package eu.seaclouds.monitor.nuroDc.metrics;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import it.polimi.tower4clouds.model.ontology.Resource;
import eu.seaclouds.monitor.nuroDc.Metric;

public class NUROServerLastTenSecondsPlayerCount extends Metric {

    @Override
    public Number getSample(Resource resource) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(this.getResponse());
        String value = actualObj.get("request_analytics").get("10seconds")
                .get("player_count").toString();
        Number toReturn = Integer.parseInt(value.substring(1,
                value.length() - 1));

        return toReturn;
    }

}
