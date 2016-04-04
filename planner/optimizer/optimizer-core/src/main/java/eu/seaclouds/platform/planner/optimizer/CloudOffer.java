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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CloudOffer {

   static Logger log = LoggerFactory.getLogger(CloudOffer.class);
   
   //full name of the offer
   private String name;
   //Name of the cloud provider, not the datacenter code
   private String provider;
   //Country+city where the offer is located
   private String location;
   private double performanceForExecutionUnit;
   private double availability;
   private double cost;
   private double numCores;

   
   
   public CloudOffer(String name, double performance, double availability,
         double cost, double numCores, String provider, String location) {

      
      this.name = name;

      // We expect to save here the value MU (service rate) of the cloud offer
      // (it works because, although it is not very modular to store
      // info here, CloudOffers are members of a list of SuitableSolutions for
      // module).
   // in service rate
      this.performanceForExecutionUnit = performance;
      this.availability = availability;
      this.cost = cost;
      this.numCores = numCores;
      this.location=location;
      this.provider=provider;
   }

   // NumCores not specified, assuming 1, provider location assumes null
   public CloudOffer(String name, double performance, double availability,
         double cost) {

      this(name, performance, availability, cost, 1.0,null,null);

   }

   public CloudOffer(String name) {
       this(name, 0,0,0);
   }
  

   public String getProvider() {
      return provider;
   }
   
   public void setProvider(String providerName){
      this.provider=providerName;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public double getPerformance() {
      return performanceForExecutionUnit;
   }

   public void setPerformance(double performance) {
      this.performanceForExecutionUnit = performance;
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

   @Override
   public CloudOffer clone() {
      return new CloudOffer(name, performanceForExecutionUnit, availability,
            cost, numCores, provider, location);
   }

   public double getNumCores() {
      return numCores;
   }

   public void setNumCores(double numCores) {
      this.numCores = numCores;
   }

   public void setNumCores(double numCores, boolean adjustPerformanceOfOffer) {

      if (adjustPerformanceOfOffer) {
         if(numCores==0.0){log.warn("Approaching a division by 0");}
         setPerformance(getPerformance() / numCores);
      }
      setNumCores(numCores);

   }


   @Override
   public String toString(){
      return "Offer name: " + name + " performance: " + performanceForExecutionUnit + " availability; " +
   availability + " cost: " + cost + " numCores " + numCores+ " providerName " + provider +" offerLocation " + location;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }
}
