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
package eu.seaclouds.common.compose.policies;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.entity.webapp.tomcat.TomcatServer;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.test.Asserts;
import eu.seaclouds.common.compose.apps.AbstractSeaCloudsAppTest;

@Test(groups = { "Live" })
public class DataCollectorInstallationPolicyLiveTest extends AbstractSeaCloudsAppTest {

   public static final String LOCATION_SPEC = "softlayer-london-2";

   @Override
   protected String getLocationSpec() {
      return LOCATION_SPEC;
   }

   @Test(groups = "Live")
   public void testTomcat() throws Exception {
      Predicate<Entity> testPredicate = new Predicate<Entity>() {
         @Override
         public boolean apply(Entity entity) {
            Asserts.assertTrue(entity instanceof TomcatServer, "entity=" + entity);
            Location location = Iterables.getOnlyElement((entity).getLocations());
            Asserts.assertTrue(location instanceof SshMachineLocation, "location=" + location);
            SshMachineLocation sshMachineLocation = (SshMachineLocation) location;
            List<String> commands = ImmutableList.<String>builder()
                    // FIXME: Check if data collector is installed
                    .add("echo foo")
                    .build();
            int returnCode = sshMachineLocation.execScript("Checking for data collector", commands);
            Assert.assertEquals(returnCode, 0, "Expected script to return 0, found " + returnCode);
            return true;
         }
      };
      runTest("tomcat-catalog-with-datacollector.yaml", testPredicate, TomcatServer.ROOT_URL, false);
   }

}
