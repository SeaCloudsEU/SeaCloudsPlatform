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
package org.apache.brooklyn.entity.openshift.webapp;

import org.apache.brooklyn.core.entity.Attributes;
import org.apache.brooklyn.entity.openshift.PaasEntityOpenShiftDriver;
import org.apache.brooklyn.location.openshift.OpenShiftPaasLocation;
import com.openshift.client.ApplicationBuilder;
import com.openshift.client.ApplicationScale;
import com.openshift.client.IApplication;
import com.openshift.client.IDomain;
import com.openshift.client.IUser;
import com.openshift.client.cartridge.IStandaloneCartridge;
import com.openshift.client.cartridge.StandaloneCartridge;
import com.openshift.client.cartridge.query.LatestVersionOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class PaasWebAppOpenShiftDriver extends PaasEntityOpenShiftDriver
        implements PaasWebAppDriver {

    public static final Logger log = LoggerFactory.getLogger(PaasWebAppOpenShiftDriver.class);

    private String gitRepositoryUrl;
    private String applicationName;
    private IUser user;
    //TODO does it change the name of domain to usedDomain? a user could have several domains.
    private IDomain domain;
    private IApplication deployedApp=null;
    private String domainName;

    public PaasWebAppOpenShiftDriver(OpenShiftWebAppImpl entity,
                                     OpenShiftPaasLocation location) {
        super(entity, location);
    }

    @Override
    protected void init() {
        super.init();
        initApplicationParameters();
    }

    @SuppressWarnings("unchecked")
    private void initApplicationParameters() {
        //TODO check if application name is a valid id
        applicationName = getEntity().getConfig(OpenShiftWebApp.APPLICATION_NAME);
        //TODO check if the url is git url
        gitRepositoryUrl = getEntity().getConfig(OpenShiftWebApp.GIT_REPOSITORY_URL);
        //TODO check if the domain is valid
        domainName=getEntity().getConfig(OpenShiftWebApp.DOMAIN);
    }

    @Override
    public OpenShiftWebAppImpl getEntity() {
        return (OpenShiftWebAppImpl) super.getEntity();
    }

    protected String getApplicationUrl(){
        return gitRepositoryUrl;
    }

    protected String getApplicationName(){
        return applicationName;
    }

    @Override
    public boolean isRunning() {
        //if the appliation is null then it was not deployed correctly
        return deployedApp != null;
    }

    @Override
    public void start() {
        super.start();

        preDeploy();
        deploy();
        preLaunch();
        launch();
        postLaunch();
    }

    public void preDeploy() {
        initOpenShiftUser();
        domain=createDomainIfNotExist(domainName);
    }

    /**
     * This method initialize the OpenShift user
     */
    private void initOpenShiftUser(){
        user=getClient().getUser();
    }

    /**
     * This method checks if the domain exists for the user. If the domain was not found,
     * it will be created using the domainId.
     * @param domainId The domainId which will be used for finding o creating the domain the
     *                 user.
     * @return a the found or created domain.
     */
    private IDomain createDomainIfNotExist(String domainId){
        IDomain foundDomain=findDomainByID(domainId);
        if(foundDomain==null){
            foundDomain=user.createDomain(domainName);
        }
        return foundDomain;
    }

    private IDomain findDomainByID(String domainsId){
        IDomain result=null;
        if(user!=null){
            List<IDomain> domains = user.getDomains();
            for(IDomain domain:domains){
                if(domain.getId().equals(domainsId)){
                    result=domain;
                    break;
                }
            }
        }
        return result;
    }

    public void deploy(){
        //TODO could be refactor to a CONFIGKEY
        ApplicationScale scale1 = ApplicationScale.NO_SCALE;

        //TODO fix this call, probably we use the second alternativa which allows to selec a cartridge from a id
        StandaloneCartridge cartridge = new StandaloneCartridge(getEntity().getCartridge());

        deployedApp = new ApplicationBuilder(domain)
                .setName(applicationName)
                .setStandaloneCartridge(cartridge)
                .setApplicationScale(scale1)
                .setInitialGitUrl(gitRepositoryUrl)
                .build();
    }

    public void preLaunch() {
        //TODO envs
        configureEnv();
    }

    public void launch() {
        deployedApp.start();
    }

    public void postLaunch() {
        getEntity().setAttribute(Attributes.MAIN_URI, URI.create(deployedApp.getApplicationUrl()));
        getEntity().setAttribute(OpenShiftWebApp.ROOT_URL, deployedApp.getApplicationUrl());

        //TODO, adding domain used as a sensor sensor(domain)
    }

    @Override
    public void restart() {
        // TODO: complete
    }

    @Override
    public void stop() {
        deployedApp.stop();
        deleteApplication();
    }

    @Override
    public void deleteApplication() {
        deployedApp.destroy();
    }

    @Override
    public void setEnv(String key, String value) {
        deployedApp.addEnvironmentVariable(key, value);
    }

    /**
     * Add the env tot he application
     */
    protected void configureEnv() {
        //TODO a sensor with the custom-environment variables?
        Map<String, String> envs=getEntity().getConfig(OpenShiftWebApp.ENV);
        if((envs!=null)&&(envs.size()!=0)){
            deployedApp.addEnvironmentVariables(envs);
            deployedApp.restart();
        }
    }


}