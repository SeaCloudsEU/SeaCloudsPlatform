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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author perez
 *
 */
/**
 * @author perez
 *
 */
public class YAMLgroupsOptimizerParser {

   public static final boolean IS_DEBUG = true;

   static Logger log = LoggerFactory.getLogger(YAMLgroupsOptimizerParser.class);

   public static boolean GroupHasQoSRequirements(Object group) {

      Map<String, Object> groupInfo = null;
      List<Object> groupPoliciesList = null;
      Map<String, Object> groupReqs = null;

      try {
         groupInfo = (Map<String, Object>) group;
         groupReqs = YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(groupInfo,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS);

      } catch (ClassCastException E) {
         log.warn("Error casting the group raw object information to a Map");

         return false;
      }
      return groupReqs != null;

      /*
       * Map<String, Object> groupInfo = null; List<Object> groupPoliciesList =
       * null; Map<String, Object> groupReqs = null; try { groupInfo =
       * (Map<String, Object>) group; if
       * (!groupInfo.containsKey(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG)) {
       * return false; } } catch (ClassCastException E) { return false; }
       * 
       * try { groupPoliciesList = (List<Object>)
       * groupInfo.get(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG); } catch
       * (ClassCastException E) { log.info(
       * "  No policies found here in this gorup"); return false; }
       * 
       * for (Object listElement : groupPoliciesList) { Map<String, Object>
       * mapEntryListElement; try { mapEntryListElement = (Map<String, Object>)
       * listElement; } catch (ClassCastException E) { log.info(
       * "  Casting of list of policies has not been successful"); return false;
       * }
       * 
       * if (mapEntryListElement.containsKey(TOSCAkeywords.
       * GROUP_POLICY_QOSREQUIREMENTS)) { // found QoS requirements in a module
       * log.info("  Check successful, it has quality requirements"); return
       * true; } } log.info(
       * "  Check NOT successful, it does not receive user requests"); return
       * false;
       */

   }

   @SuppressWarnings({ "unchecked", "null" })
   public static String getFirstMemberName(Object group) {

      List<String> listNames = YAMLgroupsOptimizerParser.getListOfMemberNames(group);
      if (listNames != null) {
         return listNames.get(0);
      } else {
         log.warn("  It was not found the information of who where the members of the group");
         return null;
      }

   }

   public static Map<String, Object> getQoSinfoOfMemberName(String moduleName, Map<String, Object> groups) {
      Map<String, Object> groupInfo = YAMLgroupsOptimizerParser.findGroupOfMemberName(moduleName, groups);
      return YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(groupInfo, TOSCAkeywords.GROUP_POLICY_QOSINFO);

   }

   private static Map<String, Object> findGroupOfMemberName(String moduleName, Map<String, Object> groups) {

      for (Map.Entry<String, Object> entry : groups.entrySet()) {
         if (YAMLgroupsOptimizerParser.isMemberOfGroup(moduleName, entry.getValue())) {
            return (Map<String, Object>) entry.getValue();
         }
      }

      if (IS_DEBUG) {
         log.info("Module " + moduleName + " is not member of any group. Possible unexpected behavior");
      }
      return null;
   }

   /**
    * @param moduleName
    * @param group
    * @return whether moduleName is one of the modules that are members of
    *         'group'
    */
   private static boolean isMemberOfGroup(String moduleName, Object group) {

      return YAMLgroupsOptimizerParser.getFirstMemberName(group).equals(moduleName);

   }

   private static Map<String, Object> getPolicySubInfoFromGroupInfo(Map<String, Object> groupInfo,
         String infoToSearch) {

      List<Object> groupPoliciesList = null;

      try {
         groupPoliciesList = (List<Object>) groupInfo.get(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG);
      } catch (ClassCastException E) {
         log.info("  No policies found here in this gorup");
         return null;
      }

      for (Object listElement : groupPoliciesList) {
         Map<String, Object> mapEntryListElement;
         try {
            mapEntryListElement = (Map<String, Object>) listElement;
         } catch (ClassCastException E) {
            log.info("  Casting of list of policies has not been successful");
            return null;
         }

         if (mapEntryListElement.containsKey(infoToSearch)) {
            // found QoS requirements in a module
            log.info("  Check successful");
            return (Map<String, Object>) mapEntryListElement.get(infoToSearch);
         }
      }
      log.info("  Check for '" + infoToSearch + "' was NOT successful");
      return null;
   }

