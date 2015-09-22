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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuitableOptions { // implements Iterable<List<String>

   static Logger                       log              = LoggerFactory
                                                              .getLogger(SuitableOptions.class);
   private static final double         COMPARATOR_LIMIT = 1000.0;

   private ArrayList<String>           moduleNames;
   private ArrayList<List<String>>     suitableOptionsNames;
   private ArrayList<List<CloudOffer>> suitableOptionsCharacteristics;

   private double                      latencyInternetMillis;

   private double                      latencyDatacenterMillis;

   public SuitableOptions() {

      moduleNames = new ArrayList<String>();
      suitableOptionsNames = new ArrayList<List<String>>();
      suitableOptionsCharacteristics = new ArrayList<List<CloudOffer>>();

   }

   public void addSuitableOptions(String moduleName, List<String> optionsNames,
         List<CloudOffer> OptionsCharacteristics) {

      moduleNames.add(moduleName);
      suitableOptionsNames.add(optionsNames);
      suitableOptionsCharacteristics.add(OptionsCharacteristics);
   }

   public int getSizeOfSuitableOptions(String moduleName) {

      int i = 0;
      boolean found = false;
      while ((i < moduleNames.size()) && (!found)) {
         if (moduleNames.get(i).equalsIgnoreCase(moduleName)) {
            found = true;
         } else {
            i++;
         }
      }

      if (found) {
         return suitableOptionsNames.get(i).size();
      } else {
         System.out
               .println("getSuitableOptions@SuitableOptions: Error, name of module not found: Return -1");
      }

      return -1;
   }

   @Override
   public SuitableOptions clone() {

      SuitableOptions cloned = new SuitableOptions();

      // Clone moduleName
      for (String moduleName : moduleNames) {
         cloned.moduleNames.add(moduleName);
      }

      // Clone suitableOptionsNames
      for (List<String> l : suitableOptionsNames) {

         List<String> clonedList = (List<String>) new ArrayList<String>();

         for (String option : l) {
            clonedList.add(option);
         }
         cloned.suitableOptionsNames.add(clonedList);
      }

      // Clone suitableOptionsCharacteristics
      for (List<CloudOffer> l : suitableOptionsCharacteristics) {

         List<CloudOffer> clonedList2 = (List<CloudOffer>) new ArrayList<CloudOffer>();

         for (CloudOffer option : l) {
            clonedList2.add(option.clone());
         }
         cloned.suitableOptionsCharacteristics.add(clonedList2);
      }

      cloned.setLatencyInternetMillis(latencyInternetMillis);
      cloned.setLatencyDatacenterMillis(latencyDatacenterMillis);

      return cloned;
   }

   public String getIthSuitableOptionForModuleName(String moduleName,
         int optionPosition) {

      int i = 0;
      boolean found = false;
      while ((i < moduleNames.size()) && (!found)) {
         if (moduleNames.get(i).equalsIgnoreCase(moduleName)) {
            found = true;
         } else {
            i++;
         }
      }

      if (found) {

         // if module found and there exist suitable options for it (i.e., if
         // suitableOptions.get(i).size()>0).
         if (suitableOptionsNames.get(i).size() > 0) {
            return suitableOptionsNames.get(i).get(optionPosition);
         } else {
            return null;
         }

      } else {
         System.out
               .println("getIthSuitableOptionForModuleName@SuitableOptions: Error, name of module not found: Return NULL");
      }
      return null;
   }

   // ITERATOR OVER THE ELEMENTS
   abstract class AbstractIterator<T> implements Iterable<T>, Iterator<T> {
      int currentIndex = 0;

      @Override
      public boolean hasNext() {
         return currentIndex < moduleNames.size();
      }

      @Override
      public void remove() {

      }

      @Override
      public Iterator<T> iterator() {
         return this;
      }

   }

   class ListIterator extends AbstractIterator<List<String>> {

      @Override
      public List<String> next() {
         List<String> currentList = suitableOptionsNames.get(currentIndex);
         currentIndex++;
         return currentList;

      }
   }

   class StringIterator extends AbstractIterator<String> {
      @Override
      public String next() {
         String currentModName = moduleNames.get(currentIndex);
         currentIndex++;
         return currentModName;
      }
   }

   public Iterable<List<String>> getListIterator() {

      return new ListIterator();
   }

   public Iterable<String> getStringIterator() {
      return new StringIterator();
   }

   /**
    * @param moduleName
    * @param cloudOptionNameForModule
    * @return The characteristics of cloudOptionNameForModule cloud for module
    *         with name moduleName
    */
   public CloudOffer getCloudCharacteristics(String moduleName,
         String cloudOptionNameForModule) {

      // Find index of moduleName
      boolean found = false;
      int indexModule = 0;
      while ((indexModule < moduleNames.size()) && (!found)) {
         if (moduleNames.get(indexModule).equals(moduleName)) {
            found = true;
         } else {
            indexModule++;
         }

      }
      // TODO: Merge this previous part with a getCloudOffersForModule() method.

      // if it is not found, there will probably be an error later. Now advise.
      // We cannot repair the situation here, just be aware.
      if (!found) {
         log.warn("Module in topology has not been found as module with some suitable optoins. ERROR ahead!");
      }

      found = false;
      int indexCloudOffer = 0;
      while ((indexCloudOffer < suitableOptionsCharacteristics.get(indexModule)
            .size()) && (!found)) {
         if (suitableOptionsCharacteristics.get(indexModule)
               .get(indexCloudOffer).getName().equals(cloudOptionNameForModule)) {
            found = true;
         } else {
            indexCloudOffer++;
         }
      }

      if (!found) {
         log.warn("Chosen cloud option (i.e,. "+cloudOptionNameForModule+") in solution has not been found as possibility"
               + " for the suitable options for the module ("+moduleName+"). ERROR ahead!");
      }

      return suitableOptionsCharacteristics.get(indexModule).get(
            indexCloudOffer);

   }

   public void sortDescendingPerformance() {

      for (int i = 0; i < suitableOptionsCharacteristics.size(); i++) {
         sortDescendingPerformanceOptionsForIthModule(i);
      }

   }

   private void sortDescendingPerformanceOptionsForIthModule(int listIndex) {

      List<CloudOffer> listToSort = suitableOptionsCharacteristics
            .get(listIndex);
      Collections.sort(listToSort,
            new CloudOptionReversePerformanceComparator());

      // Replace the list of names to be present in the same order as in
      // suitableoptionscharacteristics
      for (int i = 0; i < listToSort.size(); i++) {
         suitableOptionsNames.get(listIndex)
               .set(i, listToSort.get(i).getName());
      }

   }

   // ///////////////////////////
   // CLASSES FOR COMPARATOR
   // ///////////////////////////
   class CloudOptionPerformanceComparator implements Comparator<CloudOffer> {

      @Override
      public int compare(CloudOffer o1, CloudOffer o2) {
         return (int) ((o1.getPerformance() * COMPARATOR_LIMIT) - (o2
               .getPerformance() * COMPARATOR_LIMIT));
      }

   }

   class CloudOptionReversePerformanceComparator implements
         Comparator<CloudOffer> {

      @Override
      public int compare(CloudOffer o1, CloudOffer o2) {
         return (int) ((o2.getPerformance() * COMPARATOR_LIMIT) - (o1
               .getPerformance() * COMPARATOR_LIMIT));
      }

   }

   // ///////////////////////////
   // END OF CLASSES FOR COMPARATOR
   // ///////////////////////////

   /**
    * @param modulename
    * @param cloudOffer
    * @return whether exists a worse offer in terms of performance of the same
    *         provider than cloudOffer.
    * */
   public boolean existsOfferWithWorsePerformanceOfSameProvider(
         String modulename, String cloudOffer) {
      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      for (CloudOffer offer : offers) {
         // series of conditions in AND that I prefer to nest for visibility
         if (offer.getPerformance() < currentOffer.getPerformance()) {
            if (offer.getProviderName().equals(currentOffer.getProviderName())) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * @param modulename
    * @param currentCloudOffer
    * @return The offer in terms of performance of the same provider that is
    *         immediately worse than cloudOffer. It is assumed that
    *         ArraysOfcloudOffer are ordered by
    *         "CloudOptionReversePerformanceComparator"
    */
   public CloudOffer getOfferImmediateLowerPerformanceOfSameProvider(
         String modulename, String cloudOffer) {
      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      for (CloudOffer offer : offers) { // assumed that are ordered in reverse
                                        // order
         if (offer.getPerformance() < currentOffer.getPerformance()) {
            if (offer.getProviderName().equals(currentOffer.getProviderName())) {
               return offer; // The first one in an ordered traverse must be the
                             // chosen one
            }
         }
      }

      return null;
   }

   /**
    * @param modulename
    * @param currentCloudOffer
    * @return whether exists a better offer in terms of performance of the same
    *         provider than cloudOffer. It is assumed that ArraysOfcloudOffer
    *         are ordered by "CloudOptionReversePerformanceComparator"
    */
   public boolean existsOfferWithBetterPerformanceOfSameProvider(
         String modulename, String cloudOffer) {

      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      for (CloudOffer offer : offers) {
         // series of conditions in AND that I prefer to nest for visibility
         if (offer.getPerformance() > currentOffer.getPerformance()) {
            if (offer.getProviderName().equals(currentOffer.getProviderName())) {
               return true;
            }
         }
      }
      return false;

   }

   /**
    * @param modulename
    * @param currentCloudOffer
    * @return The offer in terms of performance of the same provider that is
    *         immediately better than cloudOffer. It is assumed that
    *         ArraysOfcloudOffer are ordered by
    *         "CloudOptionReversePerformanceComparator"
    */
   public CloudOffer getOfferImmediateHigherPerformanceOfSameProvider(
         String modulename, String cloudOffer) {
      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      CloudOffer potentialBetter = null;

      for (CloudOffer offer : offers) { // assumed that are ordered in reverse
                                        // order
         if (offer.getPerformance() > currentOffer.getPerformance()) {
            if (offer.getProviderName().equals(currentOffer.getProviderName())) {
               potentialBetter = offer;
            }
         } else {// not better. So the rest are not better either.
            return potentialBetter;
         }
      }

      return potentialBetter;
   }

   public boolean existsAlternativeCloudProviderForModuleWithHigherAvailability(
         String modulename, String cloudOffer) {
      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      for (CloudOffer offer : offers) {
         // series of conditions in AND that I prefer to nest for visibility
         if (offer.getAvailability() > currentOffer.getAvailability()) {
            if (!(offer.getProviderName()
                  .equals(currentOffer.getProviderName()))) {
               return true;
            }
         }
      }
      return false;
   }

   public CloudOffer getOfferImmediateHigherAvailabilityOfSameProviderSimilarPerformance(
         String modulename, String cloudOffer) {
      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      CloudOffer potentialBetter = null;

      for (CloudOffer offer : offers) { // assumed that are ordered in reverse
                                        // order
         if (offer.getAvailability() > currentOffer.getAvailability()) {
            if (!(offer.getProviderName()
                  .equals(currentOffer.getProviderName()))) {
               if (potentialBetter == null) {// there was not found any yet
                  potentialBetter = offer;
               } else {// An alternative offer was already found, now check by
                       // its performance (the less difference)
                  if (Math.abs(currentOffer.getPerformance()
                        - offer.getPerformance()) < (Math.abs(currentOffer
                        .getPerformance() - potentialBetter.getPerformance()))) {
                     // Closest difference
                     potentialBetter = offer;
                  }
               }

            }
         }
      }

      return potentialBetter;
   }

   public boolean existsAlternativeCloudProviderForModuleWithLowerAvailability(
         String modulename, String cloudOffer) {
      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      for (CloudOffer offer : offers) {
         // series of conditions in AND that I prefer to nest for visibility
         if (offer.getAvailability() < currentOffer.getAvailability()) {
            if (!(offer.getProviderName()
                  .equals(currentOffer.getProviderName()))) {
               return true;
            }
         }
      }
      return false;
   }

   public CloudOffer getOfferImmediateLowerAvailabilityOfSameProviderSimilarPerformance(
         String modulename, String cloudOffer) {
      List<CloudOffer> offers = getCloudOffersForModule(modulename);
      CloudOffer currentOffer = getCloudCharacteristicsFromList(offers,
            cloudOffer);

      CloudOffer potentialWorse = null;

      for (CloudOffer offer : offers) { // assumed that are ordered in reverse
                                        // order
         if (offer.getAvailability() < currentOffer.getAvailability()) {
            if (!(offer.getProviderName()
                  .equals(currentOffer.getProviderName()))) {
               if (potentialWorse == null) {// there was not found any yet
                  potentialWorse = offer;
               } else {// An alternative offer was already found, now check by
                       // its performance (the less difference)
                  if (Math.abs(currentOffer.getPerformance()
                        - offer.getPerformance()) < (Math.abs(currentOffer
                        .getPerformance() - potentialWorse.getPerformance()))) {
                     // Closest difference
                     potentialWorse = offer;
                  }
               }

            }
         }
      }

      return potentialWorse;
   }

   private CloudOffer getCloudCharacteristicsFromList(List<CloudOffer> offers,
         String cloudOffer) {
      boolean found = false;
      int indexCloudOffer = 0;
      while ((indexCloudOffer < offers.size()) && (!found)) {
         if (offers.get(indexCloudOffer).getName().equals(cloudOffer)) {
            found = true;
         } else {
            indexCloudOffer++;
         }
      }

      if (!found) {
         log.warn("Chosen cloud option in solution has not been found as possibility for the suitable options for the module. ERROR ahead!");
      }

      return offers.get(indexCloudOffer);
   }

   private List<CloudOffer> getCloudOffersForModule(String modulename) {
      // Find index of moduleName
      boolean found = false;
      int indexModule = 0;
      while ((indexModule < moduleNames.size()) && (!found)) {
         if (moduleNames.get(indexModule).equals(modulename)) {
            found = true;
         } else {
            indexModule++;
         }

      }

      // if it is not found, there will probably be an error later. Now advise.
      // We cannot repair the situation here, just be aware.
      if (!found) {
         log.warn("Module in topology has not been found as module with some suitable optoins. ERROR ahead!");
         return null;
      }
      return suitableOptionsCharacteristics.get(indexModule);
   }

   public double getLatencyIntraDatacenterMillis() {
      return latencyDatacenterMillis;
   }

   public double getLatencyInterCloudMillis() {
      return latencyInternetMillis;
   }

   public double getLatencyIntraDatacenterSec() {
      return getLatencyIntraDatacenterMillis() / 1000.0;
   }

   public double getLatencyInterCloudSec() {
      return getLatencyInterCloudMillis() / 1000.0;
   }

   public void setLatencyInternetMillis(double latencyInternetMillis) {
      this.latencyInternetMillis = latencyInternetMillis;
   }

   public void setLatencyDatacenterMillis(double latencyDatacenterMillis) {
      this.latencyDatacenterMillis = latencyDatacenterMillis;
   }
}
