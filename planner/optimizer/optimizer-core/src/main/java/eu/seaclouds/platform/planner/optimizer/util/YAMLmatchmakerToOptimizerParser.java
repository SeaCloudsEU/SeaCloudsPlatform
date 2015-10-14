/**
 * Copyright 2015 SeaClouds
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

package eu.seaclouds.platform.planner.optimizer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.optimizer.CloudOffer;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;

/**
 * 
 * Class that provides methods to get the information of cloud offers for each
 * module in the format agreed in September 2015
 *
 */
public class YAMLmatchmakerToOptimizerParser {

   // Reducing verbosity . If somebody knows a better approach for doing this (I
   // could not set dynamically the level of the logging) it should be changed
   private static int           BeeingTooVerboseWithLackOfInformationInCloudOffers = 3;
   private static final boolean IS_DEBUG = false;

   static Logger log = LoggerFactory.getLogger(YAMLmatchmakerToOptimizerParser.class);

   @SuppressWarnings("unchecked")
   public static List<Object> getListofOptions(String appModel) {
      Yaml yamlApp = new Yaml();
      if(IS_DEBUG){
      log.debug("Loading String to a YAML using sknakeyaml.Yaml");
      }
      return (List<Object>) yamlApp.load(appModel);
   } 

   public static String fromListtoYAMLstring(List<Object> appMap) {
      DumperOptions options = new DumperOptions();
      options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());

      Yaml yamlApp = new Yaml(options);

      return yamlApp.dump(appMap);

   }

   /**
    * @param appModel
    * @param suitableCloudOffers
    * @return The structure of suitable cloud options for each module (order not
    *         guaranteed with topology modules). Method just left with these
    *         parameters to keep compatibility. It just calls its homonymous
    *         with suitableCloudOffers.
    */
   public static SuitableOptions getSuitableCloudOptionsAndCharacteristicsForModules(String appModel,
         String suitableCloudOffers) {
      return YAMLmatchmakerToOptimizerParser.getSuitableCloudOptionsAndCharacteristicsForModules(suitableCloudOffers);
   }

   @SuppressWarnings("unchecked")
   private static SuitableOptions getSuitableCloudOptionsAndCharacteristicsForModules(String suitableCloudOffers) {

      List<Object> listCompleteOffers = YAMLmatchmakerToOptimizerParser.getListofOptions(suitableCloudOffers);

      SuitableOptions options = new SuitableOptions();

      // FOR EACH OF THE APP MODULES
      for (Object moduleObject : listCompleteOffers) {

         // There is a map of one element.
         Map<String, Object> module = (Map<String, Object>) moduleObject;

         for (Map.Entry<String, Object> moduleEntry : module.entrySet()) {
            // the key is the name of the module
            // the value is a list of suitable options
            try{
               //This method relies on the fact that a concrete map is always iterated in the same order (whathever it was)
               if(IS_DEBUG){
                  log.debug("exploring the options for module" + moduleEntry.getKey());
               }
               List<String> listOfferNames =  YAMLmatchmakerToOptimizerParser.getNamesOfSuitableOptions((List<Map<String,Object>>)moduleEntry.getValue());
               options.addSuitableOptions(moduleEntry.getKey(), listOfferNames,
                  YAMLmatchmakerToOptimizerParser.getSuitableOffersOfModule((List<Map<String,Object>>)moduleEntry.getValue(),listOfferNames));
            
            if (IS_DEBUG) {
               log.debug("Found module in suitable options '" + moduleEntry.getKey() + "' whose cloud options are: "
                     + YAMLmatchmakerToOptimizerParser.getStringOfAmodule(options, moduleEntry.getKey()));
            }
            }catch(ClassCastException e){
               log.warn("Problems casting the entry of suitable offers of a module");
            }catch(Exception e){
               log.warn("Problems reading the entry of suitable offers of a module, NOT in the casting");
            }
            
         }
      }
      return options;
   }

   private static List<CloudOffer> getSuitableOffersOfModule(List<Map<String, Object>> listOptions, List<String> listOfferNames) {
      ArrayList<CloudOffer> listOffers = new ArrayList<CloudOffer>();
      
      for(String offerName : listOfferNames){
         CloudOffer offer = getAllCharacteristicsOfCloudOffer(offerName, listOptions);
         if(offer!=null){
            if(IS_DEBUG){
            log.debug("Found cloud offer with info:" +offer.toString() );
            }
            listOffers.add(offer);
         }
         else{
            log.warn("not found information for cloud offer name '"+offerName+"'");
         }
      }
      return listOffers;
      
      
   }

   private static List<String> getNamesOfSuitableOptions(List<Map<String,Object>> listOptions) {
      ArrayList<String> listNamesOffers = new ArrayList<String>();
      
      for(Map<String,Object> offer : listOptions){
         //This map has only one entry
         
         for(Map.Entry<String, Object> offerEntry : offer.entrySet()){
            listNamesOffers.add(offerEntry.getKey());
            if(IS_DEBUG){
               log.debug("Found option called " + offerEntry.getKey());
            }
         }
      }
      return listNamesOffers;
   }

   private static String getStringOfAmodule(SuitableOptions options, String moduleName) {
      String out="[";
      int numberOptions = options.getSizeOfSuitableOptions(moduleName);
      for(int i=0; i<numberOptions; i++){
         out+=options.getIthSuitableOptionForModuleName(moduleName, i)+"; ";
      }
      return out+"]";
      
   }





