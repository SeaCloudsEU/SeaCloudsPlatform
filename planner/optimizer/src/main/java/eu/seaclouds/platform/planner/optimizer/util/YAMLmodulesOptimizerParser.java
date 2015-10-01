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

import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;

//Version of September 2015
public class YAMLmodulesOptimizerParser {

   private static final boolean IS_DEBUG = false;
   static Logger                log      = LoggerFactory.getLogger(YAMLmodulesOptimizerParser.class);


   public static boolean moduleHasModuleRequirements(String moduleName, Map<String, Object> groups) {


      Map<String, Object> dependeciesInfoOfGroupOfModule = YAMLgroupsOptimizerParser
            .getDependenciesInfoOfMemberName(moduleName, groups);

      if (dependeciesInfoOfGroupOfModule == null) {
         if (IS_DEBUG) {
            log.debug("There has not been found info of dependencies for module called " + moduleName);
         }
         return false;
      }

      if (dependeciesInfoOfGroupOfModule.size() > 0) {
         return true;
      }

      if (IS_DEBUG) {
         log.debug("Module'" + moduleName + "' had dependencies but it id not contain information of other modules");
      }

      return false;

   }

   /**
    * @param value
    *           . The string to check whether it is a name of one of the
    *           modules.
    * @param modulesMap
    *           . A MAP of the information of all modules, being the key of the
    *           map teh module names
    * @return
    */
   public static boolean isModuleName(String value, Map<String, Object> modulesMap) {
      return modulesMap.containsKey(value);
   }

