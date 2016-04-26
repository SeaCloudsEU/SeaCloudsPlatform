package eu.seaclouds.policy;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.policy.PolicySpec;
import org.apache.brooklyn.camp.brooklyn.BrooklynCampConstants;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.test.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class SeaCloudsMonitoringInitializationPolicyLiveTest
        extends AbstractSeaCloudsPoliciesLiveTests {

    private static final Logger log = LoggerFactory.getLogger(SeaCloudsMonitoringInitializationPolicyLiveTest.class);

    private static final String SERVICE_ID = "service";

    @Test(groups = {"Live"})
    public void testAttachPolicyToApplication() {
        app.createAndManageChild(EntitySpec.create(SeaCloudsMonitoringInitializationPolicyTest.TestSoftwareWithSensors.class)
                .configure(BrooklynCampConstants.PLAN_ID, SERVICE_ID));
        app.policies().add(getMonitoringPolicy(ImmutableList.of(SERVICE_ID)));

        app.start(ImmutableList.of(loc));

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertTrue(app.getAttribute(Startable.SERVICE_UP));
                assertTrue(app.getAttribute(Startable.SERVICE_UP));
                assertTrue(app.getAttribute(SeaCloudsMonitoringInitializationPolicies.MONITORING_CONFIGURED));
            }
        });
    }

    private PolicySpec<SeaCloudsMonitoringInitializationPolicies> getMonitoringPolicy(List<String> targetEntities) {
        return PolicySpec.create(SeaCloudsMonitoringInitializationPolicies.class)
                .configure(SeaCloudsMonitoringInitializationPolicies.TARGET_ENTITIES, targetEntities)
                .configure(SeaCloudsMonitoringInitializationPolicies.SEACLOUDS_DC_ENDPOINT, SEACLOUDS_DC_ENDPOINT);
    }


}
