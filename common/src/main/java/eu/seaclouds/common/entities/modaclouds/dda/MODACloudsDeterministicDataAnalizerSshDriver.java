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
package eu.seaclouds.common.entities.modaclouds.dda;

import static java.lang.String.format;
import java.net.URI;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.java.JavaSoftwareProcessSshDriver;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.net.Networking;
import brooklyn.util.os.Os;
import brooklyn.util.ssh.BashCommands;

public class MODACloudsDeterministicDataAnalizerSshDriver extends JavaSoftwareProcessSshDriver implements MODACloudsDeterministicDataAnalizerDriver {

   public MODACloudsDeterministicDataAnalizerSshDriver(MODACloudsDeterministicDataAnalyzerImpl entity, SshMachineLocation machine) {
      super(entity, machine);
   }

   @Override
   protected String getLogFileLocation() {
      return Os.mergePathsUnix(getRunDir(), "console.out");
   }

   @Override
   public void preInstall() {
      resolver = Entities.newDownloader(this);
      setExpandedInstallDir(Os.mergePaths(getInstallDir(), resolver.getUnpackedDirectoryName(format("data-analyzer-%s", getVersion()))));
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
      Networking.checkPortsValid(MutableMap.of("modacloudsDdaPort", getPort()));
      newScript(CUSTOMIZING)
              .body.append(
              format("cp -R %s/* .", getExpandedInstallDir()),
              format("mkdir %s/modaclouds-dda", getRunDir())
              ).execute();
   }

   public String getPidFile() {
      return Os.mergePathsUnix(getRunDir(), "modaclouds-dda.pid");
   }

   @Override
   public void launch() {
      newScript(MutableMap.of(USE_PID_FILE, getPidFile()), LAUNCHING)
              .failOnNonZeroResultCode()
              .body.append(
               format("nohup java -Xmx1200M -jar tower4clouds-data-analyzer.jar > %s 2>&1 &", getLogFileLocation()))
              .execute();

      String mainUri = String.format("http://%s:%d", entity.getAttribute(Attributes.HOSTNAME), entity.getAttribute(MODACloudsDeterministicDataAnalyzer.MODACLOUDS_DDA_PORT));
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
      return entity.getAttribute(MODACloudsDeterministicDataAnalyzer.MODACLOUDS_DDA_PORT);
   }

}
