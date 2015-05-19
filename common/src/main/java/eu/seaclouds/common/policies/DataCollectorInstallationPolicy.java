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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.event.SensorEvent;
import brooklyn.event.SensorEventListener;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.policy.basic.AbstractPolicy;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.ssh.BashCommands;
import eu.seaclouds.common.apps.SeaCloudsApp;

/**
 * MODAClouds data collector installation policy Class enables User's VM monitoring.
 * This class gets called on creation of new node, which will in turn installs
 * MODAClouds data collector on targeted VM.
 */
public class DataCollectorInstallationPolicy extends AbstractPolicy {
   /**
    * Logger object.
    */
   private static final Logger LOG = LoggerFactory.getLogger(DataCollectorInstallationPolicy.class);

   /**
    * Flag.
    */
   @SetFromFlag private final AtomicBoolean dataCollectorInstalled = new AtomicBoolean(false);

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
               final Boolean configkey = entity.getConfig(SeaCloudsApp.INSTALL_DATA_COLLECTOR);
               String hostName = sensorEvent.getSource().getParent().getApplication().getDisplayName()
                       + "-"
                       + ((SshMachineLocation) sensorEventValue).getPublicAddresses();
               hostName = hostName.replaceAll(" ", "_");
               final DataCollectorInstallationPolicy thisNRInstallationPolicy = DataCollectorInstallationPolicy.this;
               thisNRInstallationPolicy
                       .installDataCollector((SshMachineLocation) sensorEventValue, hostName);
               if (configkey) {
                  thisNRInstallationPolicy.startDataCollectorMonitoring();
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
    * @param location   newrelic blueprint machine location.
    * @param hostName   blueprint machine hostname.
    */
   private void installDataCollector(SshMachineLocation location, String hostName) {
      if (!this.getDataCollectorInstalled().getAndSet(true)) {

         /**
          * Persist the changes to dataCollectorInstalled.
          */
         requestPersist();
         final List<String> cmds = Lists.newArrayList();
         cmds.add("cd /tmp");
         cmds.add(BashCommands.sudo("rpm -Uvh newrelic.rpm"));
         cmds.add(BashCommands.sudo("yum install newrelic-sysmond -y"));
         int returnvalue = location.execScript("Download and run the NEWRELIC rpm", cmds);
         this.checkReturnValue(returnvalue, "Install RMP");
         cmds.clear();

         cmds.add(BashCommands.sudo(String
                 .format("sed -i.bk s/license_key=.*/license_key=%s/g /etc/newrelic/" + "nrsysmond.cfg")));
         returnvalue = location.execScript("Install Data collector licence key", cmds);
         this.checkReturnValue(returnvalue, "Install Licence key in config file");
         cmds.clear();

         cmds.add(BashCommands.sudo("chmod 646 /etc/newrelic/nrsysmond.cfg"));
         cmds.add(BashCommands.sudo(String.format("echo \"hostname=%s\" >> /etc/newrelic/nrsysmond.cfg", hostName)));
         cmds.add(BashCommands.sudo("chmod 640 /etc/newrelic/nrsysmond.cfg"));
         returnvalue = location.execScript("put hostname and license key in config file", cmds);
         this.checkReturnValue(returnvalue, "place license key and hostname in nrsysmond.cfg file");
         cmds.clear();
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
         final List<String> cmds = Lists.newArrayList();
         cmds.add(BashCommands.sudo("/etc/init.d/newrelic-sysmond stop"));
         cmds.add(BashCommands.sudo("yum remove newrelic-sysmond -y"));
         final int retval = location.execScript("uninstall the newrelic", cmds);
         this.checkReturnValue(retval, "uninstall newrelic");
      }
   }

   /**
    * Stop Data collector Monitoring.
    */
   public final void stopDataCollectorMonitoring() {
      requestPersist();
      final SshMachineLocation location = (SshMachineLocation) entity.getLocations().iterator().next();
      final List<String> cmds = Lists.newArrayList();
      cmds.add(BashCommands.sudo("/etc/init.d/newrelic-sysmond stop"));
      cmds.add(BashCommands.sudo("chkconfig newrelic-sysmond off"));
      final int retV = location.execScript("Stop New Relic Process", cmds);
      this.checkReturnValue(retV, "stop newrelic process");
   }

   /**
    * Start Data collector Monitoring.
    */
   public final void startDataCollectorMonitoring() {
      requestPersist();
      final SshMachineLocation location = (SshMachineLocation) entity.getLocations().iterator().next();
      final List<String> cmds = Lists.newArrayList();
      cmds.add(BashCommands.sudo("/etc/init.d/newrelic-sysmond start"));
      cmds.add(BashCommands.sudo("chkconfig newrelic-sysmond on"));
      final int ret = location.execScript("start New Relic process", cmds);
      this.checkReturnValue(ret, "start newrelic process");
   }

}