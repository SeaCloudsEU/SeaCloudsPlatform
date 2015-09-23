/*
 * Copyright 2015 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.apache;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.brooklyn.api.entity.EntitySpec;
import org.apache.brooklyn.api.location.LocationSpec;
import org.apache.brooklyn.api.mgmt.LocationManager;
import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.core.entity.Entities;
import org.apache.brooklyn.core.entity.factory.ApplicationBuilder;
import org.apache.brooklyn.core.test.entity.TestApplication;
import org.apache.brooklyn.entity.php.PhpWebAppSoftwareProcess;
import org.apache.brooklyn.entity.php.httpd.PhpHttpdServer;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.test.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PhpHttpdServerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PhpHttpdServerIntegrationTest.class);


    private SshMachineLocation loc;
    private ManagementContext managementContext;
    private LocationManager locationManager;

    private TestApplication app;
    private String gitRepoURLApp="https://github.com/kiuby88/phpHelloWorld.git";
    private String tarballResourceUrl="https://github.com/kiuby88/phpHelloWorld/archive/1.tar.gz";

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
        managementContext = app.getManagementContext();
        locationManager = managementContext.getLocationManager();
        loc = locationManager.createLocation(LocationSpec.create(SshMachineLocation.class)
                .configure("address", "localhost"));
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        log.info("Destroy all {}", new Object[]{this});
        if (app != null)
           Entities.destroyAll(app.getManagementContext());
    }

    @Test(groups = {"Integration"})
    public void testHttpTarballResource() throws Exception {
        try {
            integrationTestHttTarballResource();
        }
        catch (Exception e){
            log.error("Exception caught in testHttpTarballResource {} ", new Object[]{e.fillInStackTrace()});
            tearDown();
            throw e;
        }
    }

    @Test(groups = {"Integration"})
    public void testHttpGitResource() throws Exception {
        try {
            integrationTestHttGitResource();
        }
        catch (Exception e){
            log.error("Exception caught in testHttpGitResource{} ", new Object[]{e.fillInStackTrace()});
            tearDown();
            throw e;
        }
    }

    private void integrationTestHttTarballResource() throws Exception{
        final PhpHttpdServer server = app.createAndManageChild(EntitySpec.create(PhpHttpdServer.class)
                .configure("tarball.url", tarballResourceUrl)
                .configure("http.port", "8080")
                .configure(PhpHttpdServer.ENABLED_PROTOCOLS, ImmutableSet.of("http")));

        app.start(ImmutableList.of(loc));

        String httpUrl = "http://"+server.getAttribute(PhpHttpdServer.HOSTNAME)+":"+server.getAttribute(PhpHttpdServer.HTTP_PORT)+"/";
        assertEquals(server.getAttribute(PhpHttpdServer.ROOT_URL).toLowerCase(), httpUrl.toLowerCase());
        assertEquals(server.getAttribute(PhpWebAppSoftwareProcess.DEPLOYED_PHP_APPS).size(), 1);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertNotNull(server.getAttribute(PhpHttpdServer.TOTAL_KBYTE));
                assertNotNull(server.getAttribute(PhpHttpdServer.CPU_LOAD));
                assertNotNull(server.getAttribute(PhpHttpdServer.REQUEST_PER_SEC));
                assertNotNull(server.getAttribute(PhpHttpdServer.BYTES_PER_SEC));
                assertNotNull(server.getAttribute(PhpHttpdServer.BYTES_PER_REQ));
                assertNotNull(server.getAttribute(PhpHttpdServer.BUSY_WORKERS));
            }
        });
    }


    private void integrationTestHttGitResource() throws Exception{
        final PhpHttpdServer server = app.createAndManageChild(EntitySpec.create(PhpHttpdServer.class)
                .configure("git.url", gitRepoURLApp)
                .configure("http.port", "8080")
                .configure(PhpHttpdServer.ENABLED_PROTOCOLS, ImmutableSet.of("http")));

        app.start(ImmutableList.of(loc));

        String httpUrl = "http://"+server.getAttribute(PhpHttpdServer.HOSTNAME)+":"+server.getAttribute(PhpHttpdServer.HTTP_PORT)+"/";
        assertEquals(server.getAttribute(PhpHttpdServer.ROOT_URL).toLowerCase(), httpUrl.toLowerCase());
        assertEquals(server.getAttribute(PhpWebAppSoftwareProcess.DEPLOYED_PHP_APPS).size(), 1);

        Asserts.succeedsEventually(new Runnable() {
            public void run() {
                assertNotNull(server.getAttribute(PhpHttpdServer.TOTAL_KBYTE));
                assertNotNull(server.getAttribute(PhpHttpdServer.CPU_LOAD));
                assertNotNull(server.getAttribute(PhpHttpdServer.REQUEST_PER_SEC));
                assertNotNull(server.getAttribute(PhpHttpdServer.BYTES_PER_SEC));
                assertNotNull(server.getAttribute(PhpHttpdServer.BYTES_PER_REQ));
                assertNotNull(server.getAttribute(PhpHttpdServer.BUSY_WORKERS));
            }
        });
    }

}