   /**
    * @param module
    * @param potentialModuleName
    * @return For M12 implementation TOSCA, whether a module depends on the
    *         other module (not very sure of teh from-to relationship, check
    *         it).
    */
   @SuppressWarnings("unchecked")
   public static boolean moduleRequirementFromTo(Object module, String potentialModuleName) {
      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;

      try {
         moduleInfo = (Map<String, Object>) module;

         if (!moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {
            return false;
         }

         moduleReqs = (Map<String, Object>) moduleInfo.get(TOSCAkeywords.MODULE_REQUIREMENTS);

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
   public static boolean moduleIsHostOfOther(String moduleName, Map<String, Object> modulesMap) {

      for (Map.Entry<String, Object> entry : modulesMap.entrySet()) {
         Map<String, Object> moduleInfo = null;
         Map<String, Object> moduleReqs = null;

         try {

            // check it is a map
            moduleInfo = (Map<String, Object>) entry.getValue();

            // check it has requirements
            if (moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {

               moduleReqs = (Map<String, Object>) moduleInfo.get(TOSCAkeywords.MODULE_REQUIREMENTS);

               // check it has host as requirements
               if (moduleReqs.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS_HOST)) {

                  // check if the host requirement is equal to the searched name
                  if (moduleReqs.get(TOSCAkeywords.MODULE_REQUIREMENTS_HOST).equals(moduleName)) {
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
   public static String getMeasuredPerformanceHost(String moduleName, Map<String, Object> groups) {
      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;

      Map<String, Object> qoSinfoOfGroupOfModule = YAMLgroupsOptimizerParser.getQoSinfoOfMemberName(moduleName, groups);

      if (qoSinfoOfGroupOfModule == null) {
         if (IS_DEBUG) {
            log.debug("There has not been found info of QoS for module called " + moduleName);
         }
         return null;
      }

      if (qoSinfoOfGroupOfModule.containsKey(TOSCAkeywords.GROUP_ELEMENT_QOS_BENCHMARK_PLATFORM)) {
         return (String) qoSinfoOfGroupOfModule.get(TOSCAkeywords.GROUP_ELEMENT_QOS_BENCHMARK_PLATFORM);
      }

      // not found There were qos properties but not the information of teh
      // machine tested
      if (IS_DEBUG) {
         log.debug("Module had qos info but it did not contain information of the platform where it was executed");
      }
      return null;
   }

   public static double getMeasuredExecTimeMillis(String moduleName, Map<String, Object> groups) {
      Map<String, Object> moduleInfo = null;
      Map<String, Object> moduleReqs = null;

      Map<String, Object> qoSinfoOfGroupOfModule = YAMLgroupsOptimizerParser.getQoSinfoOfMemberName(moduleName, groups);

      if (qoSinfoOfGroupOfModule == null) {
         if (IS_DEBUG) {
            log.debug("There has not been found info of QoS for module called " + moduleName);
         }
         return 0.0;
      }

      if (qoSinfoOfGroupOfModule.containsKey(TOSCAkeywords.GROUP_ELEMENT_QOS_BENCHMARK_PLATFORM)) {
         if (IS_DEBUG) {
            log.debug("Found that module " + moduleName + " takes "
                  + YAMLmodulesOptimizerParser
                        .castToDouble(qoSinfoOfGroupOfModule.get(TOSCAkeywords.GROUP_ELEMENT_QOS_EXECUTIONTIME))
                  + " milliseconds to execute in its benchmark platform");
         }
         return YAMLmodulesOptimizerParser
               .castToDouble(qoSinfoOfGroupOfModule.get(TOSCAkeywords.GROUP_ELEMENT_QOS_EXECUTIONTIME));
      }

      // not found There were qos properties but not the information of the
      // execution time
      // machine tested
      if (IS_DEBUG) {
         log.debug("Module had qos info but it did not contain information of the time it took to execute in isolation");
      }

      return 0.0;
   }

   private static double castToDouble(Object object) {
      // It creates circular dependencies between classes. Think for the next
      // version how to refactor it.
      return YAMLoptimizerParser.castToDouble(object);
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
   public static List<String> moduleRequirementsOfAModule(String moduleName, Map<String, Object> groups) {

      // It returns a list of string that is the concatenation of the modules
      // that are members of each of the groups on which
      // the group of modulename belongs

      ArrayList<String> dependentModules = new ArrayList<String>();

      List<String> dependentGroups = (List<String>) YAMLgroupsOptimizerParser.getListDependentGroupsOfModule(moduleName,
            groups);

      if ((dependentGroups == null) || (dependentGroups.size() == 0)) {
         return null;
      }

      for (String groupName : dependentGroups) {
         List<String> membersOfGroup = YAMLgroupsOptimizerParser.getAllMembersOfGroupName(groupName, groups);
         if (membersOfGroup != null) {
            dependentModules.addAll(membersOfGroup);
         }
      }

      return dependentModules;
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
         moduleReqs = (Map<String, Object>) module.get(TOSCAkeywords.MODULE_REQUIREMENTS);

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

   public static QualityInformation getQoSRequirementsOfGroup(Object group) {

      Map<String, Object> qosInformation = YAMLgroupsOptimizerParser.getQoSInformationInPolicies(group);

      if (qosInformation == null) {
         log.warn("There was not found QoS information in the policies of the group. returning null");
         return null;
      }
      return YAMLmodulesOptimizerParser.parseQoS(qosInformation);

   }

   private static double getDoubleValueFromMapValue(Map<String, Object> quality) {

      for (Map.Entry<String, Object> entry : quality.entrySet()) {
         try {
            double qualityValue = YAMLoptimizerParser.castToDouble(entry.getValue());
            return qualityValue;
         } catch (Exception E) {
            log.info("Explored quality and a Value of its Map did not contain double values");
         }

      }
      log.warn("Explored all vvalues of the Map and it was NOT found any double value. Returning -1");
      return -1;
   }

   private static QualityInformation parseQoS(Map<String, Object> qosInformation) {

      QualityInformation quality = new QualityInformation();

      // check availability
      if (qosInformation.containsKey(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_AVAILABILITY)) {
         Map<String, Object> availabilityMapValue = getMapValueFromQuality(qosInformation,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_AVAILABILITY);
         quality.setAvailability(YAMLmodulesOptimizerParser.getDoubleValueFromMapValue(availabilityMapValue));
      }

      // check performance
      if (qosInformation.containsKey(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_RESPONSETIME)) {

         Map<String, Object> responsetimeMapValue = getMapValueFromQuality(qosInformation,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_RESPONSETIME);
         quality.setResponseTimeSecs(YAMLmodulesOptimizerParser.getDoubleValueFromMapValue(responsetimeMapValue));
      }

      // check cost
      if (qosInformation.containsKey(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_COST_MONTH)) {
         Map<String, Object> costMapValue = getMapValueFromQuality(qosInformation,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_COST_MONTH);
         quality.setCostMonth(YAMLmodulesOptimizerParser.getDoubleValueFromMapValue(costMapValue));
      }

      // check whether any of them existed
      if (quality.existAvailabilityRequirement() || quality.existCostRequirement()
            || quality.existResponseTimeRequirement()) {
         return quality;
      } else {
         log.info("There was not found any quality requirement in the application");
         return null;
      }
   }

   private static Map<String, Object> getMapValueFromQuality(Map<String, Object> qosInformation, String qualityName) {
      Map<String, Object> availabilityMapValue;
      try {
         availabilityMapValue = (Map<String, Object>) qosInformation.get(qualityName);
      } catch (ClassCastException E) {
         log.warn("I could not Cast the information inside quality '" + qualityName + "' to a Map");
         return null;
      }
      return availabilityMapValue;
   }

}
