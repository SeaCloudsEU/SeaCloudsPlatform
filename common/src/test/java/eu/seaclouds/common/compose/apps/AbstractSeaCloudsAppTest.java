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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.brooklyn.api.entity.Application;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.entity.EntityLocal;
import org.apache.brooklyn.api.location.Location;
import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.api.mgmt.Task;
import org.apache.brooklyn.api.sensor.AttributeSensor;
import org.apache.brooklyn.camp.brooklyn.BrooklynCampPlatformLauncherAbstract;
import org.apache.brooklyn.core.catalog.CatalogLoadMode;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.lifecycle.Lifecycle;
import org.apache.brooklyn.core.entity.trait.Startable;
import org.apache.brooklyn.core.internal.BrooklynProperties;
import org.apache.brooklyn.core.mgmt.internal.LocalManagementContext;
import org.apache.brooklyn.core.mgmt.persist.FileBasedObjectStore;
import org.apache.brooklyn.core.mgmt.rebind.RebindOptions;
import org.apache.brooklyn.core.mgmt.rebind.RebindTestUtils;
import org.apache.brooklyn.core.server.BrooklynServerConfig;
import org.apache.brooklyn.entity.software.base.SoftwareProcess;
import org.apache.brooklyn.entity.software.base.SoftwareProcess.RestartSoftwareParameters.RestartMachineMode;
import org.apache.brooklyn.entity.software.base.SoftwareProcess.StopSoftwareParameters.StopMode;
import org.apache.brooklyn.launcher.BrooklynLauncher;
import org.apache.brooklyn.launcher.SimpleYamlLauncherForTests;
import org.apache.brooklyn.launcher.camp.BrooklynCampPlatformLauncher;
import org.apache.brooklyn.test.Asserts;
import org.apache.brooklyn.test.EntityTestUtils;
import org.apache.brooklyn.util.core.ResourceUtils;
import org.apache.brooklyn.util.core.http.HttpTool;
import org.apache.brooklyn.util.core.http.HttpToolResponse;
import org.apache.brooklyn.util.exceptions.Exceptions;
import org.apache.brooklyn.util.net.Urls;
import org.apache.brooklyn.util.os.Os;
import org.apache.brooklyn.util.yaml.Yamls;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public abstract class AbstractSeaCloudsAppTest {

   private static final Logger LOG = LoggerFactory.getLogger(AbstractSeaCloudsAppTest.class);

   private File mementoDir;
   private ClassLoader classLoader = AbstractSeaCloudsAppTest.class.getClassLoader();

   private ManagementContext mgmt;
   private SimpleYamlLauncherForTests launcher;
   private BrooklynLauncher viewer;

   private ExecutorService executor;

   @BeforeMethod(alwaysRun = true)
   public void setUp() throws Exception {
      mementoDir = Os.newTempDir(getClass());
      mgmt = createOrigManagementContext();
      LOG.info("Test " + getClass() + " persisting to " + mementoDir);

      launcher = new SimpleYamlLauncherForTests() {
         @Override
         protected BrooklynCampPlatformLauncherAbstract newPlatformLauncher() {
            return new BrooklynCampPlatformLauncher() {
               protected ManagementContext newManagementContext() {
                  return AbstractSeaCloudsAppTest.this.mgmt;
               }
            };
         }
      };
      viewer = BrooklynLauncher.newInstance()
              .managementContext(mgmt)
              .start();

      executor = Executors.newCachedThreadPool();
   }

   @AfterMethod(alwaysRun = true)
   public void tearDown() throws Exception {
      try {
         if (mgmt != null) {
            for (Application app : mgmt.getApplications()) {
               LOG.debug("destroying app " + app + " (managed? " + Entities.isManaged(app) + "; mgmt is " + mgmt + ")");
               try {
                  Entities.destroy(app);
                  LOG.debug("destroyed app " + app + "; mgmt now " + mgmt);
               } catch (Exception e) {
                  LOG.error("problems destroying app " + app, e);
               }
            }
         }
         if (launcher != null) launcher.destroyAll();
         if (viewer != null) viewer.terminate();
         if (mgmt != null) Entities.destroyAll(mgmt);
         if (mementoDir != null) FileBasedObjectStore.deleteCompletely(mementoDir);
      } catch (Throwable t) {
         LOG.error("Caught exception in tearDown method", t);
      } finally {
         if (executor != null) executor.shutdownNow();
         executor = null;
         mgmt = null;
         launcher = null;
      }
   }

   protected abstract String getLocationSpec();

   protected Map<String, ?> getBrooklynProperties() {
      return ImmutableMap.of();
   }

   protected Map<String, String> getCustomBlueprintConfig() {
      return ImmutableMap.of();
   }

   protected Map<String, String> getCustomLocationConfig() {
      return ImmutableMap.of();
   }

   protected void runTest(String url, Class<? extends SoftwareProcess> expectedType, AttributeSensor<String> endpoint, boolean expectsSubnetTier) throws Exception {
      runTest(url, Predicates.instanceOf(expectedType), endpoint, expectsSubnetTier);
   }

   protected void runTest(String url, Predicate<? super Entity> typePredicate, AttributeSensor<String> endpoint, boolean expectsSubnetTier) throws Exception {
      String catalogId = addToCatalog(url);

      // Generate the yaml blueprint
      Map<String, String> blueprintConfig = getCustomBlueprintConfig();
      Map<String, String> locationConfig = getCustomLocationConfig();
      StringBuilder yaml = new StringBuilder();

      if (locationConfig.size() > 0) {
         yaml.append("location: \n" +
                 "  " + getLocationSpec() + ":\n");
         for (Map.Entry<String, String> entry : locationConfig.entrySet()) {
            yaml.append("    " + entry.getKey() + ": " + entry.getValue() + "\n");
         }
      } else {
         yaml.append("location: " + getLocationSpec() + "\n");
      }

      yaml.append(
              "services: \n" +
                      "- type: " + catalogId + "\n");
      if (blueprintConfig.size() > 0) {
         yaml.append("  brooklyn.config:" + "\n");
         for (Map.Entry<String, String> entry : blueprintConfig.entrySet()) {
            yaml.append("    " + entry.getKey() + ": " + entry.getValue() + "\n");
         }
      }

      // Deploy the blueprint
      LOG.info("Deploying YAML:\n" + yaml);
      Application app = launcher.launchAppYaml(new StringReader(yaml.toString()));

      //  Confirm has expected entity/structure
      Entity entity = Iterables.getOnlyElement(app.getChildren());
      assertTrue(typePredicate.apply(entity), "entity=" + entity);

      // Confirm healthy, and operations work
      assertNoFires(app);
      for (Entity softwareProcess : Entities.descendants(entity, Predicates.instanceOf(SoftwareProcess.class), true)) {
         assertCanRestartProcess((SoftwareProcess) softwareProcess);
      }

      // Rebind, and find new entity instances
      Application newApp = rebind();
      Entity newEntity = Iterables.getOnlyElement(newApp.getChildren());

      // Confirm still healthy, and operations still work
      assertNoFires(newApp);
      for (Entity softwareProcess : Entities.descendants(newEntity, Predicates.instanceOf(SoftwareProcess.class), true)) {
         assertCanRestartProcess((SoftwareProcess) softwareProcess);
      }
   }

   protected void runTestConcurrentDeploys(Map<String, Integer> urls) throws Exception {
      runTestConcurrentDeploys(1, urls);
   }

   protected void runTestConcurrentDeploys(int numCycles, Map<String, Integer> urls) throws Exception {
      Map<String, String> urlToCatalogId = Maps.newLinkedHashMap();
      for (String url : urls.keySet()) {
         String catalogId = addToCatalog(url);
         urlToCatalogId.put(url, catalogId);
      }

      for (int cycle = 0; cycle < numCycles; cycle++) {
         List<Future<Application>> futures = Lists.newArrayList();

         for (Map.Entry<String, Integer> entry : urls.entrySet()) {
            String catalogId = urlToCatalogId.get(entry.getKey());
            int num = entry.getValue();

            for (int i = 0; i < num; i++) {
               // Generate the yaml blueprint
               final StringBuilder yaml = new StringBuilder();
               Map<String, String> blueprintConfig = getCustomBlueprintConfig();
               Map<String, String> locationConfig = getCustomLocationConfig();

               if (locationConfig.size() > 0) {
                  yaml.append("location: \n" +
                          "  " + getLocationSpec() + ":\n");
                  for (Map.Entry<String, String> entry2 : locationConfig.entrySet()) {
                     yaml.append("    " + entry2.getKey() + ": " + entry2.getValue() + "\n");
                  }
               } else {
                  yaml.append("location: " + getLocationSpec() + "\n");
               }

               yaml.append(
                       "services: \n" +
                               "- type: " + catalogId + "\n");
               if (blueprintConfig.size() > 0) {
                  yaml.append("  brooklyn.config:" + "\n");
                  for (Map.Entry<String, String> entry2 : blueprintConfig.entrySet()) {
                     yaml.append("    " + entry2.getKey() + ": " + entry2.getValue() + "\n");
                  }
               }

               // Deploy the blueprint
               Future<Application> future = executor.submit(new Callable<Application>() {
                  public Application call() {
                     LOG.info("Deploying YAML:\n" + yaml);
                     return launcher.launchAppYaml(new StringReader(yaml.toString()));
                  }
               });
               futures.add(future);
            }
         }

         //  Confirm all started without error
         for (Future<Application> future : futures) {
            Application app = future.get();
            assertNoFires(app);
         }
      }

      // Confirm can rebind, and that nothing goes/stays on fire after rebind
      Application newApp = rebind();
      Collection<Application> newApps = newApp.getManagementContext().getApplications();

      // Check apps all healthy;
      // TODO simplify code again (i.e. remove try-catch) when confident not having transient errors after rebind
      try {
         for (Application app : newApps) {
            assertNoFires(app);
         }
      } catch (Throwable t) {
         // Try again, in case it's a transient error
         LOG.error("App(s) on fire after rebind; waiting 60 seconds and checking again", t);
         Thread.sleep(60 * 1000);

         try {
            for (Application app : newApps) {
               assertNoFires(app);
            }
            LOG.error("App(s) on fire after rebind; waiting 60 seconds and checking again", t);
            throw new RuntimeException("App(s) were temporarily on fire, but recovered after 60 seconds", t);
         } catch (Throwable t2) {
            LOG.error("App(s) still on fire 60 seconds after rebind; throwing original exception", t2);
            throw Exceptions.propagate(t);
         }
      }

      // Confirm can stop all the apps
      for (Application app : newApps) {
         ((Startable) app).stop();
      }
   }

   protected String addToCatalog(String yamlUri) {
      String baseUri = viewer.getServerDetails().getWebServer().getRootUrl();
      URI uri = URI.create(Urls.mergePaths(baseUri, "/v1/catalog/"));

      String yaml = ResourceUtils.create(this).getResourceAsString(yamlUri);

      // TODO Should really get symbolicName + version from the returned HTTP response
      Iterable<Object> parsedYaml = Yamls.parseAll(yaml);
      Object catalogSection = ((Map<?, ?>) Iterables.get(parsedYaml, 0)).get("brooklyn.catalog");
      String catalogId = (String) ((Map<?, ?>) catalogSection).get("id");
      String iconUrl = (String) ((Map<?, ?>) catalogSection).get("iconUrl");
      Double version = (Double) ((Map<?, ?>) catalogSection).get("version");
      String catalogRef = catalogId + ":" + Double.toString(version);

      assertNotNull(catalogId);
      assertTrue(ResourceUtils.create(this).doesUrlExist(iconUrl), "url=" + iconUrl);

      HttpClient httpClient = HttpTool.httpClientBuilder()
              .credentials(new UsernamePasswordCredentials("admin", "password"))
              .uri(uri)
              .build();

      HttpToolResponse resp = HttpTool.httpPost(httpClient, uri, ImmutableMap.<String, String>of(), yaml.getBytes());
      assertEquals(resp.getResponseCode(), 201);
      LOG.info("Added to catalog: " + resp.getContentAsString());

      // TODO Could/should return id:version, but that is currently breaking rebind.
      // Need to investigate further.
      return catalogId;
   }

   protected void assertNoFires(final Entity app) {
      EntityTestUtils.assertAttributeEqualsEventually(app, Attributes.SERVICE_UP, true);
      EntityTestUtils.assertAttributeEqualsEventually(app, Attributes.SERVICE_STATE_ACTUAL, Lifecycle.RUNNING);

      Asserts.succeedsEventually(new Runnable() {
         public void run() {
            for (Entity entity : Entities.descendants(app)) {
               assertNotEquals(entity.getAttribute(Attributes.SERVICE_STATE_ACTUAL), Lifecycle.ON_FIRE);
               assertNotEquals(entity.getAttribute(Attributes.SERVICE_UP), false);

               if (entity instanceof SoftwareProcess) {
                  EntityTestUtils.assertAttributeEquals(entity, Attributes.SERVICE_STATE_ACTUAL, Lifecycle.RUNNING);
                  EntityTestUtils.assertAttributeEquals(entity, Attributes.SERVICE_UP, Boolean.TRUE);
               }
            }
         }
      });
   }

   protected void assertCanRestartProcess(final SoftwareProcess entity) throws Exception {
      Collection<Location> origLocs = entity.getLocations();
      String origHostname = entity.getAttribute(SoftwareProcess.HOSTNAME);

      // Stop the process
      Task<?> stopTask = Entities.invokeEffector(
              (EntityLocal) entity,
              entity,
              SoftwareProcess.STOP,
              ImmutableMap.of(
                      SoftwareProcess.StopSoftwareParameters.STOP_MACHINE_MODE.getName(), StopMode.NEVER,
                      SoftwareProcess.StopSoftwareParameters.STOP_PROCESS_MODE.getName(), StopMode.ALWAYS));
      stopTask.get();

      EntityTestUtils.assertAttributeEqualsEventually(entity, Attributes.SERVICE_UP, false);
      EntityTestUtils.assertAttributeEqualsEventually(entity, Attributes.SERVICE_STATE_ACTUAL, Lifecycle.STOPPED);

      // Re-start the process - expect the same set of locations and IPs
      Task<?> restartTask = Entities.invokeEffector(
              (EntityLocal) entity,
              entity,
              SoftwareProcess.RESTART,
              ImmutableMap.of(SoftwareProcess.RestartSoftwareParameters.RESTART_MACHINE.getName(), RestartMachineMode.FALSE));
      restartTask.get();

      assertNoFires(entity);
      assertEquals(entity.getLocations(), origLocs);
      assertEquals(entity.getAttribute(SoftwareProcess.HOSTNAME), origHostname);
   }

   protected Reader loadYaml(String url, String location) {
      String yaml =
              "location: " + location + "\n" +
                      new ResourceUtils(this).getResourceAsString(url);
      return new StringReader(yaml);
   }


   //////////////////////////////////////////////////////////////////
   // FOR REBIND                                                   //
   // See brooklyn.entity.rebind.RebindTestFixture in core's tests //
   //////////////////////////////////////////////////////////////////

   /**
    * rebinds, and sets newApp
    */
   protected Application rebind() throws Exception {
      return rebind(RebindOptions.create());
   }

   protected Application rebind(RebindOptions options) throws Exception {
      ManagementContext origMgmt = mgmt;
      ManagementContext newMgmt = createNewManagementContext();
      Collection<Application> origApps = origMgmt.getApplications();

      options = RebindOptions.create(options);
      if (options.classLoader == null) options.classLoader(classLoader);
      if (options.mementoDir == null) options.mementoDir(mementoDir);
      if (options.origManagementContext == null) options.origManagementContext(origMgmt);
      if (options.newManagementContext == null) options.newManagementContext(newMgmt);

      for (Application origApp : origApps) {
         RebindTestUtils.waitForPersisted(origApp);
      }

      mgmt = options.newManagementContext;
      Application newApp = RebindTestUtils.rebind(options);

      if (launcher != null) {
         launcher = new SimpleYamlLauncherForTests() {
            @Override
            protected BrooklynCampPlatformLauncherAbstract newPlatformLauncher() {
               return new BrooklynCampPlatformLauncher() {
                  protected ManagementContext newManagementContext() {
                     return AbstractSeaCloudsAppTest.this.mgmt;
                  }
               };
            }
         };
      }
      if (viewer != null) {
         viewer.terminate();
         viewer = BrooklynLauncher.newInstance()
                 .managementContext(mgmt)
                 .start();
      }

      return newApp;
   }

   /**
    * @return A started management context
    */
   protected LocalManagementContext createOrigManagementContext() {
      BrooklynProperties properties = BrooklynProperties.Factory.newDefault();
      properties.put(BrooklynServerConfig.CATALOG_LOAD_MODE, CatalogLoadMode.LOAD_BROOKLYN_CATALOG_URL);
      properties.putAll(getBrooklynProperties());
      return RebindTestUtils.managementContextBuilder(mementoDir, classLoader)
              .properties(properties)
              .persistPeriodMillis(1)
              .forLive(true)
              .emptyCatalog(true)
              .buildStarted();
   }

   /**
    * @return An unstarted management context
    */
   protected LocalManagementContext createNewManagementContext() {
      BrooklynProperties properties = BrooklynProperties.Factory.newDefault();
      properties.put(BrooklynServerConfig.CATALOG_LOAD_MODE, CatalogLoadMode.LOAD_BROOKLYN_CATALOG_URL_IF_NO_PERSISTED_STATE);
      return RebindTestUtils.managementContextBuilder(mementoDir, classLoader)
              .properties(properties)
              .persistPeriodMillis(1)
              .forLive(true)
              .emptyCatalog(true)
              .buildUnstarted();
   }
}
