/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.proxy;

import com.google.common.io.Resources;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import eu.seaclouds.platform.dashboard.DashboardTestApplication;
import eu.seaclouds.platform.dashboard.DashboardTestConfiguration;
import io.dropwizard.testing.DropwizardTestSupport;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

public abstract class AbstractProxyTest<T extends AbstractProxy> {
    private static final String TEST_CONFIG_YAML_PATH = "test-config.yml";
    private MockWebServer mockWebServer;

    // Create a new Dropwizard Test Application
    private final DropwizardTestSupport<DashboardTestConfiguration> SUPPORT =
            new DropwizardTestSupport<>(DashboardTestApplication.class, resourceFilePath(TEST_CONFIG_YAML_PATH));

    public final DropwizardTestSupport<DashboardTestConfiguration> getSupport() {
        return SUPPORT;
    }

    public final MockWebServer getMockWebServer() {
        return mockWebServer;
    }

    @BeforeClass
    public final void loadDropWizard() throws Exception {
        SUPPORT.before();
    }

    @AfterClass
    public final void unLoadDropWizard() throws Exception {
        SUPPORT.after();
    }

    @BeforeMethod
    public final void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        HttpUrl serverUrl = mockWebServer.url("/");

        // Override factory endpoint
        getProxy().setHost(serverUrl.host());
        getProxy().setPort(serverUrl.port());
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    public abstract T getProxy();
}
