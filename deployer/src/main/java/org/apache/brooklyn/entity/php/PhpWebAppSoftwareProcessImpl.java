/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.entity.php;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.brooklyn.api.entity.Entity;
import org.apache.brooklyn.entity.SourceNameResolver;
import org.apache.brooklyn.entity.software.base.SoftwareProcessImpl;
import org.apache.brooklyn.entity.webapp.WebAppServiceMethods;
import org.apache.brooklyn.util.text.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class PhpWebAppSoftwareProcessImpl extends SoftwareProcessImpl implements PhpWebAppSoftwareProcess {
    private static final Logger LOG = LoggerFactory.getLogger(PhpWebAppSoftwareProcessImpl.class);

    public PhpWebAppSoftwareProcessImpl() {
        super();
    }

    public PhpWebAppSoftwareProcessImpl(Entity parent) {
        this(new LinkedHashMap(), parent);
    }

    public PhpWebAppSoftwareProcessImpl(Map flags) {
        this(flags, null);
    }

    public PhpWebAppSoftwareProcessImpl(Map flags, Entity parent) {
        super(flags, parent);
    }

    public PhpWebAppDriver getDriver() {
        return (PhpWebAppDriver) super.getDriver();
    }

    protected Set<String> getEnabledProtocols() {
        return getAttribute(PhpWebAppSoftwareProcess.ENABLED_PROTOCOLS);
    }

    protected Set<String> getDeployedApps() {
        return getAttribute(DEPLOYED_PHP_APPS);
    }
    
    protected void setDeployedApps(Set<String> deployedPhpApps) {
        setAttribute(DEPLOYED_PHP_APPS, deployedPhpApps);
    }
    
    protected int getHttpPort() {
        return getAttribute(HTTP_PORT);
    }

    public String getAppName() {
        return getConfig(APP_NAME);
    }
    
    @Override
    protected void connectSensors() {
        super.connectSensors();
        WebAppServiceMethods.connectWebAppServerPolicies(this);
    }


    @Override
    protected void preStop(){
        super.preStop();
        //zero our workrate derived workrates.
        //TODO might not be enough, as a policy may still be executing and have a record of historic vals;
        // should remove policies
        // also nor sure we want this; implies more generally a resposibility for sensor to announce things
        // disconnected
        
        // Resetting sensor values
        setAttribute(REQUESTS_PER_SECOND_LAST, 0D);
        setAttribute(REQUESTS_PER_SECOND_IN_WINDOW, 0D);
    }

    // TODO thread-safety issues: if multiple concurrent calls, may break (e.g. deployment_wars being reset)
    public void deployInitialApplications() {
        initDeployAppAttributeIfIsNull();

        String gitRepoUrl = getConfig(GIT_URL);
        String tarballResourceUrl = getConfig(TARBALL_URL);
        
        if (gitRepoUrl != null) {
            String targetName = inferCorrectAppGitName();
            deployGitResource(gitRepoUrl, targetName);
        } else if (tarballResourceUrl != null) {
            String targetName = inferCorrectAppTarballName();
            deployTarballResource(tarballResourceUrl, targetName);
        }
        
    }

    private void initDeployAppAttributeIfIsNull() {
        if (getDeployedApps() == null)
            setDeployedApps(Sets.<String>newLinkedHashSet());
    }

    private String inferCorrectAppGitName() {
        String result;
        if (Strings.isEmpty(getConfig(APP_NAME))) {
            result = SourceNameResolver.getNameOfRepositoryGitFromHttpsUrl(getConfig(GIT_URL));
        } else {
            result = getConfig(APP_NAME);
        }
        return result;
    }

    private String inferCorrectAppTarballName() {
        String result;
        if (Strings.isEmpty(getConfig(APP_NAME))) {
            result = SourceNameResolver.getIdOfTarballFromUrl(getConfig(TARBALL_URL));
        } else {
            result = getConfig(APP_NAME);
        }
        return result;
    }

    public void deployGitResource(String url, String targetName) {
        try {
            doDeployGitResource(url, targetName);
        } catch (RuntimeException e) {
            LOG.error("Error deploying '" + url + "' on " + toString() + "; rethrowing...", e);
            throw Throwables.propagate(e);
        }
    }

    private void doDeployGitResource(String url, String targetName) {
        checkNotNull(url, "url");
        PhpWebAppDriver driver = getDriver();
        String deployedAppName = driver.deployGitResource(url, targetName);
        updateDeploymentSensorToDeployAnApp(deployedAppName);
    }

    public void deployTarballResource(String url, String targetName) {
        try {
            doDeployTarballResource(url, targetName);
        } catch (RuntimeException e) {
            LOG.error("Error deploying '" + url + "' on " + toString() + "; rethrowing...", e);
            throw Throwables.propagate(e);
        }
    }

    private void doDeployTarballResource(String url, String targetName) {
        //TODO deployment git resource
        checkNotNull(url, "url");
        PhpWebAppDriver driver = getDriver();
        String deployedAppName = driver.deployTarballResource(url, targetName);
        updateDeploymentSensorToDeployAnApp(deployedAppName);
    }

    private void updateDeploymentSensorToDeployAnApp(String deployedAppName) {
        Set<String> deployedPhpApps = getDeployedApps();
        if (deployedPhpApps == null) {
            deployedPhpApps = Sets.newLinkedHashSet();
        }
        deployedPhpApps.add(deployedAppName);
        setDeployedApps(deployedPhpApps);
    }
    
    public void undeploy(String targetName) {
        try {
            doUndeploy(targetName);
        } catch (RuntimeException e) {
            LOG.error("Error undeploying '" + targetName + "' on " + toString() + "; rethrowing...", e);
            throw Throwables.propagate(e);
        }
    }

    private void doUndeploy(String targetName) {
        PhpWebAppDriver driver = getDriver();
        driver.undeploy(targetName);
        
        // Updating deploy sensor
        initDeployAppAttributeIfIsNull();
        Set<String> deployedPhpApps = getDeployedApps();
        deployedPhpApps.remove(targetName);
        setDeployedApps(deployedPhpApps);
    }


    
}
