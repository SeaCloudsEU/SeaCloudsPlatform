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
package eu.seaclouds.common.policies;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.event.SensorEvent;
import brooklyn.event.SensorEventListener;
import brooklyn.location.Location;
import brooklyn.location.OsDetails;
import brooklyn.location.basic.Locations;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.policy.basic.AbstractPolicy;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.config.ConfigBag;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.internal.ssh.SshTool;
import brooklyn.util.ssh.BashCommands;
import eu.seaclouds.common.apps.SeaCloudsApp;

/**
 * MODAClouds data collector installation policy Class enables User's VM monitoring.
 * This class gets called on creation of new node, which will in turn installs
 * MODAClouds data collector on targeted VM.
 */
public class DataCollectorInstallationPolicy extends AbstractPolicy {

   private static final Logger LOG = LoggerFactory.getLogger(DataCollectorInstallationPolicy.class);
   public static final String INSTALL_DIR = "~/datacollectors";
   public static final String PID_FILENAME = "pid.txt";

   @SetFromFlag("modacloudsDdaIp")
   public static final ConfigKey<String> MODACLOUDS_DDA_IP = ConfigKeys.newStringConfigKey("modaclouds.dda.ip", "", "127.0.0.1");

   @SetFromFlag("modacloudsDdaPort")
   public static final ConfigKey<String> MODACLOUDS_DDA_PORT = ConfigKeys.newStringConfigKey("modaclouds.dda.port", "", "8175");

   @SetFromFlag("modacloudsKbIp")
   public static final ConfigKey<String> MODACLOUDS_KB_IP = ConfigKeys.newStringConfigKey("modaclouds.kb.ip", "", "127.0.0.1");

   @SetFromFlag("modacloudsKbPort")
   public static final ConfigKey<String> MODACLOUDS_KB_PORT = ConfigKeys.newStringConfigKey("modaclouds.kb.port", "", "3030");

   private final AtomicBoolean dataCollectorInstalled = new AtomicBoolean(false);

   /**
    * Get dataCollectorInstalled flag.
    *
    * @return AtomicBoolean.
    */
   public final AtomicBoolean getDataCollectorInstalled() {
      return this.dataCollectorInstalled;
   }

   /**
    * Handle Policy Events.
    *
    * @param entity blueprint entity.
    */
   @Override public final void setEntity(final EntityLocal entity) {
      super.setEntity(entity);
      subscribe(entity, AbstractEntity.LOCATION_ADDED, new SensorEventListener<Location>() {
         /**
          * Location added event.
          * @param sensorEvent SensorEvent.
          */
         @Override public void onEvent(SensorEvent<Location> sensorEvent) {
            final Object sensorEventValue = sensorEvent.getValue();
            if (sensorEventValue instanceof SshMachineLocation) {
               final Boolean installDataCollector = entity.getConfig(SeaCloudsApp.INSTALL_DATA_COLLECTOR);
               if (installDataCollector) {
                  DataCollectorInstallationPolicy.this.installDataCollector((SshMachineLocation) sensorEventValue);
                  getManagementContext().getConfig().getConfig(MODACLOUDS_DDA_IP);
                  String ddaHostname = DataCollectorInstallationPolicy.this.getConfig(MODACLOUDS_DDA_IP);
                  String ddaPort = DataCollectorInstallationPolicy.this.getConfig(MODACLOUDS_DDA_PORT);
                  String kbHostname = DataCollectorInstallationPolicy.this.getConfig(MODACLOUDS_KB_IP);
                  String kbPort = DataCollectorInstallationPolicy.this.getConfig(MODACLOUDS_KB_PORT);
                  DataCollectorInstallationPolicy.this.startDataCollectorMonitoring(ddaHostname, ddaPort, kbHostname, kbPort);
               }
            }
         }
      });

      subscribe(entity, AbstractEntity.LOCATION_REMOVED, new SensorEventListener<Location>() {
         /**
          * Location removed event.
          *
          * @param sensorEvent SensorEvent.
          */
         @Override public void onEvent(SensorEvent<Location> sensorEvent) {
            final Object sensorEventValue = sensorEvent.getValue();
            if (sensorEventValue instanceof SshMachineLocation) {
               DataCollectorInstallationPolicy.this.uninstallDataCollector((SshMachineLocation) sensorEventValue);
            }
         }
      });
   }

