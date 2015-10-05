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
package eu.seaclouds.common.compose.apps;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import org.apache.brooklyn.entity.database.mysql.MySqlNode;
import org.apache.brooklyn.entity.webapp.jboss.JBoss6Server;
import org.apache.brooklyn.entity.webapp.jboss.JBoss7Server;
import org.apache.brooklyn.entity.webapp.tomcat.TomcatServer;

@Test(groups = { "Live" })
public class SeaCloudsAppEc2LiveTest extends AbstractSeaCloudsAppTest {

   public static final String LOCATION_SPEC = "aws-ec2-us-west";

   @Override
   protected String getLocationSpec() {
      return LOCATION_SPEC;
   }

   @Test(groups = "Live")
   public void testTomcat() throws Exception {
      runTest("tomcat-catalog.yaml", TomcatServer.class, TomcatServer.ROOT_URL, false);
   }

   @Test(groups = "Live")
   public void testJboss7() throws Exception {
      runTest("jboss7-catalog.yaml", JBoss7Server.class, JBoss7Server.ROOT_URL, false);
   }

   @Test(groups = "Live")
   public void testJboss6() throws Exception {
      runTest("jboss6-catalog.yaml", JBoss6Server.class, JBoss6Server.ROOT_URL, false);
   }

   @Test(groups = "Live")
   public void testMySql() throws Exception {
      runTest("mysql-catalog.yaml", MySqlNode.class, MySqlNode.DATASTORE_URL, false);
   }

   @Test(groups = "Live")
   public void testConcurrentDeploys() throws Exception {
      final int NUM_CYCLES = 2;
      final int NUM_PER_TYPE = 1;
      runTestConcurrentDeploys(NUM_CYCLES, ImmutableMap.<String, Integer>builder()
              .put("tomcat-catalog.yaml", NUM_PER_TYPE)
              .put("jboss7-catalog.yaml", NUM_PER_TYPE)
              .put("jboss6-catalog.yaml", NUM_PER_TYPE)
              .put("mysql-catalog.yaml", NUM_PER_TYPE)
              .build());
   }
}
