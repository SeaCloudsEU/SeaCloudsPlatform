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

package eu.seaclouds.platform.planner.optimizer;

import eu.seaclouds.platform.planner.optimizer.util.TOSCAkeywords;

public class CloudOffer {

   private String name;
   private double performance;
   private double availability;
   private double cost;
   private double numCores;

   public CloudOffer(String name, double performance, double availability,
         double cost, double numCores) {

      this.name = name;

      // We expect to save here the value MU (service rate) of the cloud offer
      // (it works because, although it is not very modular to store
      // info here, CloudOffers are members of a list of SuitableSolutions for
      // module).
      this.performance = performance;
      this.availability = availability;
      this.cost = cost;
      this.numCores = numCores;
   }

   // NumCores not specified, assuming 1
   public CloudOffer(String name, double performance, double availability,
         double cost) {

      this(name, performance, availability, cost, 1.0);

   }

   public CloudOffer(String name) {
      this.name = name;
      this.performance = 0;
      this.availability = 0;
      this.cost = 0;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public double getPerformance() {
      return performance;
   }

   public void setPerformance(double performance) {
      this.performance = performance;
   }

   public double getAvailability() {
      return availability;
   }

   public void setAvailability(double availability) {
      this.availability = availability;
   }

   public double getCost() {
      return cost;
   }

   public void setCost(double cost) {
      this.cost = cost;
   }

   public CloudOffer clone() {
      return new CloudOffer(name, performance, availability, cost);
   }

   public double getNumCores() {
      return numCores;
   }

   public void setNumCores(double numCores) {
      this.numCores = numCores;
   }

   /**
    * @return The part of the cloud offer name before the first dot (".")
    */
   public String getProviderName() {
      // TODO: Test this method with several offers names since the split method
      // gives curious results sometimes (null)
      return name.split(TOSCAkeywords.CLOUD_OFFER_PROVIDER_NAME_SEPARATOR)[0];
   }

}