@SuppressWarnings("unchecked")
private static CloudOffer getAllCharacteristicsOfCloudOffer(String offerName,
            List<Map<String, Object>> listOptions) {

   //It does not contain the cloudOfferName
   Map<String,Object> cloudOfferInfoMap = getCloudOfferInfoMapFromCloudOfferListByName(listOptions,offerName);

      CloudOffer offer;
   
      try {
         offer = new CloudOffer(offerName);

         offer.setAvailability(getPropertyOfCloudOffer(TOSCAkeywords.CLOUD_CONCRETE_OFFER_AVAILABILITY,
               cloudOfferInfoMap));

         offer.setPerformance(getPropertyOfCloudOffer(TOSCAkeywords.CLOUD_CONCRETE_OFFER_PERFORMANCE,
               cloudOfferInfoMap));

         offer.setCost(getPropertyOfCloudOffer(TOSCAkeywords.CLOUD_CONCRETE_OFFER_COST,cloudOfferInfoMap));

         double numcores=getPropertyOfCloudOffer(TOSCAkeywords.CLOUD_CONCRETE_OFFER_NUM_CORES,cloudOfferInfoMap);
         if(numcores==0){
            numcores=1;
            log.warn("not found number of cores for offer '"+offerName+"' . Assuming single core.");
            }
         offer.setNumCores(numcores, true);
      } catch (NullPointerException E) {
         return null;
      }

      return offer;
   }

   private static Map<String, Object> getCloudOfferInfoMapFromCloudOfferListByName(List<Map<String, Object>> listOptions,
      String offerName) {
      
      for(Map<String,Object> option : listOptions){
         //There should be only one of this in eah option
         for(Map.Entry<String, Object> optionEntry : option.entrySet()){
          if(optionEntry.getKey().equals(offerName)){
             //it is the correct offer
             return (Map<String,Object>) optionEntry.getValue();
          }

         }
      }
   return null;
}

   private static double getPropertyOfCloudOffer(String cloudOfferProperty, Map<String, Object> singleOfferMap) {

      Map<String, Object> propertiesOfOffer = (Map<String, Object>) singleOfferMap
            .get(TOSCAkeywords.CLOUD_OFFER_PROPERTIES_TAG);

      double valueOfProperty = 0.0;
 
      if (propertiesOfOffer.containsKey(cloudOfferProperty)) {
         // If there is an error here, treat the value returned in the Map as
         // List<String> instead of as String; i.e., add a .get(0)
         valueOfProperty = YAMLoptimizerParser.castToDouble(propertiesOfOffer.get(cloudOfferProperty));

      } else {
         // Many times it will not exist the value and it will return 0
         // Try to make theo output less verbose
         if (BeeingTooVerboseWithLackOfInformationInCloudOffers > 0) {
            log.info("Property " + cloudOfferProperty + " not found. REAL SOLUTION CANNOT BE COMPUTED in case that "
                  + cloudOfferProperty + " requirement existed in the system");
            BeeingTooVerboseWithLackOfInformationInCloudOffers--;
         }

      }

      return valueOfProperty;

   }
   
 
   
}
