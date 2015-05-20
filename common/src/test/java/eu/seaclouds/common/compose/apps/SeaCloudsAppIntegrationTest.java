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

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import brooklyn.entity.database.mysql.MySqlNode;
import brooklyn.entity.webapp.jboss.JBoss6Server;
import brooklyn.entity.webapp.jboss.JBoss7Server;
import brooklyn.entity.webapp.tomcat.TomcatServer;

@Test(groups = { "Integration" })
public class SeaCloudsAppIntegrationTest extends AbstractSeaCloudsAppTest {

   public static final String LOCATION_SPEC = "localhost";

   private Map<String, String> customBlueprintConfig;
   private Map<String, String> customLocationConfig;

   @BeforeMethod(alwaysRun=true)
   @Override
   public void setUp() throws Exception {
      customBlueprintConfig = null;
      customLocationConfig = null;
      super.setUp();
   }

   @Override
   protected String getLocationSpec() {
      return LOCATION_SPEC;
   }

   @Override
   protected Map<String, String> getCustomBlueprintConfig() {
      return (customBlueprintConfig == null) ? ImmutableMap.<String, String>of() : customBlueprintConfig;
   }

   @Override
   protected Map<String, String> getCustomLocationConfig() {
      return (customLocationConfig == null) ? ImmutableMap.<String, String>of() : customLocationConfig;
   }

   @Test(groups="Integration")
   public void testTomcat() throws Exception {
      runTest("tomcat-catalog.yaml", TomcatServer.class, TomcatServer.ROOT_URL, false);
   }

   @Test(groups="Integration")
   public void testJboss7() throws Exception {
      runTest("jboss7-catalog.yaml", JBoss7Server.class, JBoss7Server.ROOT_URL, false);
   }

   @Test(groups="Integration")
   public void testJboss6() throws Exception {
      runTest("jboss6-catalog.yaml", JBoss6Server.class, JBoss6Server.ROOT_URL, false);
   }

   @Test(groups="Integration")
   public void testMySql() throws Exception {
      runTest("mysql-catalog.yaml", MySqlNode.class, MySqlNode.DATASTORE_URL, false);
   }
}