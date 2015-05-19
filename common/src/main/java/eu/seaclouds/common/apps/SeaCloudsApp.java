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
package eu.seaclouds.common.apps;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.AbstractApplication;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.location.Location;
import brooklyn.management.internal.CollectionChangeListener;
import brooklyn.management.internal.ManagementContextInternal;
import brooklyn.policy.PolicySpec;
import brooklyn.policy.basic.AbstractPolicy;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.text.Strings;
import eu.seaclouds.common.policies.DataCollectorInstallationPolicy;

public class SeaCloudsApp extends AbstractApplication {
   /**
    * The default name to use for this app, if not explicitly overridden by the top-level app.
    * Necessary to avoid the app being wrapped in another layer of "BasicApplication" on deployment.
    * Previously, the catalog item gave an explicit name (rathe than this defaultDisplayName), which
    * meant that if the user chose a different name then AMP would automatically wrap this app so
    * that both names would be presented.
    */
   public static final ConfigKey<String> DEFAULT_DISPLAY_NAME = ConfigKeys.newStringConfigKey("defaultDisplayName");

   /**
    * childSpec.
    */
   @SuppressWarnings("serial")
   public static final ConfigKey<EntitySpec<?>> CHILD_SPEC = ConfigKeys.newConfigKey(
           new TypeToken<EntitySpec<?>>() {
           },
           "childSpec");

   /**
    * Config Key for Install Data CollectorOption.
    */
   @SetFromFlag("installDataCollector")
   public static final ConfigKey<Boolean> INSTALL_DATA_COLLECTOR = ConfigKeys
           .newBooleanConfigKey("seaclouds.installDataCollector",
                   "Determines whether the Data Collector agent is installed",
                   Boolean.TRUE);

   private static final Logger LOG = LoggerFactory.getLogger(SeaCloudsApp.class);

   @Override
   public void init() {
      super.init();
      if (Strings.isNonBlank(getConfig(DEFAULT_DISPLAY_NAME))) {
         setDefaultDisplayName(getConfig(DEFAULT_DISPLAY_NAME));
      }
   }

   /**
    * stopDataCollectorMonitoring method.
    */
   public final void stopDataCollectorMonitoring() {

      for (final DataCollectorInstallationPolicy policy : this.getDescedentPolicies(DataCollectorInstallationPolicy.class)) {
         policy.stopDataCollectorMonitoring();
      }
   }

   /**
    * startDataCollectorMonitoring method.
    */
   public final void startDataCollectorMonitoring(String ddaHostname, String ddaPort, String kbHostname, String kbPort) {

      for (final DataCollectorInstallationPolicy policy : this.getDescedentPolicies(DataCollectorInstallationPolicy.class)) {
         policy.startDataCollectorMonitoring(ddaHostname, ddaPort, kbHostname, kbPort);
      }
   }

   /**
    * Get Specific Policy Iterable.
    *
    * @param policyClass Compose Policy Class.
    * @param <T>         Type which extends AbstractPolicy.
    * @return Iterable<T> Specific Policy Class.
    */
   protected final <T extends AbstractPolicy> Iterable<T> getDescedentPolicies(Class<T> policyClass) {
      final Set<T> descPolicy = Sets.newHashSet();
      for (final SoftwareProcess descendents : Entities.descendants(this, SoftwareProcess.class)) {
         descPolicy.addAll(ImmutableList
                 .copyOf(Iterables.filter(descendents.getPolicies(), policyClass)));
      }
      return descPolicy;
   }

   @Override
   public final void start(Collection<? extends Location> locations) {
      addLocations(locations);
      final Collection<? extends Location> locationsToUse = getLocations();
      final EntitySpec<?> childSpec = getConfig(CHILD_SPEC);

      this.getMutableEntityType()
              .addEffector(new MethodEffector<Void>(SeaCloudsApp.class, "stopDataCollectorMonitoring"));
      this.getMutableEntityType()
              .addEffector(new MethodEffector(SeaCloudsApp.class, "startDataCollectorMonitoring"));

      ((ManagementContextInternal) getManagementContext())
              .addEntitySetListener(new CollectionChangeListener<Entity>() {

                 @Override
                 public void onItemAdded(Entity entity) {
                    if (SeaCloudsApp.this.checkEntity(entity)) {
                       final PolicySpec<DataCollectorInstallationPolicy> spec = PolicySpec.create(DataCollectorInstallationPolicy.class);
                       entity.addPolicy(spec);
                    }
                 }

                 @Override
                 public void onItemRemoved(Entity entity) {
                    // no action required
                 }
              });

      final Entity child = addChild(childSpec);
      Entities.manage(child);

      super.start(locationsToUse);
   }

   private boolean checkEntity(Entity entity) {
      return entity instanceof SoftwareProcess
              && Entities.isAncestor(entity, this)
              && Iterables.isEmpty(Iterables.filter(entity.getPolicies(), DataCollectorInstallationPolicy.class));
   }

}