   /**
    * Install Data collector Monitoring.
    *
    */
   private void installDataCollector(SshMachineLocation location) {
      if (!this.getDataCollectorInstalled().getAndSet(true)) {

         // Persist the changes to dataCollectorInstalled.
         requestPersist();
         ImmutableList.Builder<String> commandsBuilder = ImmutableList.builder();

         commandsBuilder.add("mkdir -p " + INSTALL_DIR)
                 .add("cd " + INSTALL_DIR);
         OsDetails os = location.getOsDetails();
         if (!os.isMac()) {
            commandsBuilder.add(BashCommands.sudo(BashCommands.installJava(7)))
                    .add(BashCommands.INSTALL_WGET)
                    .add(BashCommands.INSTALL_UNZIP);
         }
         commandsBuilder.add("wget -O data-collector-1.3-SNAPSHOT.jar \"https://github.com/imperial-modaclouds/modaclouds-data-collectors/releases/download/1.3-Snapshot/data-collector-1.3-SNAPSHOT.jar\"")
                 .add("wget -O hyperic-sigar-1.6.4.zip \"http://sourceforge.net/projects/sigar/files/sigar/1.6/hyperic-sigar-1.6.4.zip/download?use_mirror=switch\"")
                 .add("unzip hyperic-sigar-1.6.4.zip");
         ByteArrayOutputStream stdout = new ByteArrayOutputStream();
         ByteArrayOutputStream stderr = new ByteArrayOutputStream();

         ConfigBag flags = ConfigBag.newInstance()
                 .configure(SshTool.PROP_OUT_STREAM, stdout)
                 .configure(SshTool.PROP_ERR_STREAM, stderr);
         int exitStatus = location.execScript(flags.getAllConfig(), "test PATH", ImmutableList.of("echo $PATH"));

         exitStatus = location.execScript(flags.getAllConfig(), "Download and run the data collector", commandsBuilder.build());
         checkReturnValue(exitStatus, "Install Data Collector");
      }
   }

   /**
    * Validate script execution.
    *
    * @param returnvalue checkReturnValue method variable.
    * @param phase       returns the phase value.
    */
   private void checkReturnValue(int returnvalue, String phase) {
      if (returnvalue != 0) {
         LOG.error("Error installing Data collector during '{}', return code {}", phase, returnvalue);
         throw new IllegalStateException(
                 String.format("Data collector installation failed during phase %s, return value %s ", phase,
                         returnvalue));
      }
   }

   /**
    * Un-register Data collector Monitoring.
    *
    * @param location blueprint machine location.
    */
   private void uninstallDataCollector(SshMachineLocation location) {
      if (this.getDataCollectorInstalled().getAndSet(false)) {
         final List<String> commands = Lists.newArrayList();
         commands.add("rm -rf " + INSTALL_DIR);
         final int retval = location.execScript("uninstall the data collector", commands);
         this.checkReturnValue(retval, "uninstall data collector");
      }
   }

   /**
    * Stop Data collector Monitoring.
    */
   public final void stopDataCollectorMonitoring() {
      requestPersist();
      SshMachineLocation location = Locations.findUniqueSshMachineLocation(entity.getLocations()).get();
      final List<String> commands = Lists.newArrayList();
      commands.add("cd " + INSTALL_DIR);
      commands.add(String.format("test -f %s && { kill -9 `cat %s`; rm %s;", PID_FILENAME, PID_FILENAME, PID_FILENAME));
      final int retV = location.execScript("Stop data collector Process", commands);
      this.checkReturnValue(retV, "stop data collector process");
   }

   /**
    * Start Data collector Monitoring.
    */
   public final void startDataCollectorMonitoring(String ddaHostname, String ddaPort, String kbHostname, String kbPort) {
      requestPersist();
      final SshMachineLocation location = (SshMachineLocation) entity.getLocations().iterator().next();
      final List<String> commands = Lists.newArrayList();
      commands.add("cd " + INSTALL_DIR);
      commands.add("nohup java -Djava.library.path=./hyperic-sigar-1.6.4/sigar-bin/lib/ -jar data-collector-1.3-SNAPSHOT.jar kb &");
      commands.add(String.format("echo $! > %s", PID_FILENAME));
      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      ByteArrayOutputStream stderr = new ByteArrayOutputStream();

      ConfigBag flags = ConfigBag.newInstance()
              .configure(SshTool.PROP_NO_EXTRA_OUTPUT, true)
              .configure(SshTool.PROP_OUT_STREAM, stdout)
              .configure(SshTool.PROP_ERR_STREAM, stderr);
      Map<String, String> env = MutableMap.of(
              "MODACLOUDS_MONITORING_DDA_ENDPOINT_IP", ddaHostname,
              "MODACLOUDS_MONITORING_DDA_ENDPOINT_PORT", ddaPort,
              "MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_IP", kbHostname,
              "MODACLOUDS_KNOWLEDGEBASE_ENDPOINT_PORT", kbPort,
              "MODACLOUDS_KNOWLEDGEBASE_DATASET_PATH", "/modaclouds/kb",
              "MODACLOUDS_KNOWLEDGEBASE_SYNC_PERIOD", "10");
      int ret = location.execScript(flags.getAllConfig(), "start data collector process", commands, env);
      this.checkReturnValue(ret, "start data collector process");
   }

}