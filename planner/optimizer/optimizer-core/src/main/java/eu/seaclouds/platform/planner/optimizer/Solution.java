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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;

public class Solution implements Iterable<String>, Comparable<Solution> {

   /*
    * Simple class that serves as a data structure to store a solution for the
    * cloud in a Map of (moduleName, CloudOptionUsed)
    */

   // "Note: this class has a natural ordering that is inconsistent with
   // equals."

   private static final double COMPARATOR_LIMIT = 1000.0;

   static Logger log = LoggerFactory.getLogger(Solution.class);

   private Map<String, String> modName_ModOption;
   private Map<String, Integer> modName_NumInstances;
   private double solutionFitness = 0.0;
   private QualityInformation solutionQuality;

   public Solution() {
      modName_ModOption = new HashMap<String, String>();
      modName_NumInstances = new HashMap<String, Integer>();
   }

   public QualityInformation getSolutionQuality() {
      return solutionQuality;
   }

   public void setSolutionQuality(QualityInformation solutionQuality) {
      this.solutionQuality = solutionQuality;
   }

   public void addItem(String name, String cloudOption) {
      addItem(name, cloudOption, 1);
   }

   public void addItem(String name, String cloudOption, int numInstances) {
      modName_ModOption.put(name, cloudOption);
      modName_NumInstances.put(name, numInstances);
   }

   public String getCloudOfferNameForModule(String key) {
      return modName_ModOption.get(key);
   }

   // returns the provider name of the selected offer of a module
   public String getCloudProviderNameForModule(String calledElementName) {
      return CloudOffer.providerNameOfCloudOffer(getCloudOfferNameForModule(calledElementName));
   }

   public int getCloudInstancesForModule(String key) {
      try {
         return modName_NumInstances.get(key);
      } catch (Exception E) {
         log.debug("Looking for ModuleName " + key
               + " unsuccessful. Forwarding the received exception. Modules to optimize their allocation are: "
               + modName_NumInstances.toString());
         throw E;
      }

   }

   public double getSolutionFitness() {
      return solutionFitness;
   }

   public void setSolutionFitness(double solutionFitness) {
      this.solutionFitness = solutionFitness;
   }

   public void modifyNumInstancesOfModule(String modulename, int newInstances) {

      if (!modName_NumInstances.containsKey(modulename)) {
         log.error("trying to modify the number of instances of a module which does not exist");
      } else {
         modName_NumInstances.put(modulename, newInstances);
      }

   }

   public void modifyCloudOfferOfModule(String modulename, CloudOffer newOffer) {
      String nameOfOffer = newOffer.getName();
      if (!modName_ModOption.containsKey(modulename)) {
         log.error("trying to modify the cloud offer of a module which does not exist");
      }
      modName_ModOption.put(modulename, nameOfOffer);

   }

   public int size() {
      return modName_ModOption.size();
   }

   public boolean containsModuleName(String modn) {
      if (modName_ModOption == null) {
         return false;
      } // Should never execute because the constructor already creates objects
      return modName_ModOption.containsKey(modn);
   }

   @Override
   public Solution clone() {

      Solution sol = new Solution();
      for (String key : this) {

         try {
            sol.addItem(key, this.getCloudOfferNameForModule(key), this.getCloudInstancesForModule(key));
         } catch (Exception E) {
            sol.addItem(key, this.getCloudOfferNameForModule(key), -1);
         }

      }

      sol.solutionFitness = this.solutionFitness;
      return sol;
   }

   @Override
   public boolean equals(Object o) {

      if (o == null) {
         return false;
      }
      if (o == this) {
         return true;
      }

      Solution s;
      try {
         s = (Solution) o;
      } catch (ClassCastException e) {
         return false;
      }

      if (this.size() != s.size()) {
         return false;
      }
      for (String modname : this) {
         try {
            if (!(s.containsModuleName(modname)
                  && s.getCloudOfferNameForModule(modname).equals(this.getCloudOfferNameForModule(modname))
                  && (s.getCloudInstancesForModule(modname) == this.getCloudInstancesForModule(modname)))) {
               return false;
            }
         } catch (Exception E) {
            //some comparison went wrong, but modname existed (given by the order of the AND)
            //Solutions are not equal (one should be consistent and the other should not). 
            return false;
         }
      }

      return true;
   }

   @Override
   public int compareTo(Solution o) {
      return (int) ((this.solutionFitness * COMPARATOR_LIMIT) - (o.solutionFitness * COMPARATOR_LIMIT));
   }

   public boolean isContainedIn(Solution[] sols) {

      for (int i = 0; i < sols.length; i++) {
         if (this.equals(sols[i])) {
            return true;
         }
      }
      return false;

   }

   // ITERATOR //Iterates over names of modules
   @Override
   public Iterator<String> iterator() {
      Iterator<String> it = new Iterator<String>() {

         private Set<Entry<String, String>> entries = modName_ModOption.entrySet();
         Iterator<Entry<String, String>> iteratorSet = entries.iterator();

         @Override
         public boolean hasNext() {
            return iteratorSet.hasNext();

         }

         @Override
         public String next() {
            return iteratorSet.next().getKey();

         }

         @Override
         public void remove() {

         }
      };
      return it;
   }

   @Override
   public String toString() {
      String out = "{";
      for (String modulename : this) {
         out += modulename + ":" + modName_ModOption.get(modulename) + "-" + modName_NumInstances.get(modulename)
               + "  ";
      }
      out += "}";
      return out;
   }
}