   public static Map<String, Object> getDependenciesInfoOfMemberName(String moduleName, Map<String, Object> groups) {
      Map<String, Object> groupInfo = YAMLgroupsOptimizerParser.findGroupOfMemberName(moduleName, groups);
      return YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(groupInfo,
            TOSCAkeywords.GROUP_POLICY_DEPENDENCIES);
   }

   public static List<String> getListDependentGroupsOfModule(String moduleName, Map<String, Object> groups) {

      Map<String, Object> moduleDependencies = null;

      Map<String, Object> dependeciesInfoOfGroupOfModule = YAMLgroupsOptimizerParser
            .getDependenciesInfoOfMemberName(moduleName, groups);

      if (dependeciesInfoOfGroupOfModule == null) {
         if (IS_DEBUG) {
            log.info(
                  "There has not been found info of dependencies for module called " + moduleName + " returning null");
         }
         return null;
      }

      if (dependeciesInfoOfGroupOfModule.containsKey(TOSCAkeywords.GROUP_ELEMENT_DEPENDENCIES_MODULES_TAG)) {
         return (List<String>) dependeciesInfoOfGroupOfModule.get(TOSCAkeywords.GROUP_ELEMENT_DEPENDENCIES_MODULES_TAG);

      }

      // not found There were qos properties but not the information of the
      // execution time
      // machine tested
      if (IS_DEBUG) {
         log.info("Module had dependencies but it id not contain information of other modules");
      }

      return null;
   }

   public static List<String> getAllMembersOfGroupName(String groupName, Map<String, Object> groups) {
      Map<String, Object> groupInfo = (Map<String, Object>) groups.get(groupName);
      try {

         if (groupInfo.containsKey(TOSCAkeywords.GROUP_ELEMENT_MEMBERS_TAG)) {
            return (List<String>) groupInfo.get(TOSCAkeywords.GROUP_ELEMENT_MEMBERS_TAG);
         }
      } catch (ClassCastException E) {
         log.warn("It was found a group without members. Error ahead.");
         return null;
      }

      if (IS_DEBUG) {
         log.info("It was found a group without member definitios. Error ahead.");
      }
      return null;
   }

   public static List<String> getListOfMemberNames(Object group) {
      Map<String, Object> groupInfo = null;
      List<String> memberList = null;
      try {
         groupInfo = (Map<String, Object>) group;
         if (groupInfo.containsKey(TOSCAkeywords.GROUP_ELEMENT_MEMBERS_TAG)) {
            memberList = (List<String>) groupInfo.get(TOSCAkeywords.GROUP_ELEMENT_MEMBERS_TAG);
         }
      } catch (ClassCastException E) {
         log.warn("It was found a group without members. Error ahead.");
         return null;
      }

      if (!memberList.isEmpty()) {
         if (IS_DEBUG) {
            log.info("Member of the group is: " + memberList.get(0));
         }
         return memberList;
      }

      log.warn("  It was found a group with declaration of members but without information of who where the members");
      return null;
   }

   public static double getReceivedWorkloadOfGroup(Object group) {

      Map<String, Object> qosInformation = YAMLgroupsOptimizerParser.getQoSInformationInPolicies(group);

      if (qosInformation == null) {
         log.warn("qosInformation was not found among the policies of the group");
         return -1;
      }

      if (qosInformation.containsKey(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_WORKLOAD_MINUTE)) {
         Map<String, Object> workloadInfo = null;
         try {
            workloadInfo = (Map<String, Object>) qosInformation
                  .get(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_WORKLOAD_MINUTE);
         } catch (ClassCastException E) {
            log.warn("It was not possible to cast the workload information to a map. Returning -1 . ");
            return -1;
         }

         for (Map.Entry<String, Object> entry : workloadInfo.entrySet()) {
            return YAMLoptimizerParser.castToDouble(entry.getValue());

         }
      }
      log.warn("Not possible to find workload information after exploring all the group in depth. Returning -1");
      return -1;
   }

   public static Map<String, Object> getQoSInformationInPolicies(Object group) {

      Map<String, Object> groupInfo = null;
      List<Object> groupPoliciesList = null;
      Map<String, Object> groupReqs = null;

      try {
         groupInfo = (Map<String, Object>) group;
         groupReqs = YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(groupInfo,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS);

      } catch (ClassCastException E) {
         log.warn("Error casting the group raw object information to a Map");

         return null;
      }
      return groupReqs;

   }
}
