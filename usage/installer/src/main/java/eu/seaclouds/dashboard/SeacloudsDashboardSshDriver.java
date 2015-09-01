/*
 * Copyright 2014 SeaClouds
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
package eu.seaclouds.dashboard;

import static java.lang.String.format;

import java.net.URI;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.entity.java.JavaSoftwareProcessSshDriver;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.net.Networking;
import brooklyn.util.net.Urls;
import brooklyn.util.os.Os;
import brooklyn.util.ssh.BashCommands;

public class SeacloudsDashboardSshDriver extends JavaSoftwareProcessSshDriver implements SeacloudsDashboardDriver {
    
    public SeacloudsDashboardSshDriver(EntityLocal entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    @Override
    protected String getLogFileLocation() {
        return Os.mergePathsUnix(getRunDir(), "console.out");
    }

    @Override
    public void preInstall() {
        resolver = Entities.newDownloader(this, ImmutableMap.<String, Object>builder()
                .put("filename", "dashboard.jar").build());
        setExpandedInstallDir(Os.mergePaths(
                getInstallDir(), 
                resolver.getUnpackedDirectoryName(format("dashboard-%s", getVersion()))));
    }
    
    @Override
    public void install() {
        List<String> urls = resolver.getTargets();
        String saveAs = resolver.getFilename();

        List<String> commands = ImmutableList.<String>builder()
                .addAll(BashCommands.commandsToDownloadUrlsAs(urls, saveAs))
                .build();

        newScript(INSTALLING)
                .body.append(commands)
                .execute();
    }

    @Override
    public void customize() {
        log.debug("Customizing {}", entity);
        Networking.checkPortsValid(MutableMap.of("dashboardPort", getPort()));
        newScript(CUSTOMIZING)
                .body.append(
                        format("cp -R %s/* .", getInstallDir()),
                        format("mkdir %s/seaclouds-dashboard", getRunDir())
                ).execute();
        copyTemplate(entity.getConfig(SeacloudsDashboard.CONFIG_URL), getConfigFileLocation());
    }

    @Override
    public void launch() {
        newScript(MutableMap.of(USE_PID_FILE, getPidFile()), LAUNCHING)
                .failOnNonZeroResultCode()
                .body.append(getCommand()).execute();

        String mainUri = format("http://%s:%d", 
                entity.getAttribute(Attributes.HOSTNAME), 
                entity.getAttribute(SeacloudsDashboard.DASHBOARD_PORT));
        entity.setAttribute(Attributes.MAIN_URI, URI.create(mainUri));
    }
    
    private String getCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append(format("nohup java -jar dashboard.jar server " + getFinalConfigName() +
                        " > %s 2>&1 &",
                getLogFileLocation()));
        return sb.toString();
    }
    
    public String getPidFile() {
        return Os.mergePathsUnix(getRunDir(), "seaclouds-dashboard.pid");
    }
    
    @Override
    public boolean isRunning() {
        return newScript(MutableMap.of(USE_PID_FILE, getPidFile()), CHECK_RUNNING).execute() == 0;
    }

    @Override
    public void stop() {
        newScript(ImmutableMap.of(USE_PID_FILE, getPidFile()), STOPPING).execute();
    }

    @Override
    public Integer getPort() {
        return entity.getAttribute(SeacloudsDashboard.DASHBOARD_PORT);
    }

    protected String getConfigFileLocation() {
        return Urls.mergePaths(getRunDir(), getFinalConfigName());
    }
    
    protected String getFinalConfigName() {
        return entity.getConfig(SeacloudsDashboard.FINAL_CONFIG_NAME);
    }
}
