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
package eu.seaclouds.common.entities.modaclouds.manager;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.java.JavaSoftwareProcessSshDriver;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.net.Networking;
import brooklyn.util.os.Os;
import brooklyn.util.ssh.BashCommands;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.List;

import static java.lang.String.format;

public class MODACloudsMonitoringManagerSshDriver extends JavaSoftwareProcessSshDriver implements MODACloudsMonitoringManagerDriver {

    public MODACloudsMonitoringManagerSshDriver(MODACloudsMonitoringManagerImpl entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    @Override
    protected String getLogFileLocation() {
        return Os.mergePathsUnix(getRunDir(), "console.out");
    }

    @Override
    public void preInstall() {
        resolver = Entities.newDownloader(this);
        setExpandedInstallDir(Os.mergePaths(getInstallDir(), resolver.getUnpackedDirectoryName(format("monitoring-manager-%s", getVersion()))));
    }

    @Override
    public void install() {
        List<String> urls = resolver.getTargets();
        String saveAs = resolver.getFilename();

        List<String> commands = ImmutableList.<String>builder()
                .addAll(BashCommands.commandsToDownloadUrlsAs(urls, saveAs))
                .add(BashCommands.INSTALL_TAR)
                .add("tar xzfv " + saveAs)
                .build();

        newScript(INSTALLING)
                .body.append(commands)
                .execute();
    }

    @Override
    public void customize() {
        log.debug("Customizing {}", entity);
        Networking.checkPortsValid(MutableMap.of("modacloudsMmPort", getPort()));
        newScript(CUSTOMIZING)
                .body.append(
                format("cp -R %s/* .", getExpandedInstallDir()),
                format("mkdir %s/modaclouds-mm", getRunDir())
        ).execute();
    }

    public String getPidFile() {
        return Os.mergePathsUnix(getRunDir(), "modaclouds-mm.pid");
    }

    @Override
    public void launch() {
        newScript(MutableMap.of(USE_PID_FILE, getPidFile()), LAUNCHING)
                .failOnNonZeroResultCode()
                .body.append(
                format("nohup java -jar monitoring-manager.jar " +
                                "-ddaip %s " +
                                "-ddaport %s " +
                                "-kbip %s " +
                                "-kbport %s " +
                                "-kbpath %s " +
                                "-mmport %s " +
                                "> %s 2>&1 &",
                        entity.getConfig(MODACloudsMonitoringManager.MODACLOUDS_DDA_IP),
                        entity.getConfig(MODACloudsMonitoringManager.MODACLOUDS_DDA_PORT),
                        entity.getConfig(MODACloudsMonitoringManager.MODACLOUDS_KB_IP),
                        entity.getConfig(MODACloudsMonitoringManager.MODACLOUDS_KB_PORT),
                        entity.getConfig(MODACloudsMonitoringManager.MODACLOUDS_KB_DATASET_PATH),
                        entity.getAttribute(MODACloudsMonitoringManager.MODACLOUDS_MM_PORT),
                        getLogFileLocation()))
                .execute();

        String mainUri = String.format("http://%s:%d",
                entity.getAttribute(Attributes.HOSTNAME),
                entity.getAttribute(MODACloudsMonitoringManager.MODACLOUDS_MM_PORT));
        entity.setAttribute(Attributes.MAIN_URI, URI.create(mainUri));
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
        return entity.getAttribute(MODACloudsMonitoringManager.MODACLOUDS_MM_PORT);
    }

}
