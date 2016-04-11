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
package org.apache.brooklyn.entity.php.httpd;


import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.api.sensor.Enricher;
import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.entity.php.PhpWebAppSoftwareProcessImpl;
import org.apache.brooklyn.feed.function.FunctionFeed;
import org.apache.brooklyn.feed.function.FunctionPollConfig;
import org.apache.brooklyn.feed.http.HttpFeed;
import org.apache.brooklyn.feed.http.HttpPollConfig;
import org.apache.brooklyn.feed.http.HttpValueFunctions;
import org.apache.brooklyn.util.guava.Functionals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class PhpHttpdServerImpl extends PhpWebAppSoftwareProcessImpl implements PhpHttpdServer {

    public static final Logger log = LoggerFactory.getLogger(PhpHttpdServerImpl.class);

    private volatile FunctionFeed functionFeed;
    private volatile HttpFeed httpFeed;
    private Enricher serviceUpEnricher;


    public PhpHttpdServerImpl() {
        super();
    }

    public PhpHttpdServerImpl(Map flags) {
        this(flags, null);
    }

    public PhpHttpdServerImpl(Map flags, Entity parent) {
        super(flags, parent);
    }

    @Override
    public Class getDriverInterface() {
        return PhpHttpdDriver.class;
    }

    @Override
    public PhpHttpdDriver getDriver() {
        return (PhpHttpdDriver) super.getDriver();
    }

    public String getDefaultGroup() {
        return getConfig(DEFAULT_GROUP);
    }

    public void setAppUser(String appName) {
        setConfig(APP_NAME, appName);
    }

    public String getInstallDir() {
        return getConfig(PhpHttpdServer.CONFIG_DIR);
    }

    public String getConfigurationDir() {
        return getConfig(CONFIGURATION_DIR);
    }

    public String getSiteConfigurationFile() {
        return getConfig(SITE_CONFIGURATION_FILE);
    }

    public void setSiteConfigurationFile(String siteConfigurationFile) {
        config().set(SITE_CONFIGURATION_FILE, siteConfigurationFile);
    }

    public String getDeployRunDir() {
        return getConfig(DEPLOY_RUN_DIR);
    }

    public String getSitesAvailableFolder() {
        return getConfig(SITES_AVAILABLE);
    }

    public String getConfigTemplate() {
        return getConfig(CONFIG_FILE_TEMPLATE);
    }
    
    public String getConfigurationFile() {
        return getConfig(CONFIG_FILE);
    }

    public Map<String, String> getPhpEnvVariables(){
        return getConfig(PhpHttpdServer.PHP_ENV_VARIABLES);
    }

    public Map<String, String> getDbConnectionConfigParams() {
        return getConfig(PHP_CONFIG_PARAMS);
    }

    public String getPhpVersion() {
        return getConfig(SUGGESTED_PHP_VERSION);
    }


    @Override
    public int getHttpPort() {
        return getAttribute(PhpHttpdServer.HTTP_PORT);
    }


    private Function<String, String> parseApacheStatus(final String key) {
        return new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String s) {
                String result = null;
                if ((s != null) && (key != null) && (s.contains(key))) {
                    int i = s.indexOf(key) + key.length() + 1;
                    int j = s.indexOf("\n", i);
                    result = s.substring(i, j).trim();
                }
                return result;
            }
        };
    }

    private <T> Function<String, T> cast(final Class<T> expected) {
        return new Function<String, T>() {
            @Nullable
            @Override
            public T apply(@Nullable String s) {
                if (s == null) {
                    return (T) null;
                } else if (expected == long.class || expected == Long.class) {
                    return (T) (Long) Long.parseLong(s);
                } else if (expected == int.class || expected == Integer.class) {
                    return (T) (Integer) Integer.parseInt(s);
                } else if (expected == double.class || expected == Double.class) {
                    return (T) (Double) Double.parseDouble(s);
                } else {
                    return (T) (String) s;
                }
            }
        };
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();
        functionFeed = FunctionFeed.builder()
                .entity(this)
                .poll(new FunctionPollConfig<Object, Boolean>(SERVICE_UP)
                        .period(500, TimeUnit.MILLISECONDS)
                        .callable(new Callable<Boolean>() {
                            public Boolean call() throws Exception {
                                return getDriver().isRunning();
                            }
                        })
                        .onException(Functions.constant(Boolean.FALSE)))
                .build();

        String monitorUri = String.format("http://%s:%s/%s",
                getAttribute(Attributes.HOSTNAME), getHttpPort(), getConfig(SERVER_STATUS_URL));
        httpFeed = HttpFeed.builder()
                .entity(this)
                .period(200)
                .baseUri(monitorUri)
                .poll(new HttpPollConfig<Boolean>(SERVER_STATUS_IS_UP)
                        .onSuccess(HttpValueFunctions.responseCodeEquals(200))
                        .onFailureOrException(Functions.constant(false)))
                .poll(new HttpPollConfig<Integer>(REQUEST_COUNT).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("Total Accesses"), cast(Integer.class))) )
                .poll(new HttpPollConfig<Long>(TOTAL_KBYTE).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("Total kBytes"), cast(Long.class))))
                .poll(new HttpPollConfig<Double>(CPU_LOAD).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("CPULoad"), cast(Double.class))))
                .poll(new HttpPollConfig<Integer>(TOTAL_PROCESSING_TIME).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("Uptime"), cast(Integer.class))))
                .poll(new HttpPollConfig<Double>(REQUEST_PER_SEC).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("ReqPerSec"), cast(Double.class))))
                .poll(new HttpPollConfig<Double>(BYTES_PER_SEC).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("BytesPerSec"), cast(Double.class))))
                .poll(new HttpPollConfig<Double>(BYTES_PER_REQ).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("BytesPerReq"), cast(Double.class))))
                .poll(new HttpPollConfig<Integer>(BUSY_WORKERS).onSuccess(
                        Functionals.chain(HttpValueFunctions.stringContentsFunction(),
                                parseApacheStatus("BusyWorkers"), cast(Integer.class))))
                .build();
    }

    @Override
    public String getShortName() {
        return "ApacheWebServerHttpd";
    }
}
