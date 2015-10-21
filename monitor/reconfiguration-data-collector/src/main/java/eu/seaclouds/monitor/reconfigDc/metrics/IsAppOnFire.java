package eu.seaclouds.monitor.reconfigDc.metrics;

import java.util.List;
import org.apache.brooklyn.rest.client.BrooklynApi;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import it.polimi.tower4clouds.model.ontology.Resource;
import eu.seaclouds.monitor.reconfigDc.Metric;

public class IsAppOnFire extends Metric {

    private BrooklynApi deployer;

    @Override
    public Number getSample(Resource resource) throws Exception {
        deployer = new BrooklynApi("http://" + getDeployerInstanceIp() + ":"
                + getDeployerInstancePort() + "/", getDeployerUser(),
                getDeployerPassword());

        Number toReturn = 0;

        List<ApplicationSummary> apps = deployer.getApplicationApi().list(null);
        for (ApplicationSummary app : apps) {

            if (app.getId().equals(resource.getType()))
                if (app.getStatus().toString().equals("ON_FIRE")
                        | app.getStatus().toString().equals("ERROR")) {
                    toReturn = 0;
                } else {
                    toReturn = 1;
                }

        }

        return toReturn;

    }

}
