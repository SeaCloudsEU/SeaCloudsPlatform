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

package eu.seaclouds.platform.planner.optimizer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YAMLmodulesOptimizerParser {

   static Logger log = LoggerFactory
                           .getLogger(YAMLmodulesOptimizerParser.class);

   @SuppressWarnings("unchecked")
   public static boolean ModuleHasModuleRequirements(Object module,
         Map<String, Object> modulesMap) {

      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;
      try {
         moduleInfo = (Map<String, Object>) module;
         if (!moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {
            return false;
         }
         moduleReqs = (Map<String, Object>) moduleInfo
               .get(TOSCAkeywords.MODULE_REQUIREMENTS);

      } catch (ClassCastException E) {
         return false;
      }

      // If requirements=1, it could be its host so check that there are at
      // least two requirements
      // It can also happen that the module has constraints.

      // if any of the module requirements has the name of potentialModuleName,
      // then there is a requirement between modules.
      for (Map.Entry<String, Object> entry : moduleReqs.entrySet()) {
         if (!entry.getKey().equals(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)) {

            try { // It may happen that the result is not a String but a
                  // Hashmap... in that case just skip the check for this entry
               if (isModuleName((String) entry.getValue(), modulesMap)) {
                  return true;
               }
            } catch (ClassCastException E) {// It wasnt a string, maybe they
                                            // were constraints
            }

         }

      }
      return false;

   }

   /**
    * @param value. The string to check whether it is a name of one of the
    *           modules.
    * @param modulesMap. A MAP of the information of all modules, being the key of the
    *           map teh module names
    * @return
    */
   public static boolean isModuleName(String value,
         Map<String, Object> modulesMap) {
      return modulesMap.containsKey(value);
   }

   @SuppressWarnings("unchecked")
   public static boolean ModuleRequirementFromTo(Object module,
         String potentialModuleName) {
      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;

      try {
         moduleInfo = (Map<String, Object>) module;

         if (!moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {
            return false;
         }

         moduleReqs = (Map<String, Object>) moduleInfo
               .get(TOSCAkeywords.MODULE_REQUIREMENTS);

      } catch (ClassCastException E) {
         return false;
      }

      // if any of the module requirements has the name of potentialModuleName,
      // then there is a requirement between modules.
      for (Map.Entry<String, Object> entry : moduleReqs.entrySet()) {
         try {
            if (((String) entry.getValue()).equals(potentialModuleName)) {
               return true;
            }
         } catch (ClassCastException E) {// It wasnt a string, maybe they were
                                         // constraints
         }
      }

      return false;
   }

   @SuppressWarnings("unchecked")
   public static boolean moduleIsHostOfOther(String moduleName,
         Map<String, Object> modulesMap) {

      for (Map.Entry<String, Object> entry : modulesMap.entrySet()) {
         Map<String, Object> moduleInfo = null;
         Map<String, Object> moduleReqs = null;

         try {

            // check it is a map
            moduleInfo = (Map<String, Object>) entry.getValue();

            // check it has requirements
            if (moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {

               moduleReqs = (Map<String, Object>) moduleInfo
                     .get(TOSCAkeywords.MODULE_REQUIREMENTS);

               // check it has host as requirements
               if (moduleReqs
                     .containsKey(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)) {

                  // check if the host requirement is equal to the searched name
                  if (moduleReqs.get(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)
                        .equals(moduleName)) {
                     return true;
                  }

               }

            }
         } catch (ClassCastException E) {
            // Nothing to do, this entry was not a module or it did not 
            // have a host. Keep looping
         }
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   public static String getMeasuredPerformanceHost(Map<String, Object> module) {
      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;
      try {
         moduleInfo = (Map<String, Object>) module;
         // Check existence of qos properties
         if (!moduleInfo.containsKey(TOSCAkeywords.MODULE_QOS_PROPERTIES)) {
            return null;
         }
         moduleReqs = (Map<String, Object>) moduleInfo
               .get(TOSCAkeywords.MODULE_QOS_PROPERTIES);

         // Check Existence of measured host
         if (moduleReqs
               .containsKey(TOSCAkeywords.MODULE_QOS_PERFORMANCE_LOCATION)) {
            return (String) moduleReqs
                  .get(TOSCAkeywords.MODULE_QOS_PERFORMANCE_LOCATION);
         }

      } catch (ClassCastException E) {
         return null;
      }
      // not found There were qos properties but not the information of teh
      // machine tested
      return null;
   }

   @SuppressWarnings("unchecked")
   public static double getMeasuredExecTimeMillis(Map<String, Object> module) {
      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;
      try {
         moduleInfo = (Map<String, Object>) module;
         // Check existence of qos properties
         if (!moduleInfo.containsKey(TOSCAkeywords.MODULE_QOS_PROPERTIES)) {
            return 0.0;
         }
         moduleReqs = (Map<String, Object>) moduleInfo
               .get(TOSCAkeywords.MODULE_QOS_PROPERTIES);

         // Check Existence of measured host
         if (moduleReqs
               .containsKey(TOSCAkeywords.MODULE_QOS_PERFORMANCE_MILLIS)) {
            return (Double) moduleReqs
                  .get(TOSCAkeywords.MODULE_QOS_PERFORMANCE_MILLIS);
         }

      } catch (ClassCastException E) {
         return 0.0;
      }
      // not found There were qos properties but not the information of teh
      // machine tested
      return 0.0;
   }

   @SuppressWarnings("unchecked")
   public static double getOpProfileWithModule(Map<String, Object> module,
         String moduleReqName) {

      Map<String, Object> moduleReqs = null;
      try {

         // Check existence of qos properties
         if (!module.containsKey(TOSCAkeywords.MODULE_QOS_PROPERTIES)) {
            return 1.0;
         }
         moduleReqs = (Map<String, Object>) module
               .get(TOSCAkeywords.MODULE_QOS_PROPERTIES);
      } catch (ClassCastException E) {
         return 1.0;
      }

      try {
         // Check Existence of measured host
         if (moduleReqs
               .containsKey(TOSCAkeywords.MODULE_QOS_OPERATIONAL_PROFILE)) {

            Map<String, Object> opprofilemodule = (Map<String, Object>) moduleReqs
                  .get(TOSCAkeywords.MODULE_QOS_OPERATIONAL_PROFILE);

            return (Double) opprofilemodule.get(moduleReqName);
         }
      } catch (ClassCastException E) {
         return 1.0;
      }

      // not found There were qos properties but not the information of teh
      // machine tested
      return 0.0;
   }

   /**
    * @param module
    * @param modules
    * @return all the modules required by this module. Its required modules are
    *         the ones that satisfy all the next conditions: its name is under
    *         the "requirements" part of the module (but not under "host" key),
    *         its name is in the general list of existing modules,
    */
   @SuppressWarnings("unchecked")
   public static List<String> ModuleRequirementsOfAModule(
         Map<String, Object> module, Map<String, Object> modules) {

      ArrayList<String> reqnameslist = new ArrayList<String>();

      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;

      try {
         moduleInfo = (Map<String, Object>) module;

         if (!moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {
            // return empty list
            return reqnameslist;
         }

         moduleReqs = (Map<String, Object>) moduleInfo
               .get(TOSCAkeywords.MODULE_REQUIREMENTS);

      } catch (ClassCastException E) {
         // return empty list
         return reqnameslist;
      }

      // if any of the module requirements has the name of potentialModuleName,
      // then there is a requirement between modules.
      for (Map.Entry<String, Object> entry : moduleReqs.entrySet()) {
         try {
            if (isModuleName((String) entry.getValue(), modules)) {// if it is a
                                                                   // module
               if (!entry.getKey().equals(
                     TOSCAkeywords.MODULE_REQUIREMENTS_HOST)) {// it isn't its
                                                               // execution host
                  reqnameslist.add((String) entry.getValue());
               }
            }
         } catch (ClassCastException E) {// It wasnt a string, maybe they were
                                         // constraints
         }
      }

      return reqnameslist;

   }

   /**
    * @param module
    * @return the name of the host of a module. It looks the requirements::host
    *         value
    */
   @SuppressWarnings("unchecked")
   public static String getHostOfModule(Map<String, Object> module) {

      Map<String, Object> moduleReqs = null;
      try {

         if (!module.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {
            return null;
         }
         moduleReqs = (Map<String, Object>) module
               .get(TOSCAkeywords.MODULE_REQUIREMENTS);

      } catch (ClassCastException E) {
         return null;
      }

      try {
         return (String) moduleReqs.get(TOSCAkeywords.MODULE_REQUIREMENTS_HOST);
      } catch (ClassCastException E) {// It wasnt a string the host information:
                                      // weird
         log.warn("Cast to String could not be performed for a host reqirement of a module");
         return null;
      }

   }

}
