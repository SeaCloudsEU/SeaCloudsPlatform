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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.seaclouds.platform.planner.optimizer.Solution;

//Version of September 2015
public class YAMLgroupsOptimizerParser {

   public static final boolean IS_DEBUG = false;

   static Logger log = LoggerFactory.getLogger(YAMLgroupsOptimizerParser.class);

   public static boolean groupHasQoSRequirements(Object group) {

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

   public static Map<String, Object> findGroupOfMemberName(String moduleName, Map<String, Object> groups) {

      for (Map.Entry<String, Object> entry : groups.entrySet()) {
         if (YAMLgroupsOptimizerParser.isMemberOfGroup(moduleName, entry.getValue())) {
            return (Map<String, Object>) entry.getValue();
         }
      }

      if (IS_DEBUG) {
         log.debug("Module " + moduleName + " is not member of any group. Possible unexpected behavior");
      }
      return null;
   }

   private static String findGroupNameOfMemberName(String moduleName, Map<String, Object> groups) {

      for (Map.Entry<String, Object> entry : groups.entrySet()) {
         if (YAMLgroupsOptimizerParser.isMemberOfGroup(moduleName, entry.getValue())) {
            return entry.getKey();
         }
      }

      if (IS_DEBUG) {
         log.debug("Module " + moduleName
               + " is not member of any group. Found out while looking for the name of its group. Possible unexpected behavior.");
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
            if (IS_DEBUG) {
               log.debug("  Check successful");
            }
            return (Map<String, Object>) mapEntryListElement.get(infoToSearch);
         }
      }
      if (IS_DEBUG) {
         log.debug("  Check for '" + infoToSearch + "' was NOT successful");
      }
      return null;
   }

   public static Map<String, Object> getDependenciesInfoOfMemberName(String moduleName, Map<String, Object> groups) {
      Map<String, Object> groupInfo = YAMLgroupsOptimizerParser.findGroupOfMemberName(moduleName, groups);
      return YAMLgroupsOptimizerParser.getPolicySubInfoFromGroupInfo(groupInfo,
            TOSCAkeywords.GROUP_POLICY_DEPENDENCIES);
   }

   public static List<String> getListDependentGroupsOfModule(String moduleName, Map<String, Object> groups) {

      Map<String, Object> moduleDependencies = null;

      Map<String, Object> dependenciesInfoOfGroupOfModule = YAMLgroupsOptimizerParser
            .getDependenciesInfoOfMemberName(moduleName, groups);

      if (dependenciesInfoOfGroupOfModule == null) {
         if (IS_DEBUG) {
            log.debug(
                  "There has not been found info of dependencies for module called " + moduleName + " returning null");
         }
         return null;
      }
      if (dependenciesInfoOfGroupOfModule.size() == 0) {
         if (IS_DEBUG) {
            log.debug(
                  "There has been found EMPY info of dependencies for module called " + moduleName + " returning null");
         }
         return null;
      }

      List<String> dependencesGroupNames = new ArrayList<String>();
      for (Map.Entry<String, Object> dependency : dependenciesInfoOfGroupOfModule.entrySet()) {
         dependencesGroupNames.add(dependency.getKey());
      }
      if (dependencesGroupNames.size() > 0) {
         return dependencesGroupNames;
      }
      // not found There were qos properties but not the information of the
      // execution time
      // machine tested
      if (IS_DEBUG) {
         log.debug("Module '" + moduleName + "' had dependencies but it id not contain information of other modules");
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
         log.debug("It was found a group without member definitios. Error ahead.");
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
            log.debug("Member of the group is: " + memberList.get(0));
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

   @SuppressWarnings("unchecked")
   public static double getOpProfileWithModule(String moduleName, String moduleReqName, Map<String, Object> groups) {

      Map<String, Object> dependenciesInfoOfModule = getDependenciesInfoOfMemberName(moduleName, groups);
      String groupNameOfRequestedModule = findGroupNameOfMemberName(moduleReqName, groups);

      if (dependenciesInfoOfModule != null && dependenciesInfoOfModule.size() > 0) {
         if (dependenciesInfoOfModule.containsKey(groupNameOfRequestedModule)) {
            return YAMLoptimizerParser.castToDouble(dependenciesInfoOfModule.get(groupNameOfRequestedModule));
         } else {
            log.warn("Found dependences of module '" + moduleName + "' but '" + moduleReqName
                  + "' was not among them. Returning 0 ");
            return 0;
         }
      }

      log.warn("Not found the Op profile of the dependences between modules'" + moduleName + "' and'" + moduleReqName
            + "' because there WAS NOT " + "ANY dependency for the first one. Returning 1");

      return 1.0;

   }

   /**
    * @param sol
    * @param initialElementName
    * @param groups
    *           It adds to the groups where "moduleName" is member an entry in
    *           its policies with the expected quality of the solution
    */
   public static void addQualityOfSolutionToGroup(Solution sol, String initialElementName, Map<String, Object> groups) {

      HashMap<String, Object> expectedQuality = createHashmapOfExpectedQuality(sol);

      Map<String, Object> groupInfo = YAMLgroupsOptimizerParser.findGroupOfMemberName(initialElementName, groups);

      List<Object> policies = null;
      if (groupInfo.containsKey(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG)) {
         policies = (List<Object>) groupInfo.get(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG);
      } else {
         policies = new ArrayList<Object>();
         groupInfo.put(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG, policies);
      }

      policies.add(groupInfo);

   }

   private static HashMap<String, Object> createHashmapOfExpectedQuality(Solution sol) {
      HashMap<String, Double> qosPropsMap = new HashMap<String, Double>();

      if (sol.getSolutionQuality() == null) {
         log.warn("quality Of Solution Not Found for solution: " + sol.toString());
      }
      try {
         if (sol.getSolutionQuality().existAvailabilityRequirement()) {
            qosPropsMap.put(TOSCAkeywords.EXPECTED_QOS_AVAILABILITY, sol.getSolutionQuality().getAvailability());

         }
      } catch (Exception E) {
         log.warn("Availability not found for solutiot: " + sol.toString());
      }

      if (sol.getSolutionQuality().existCostRequirement()) {
         qosPropsMap.put(TOSCAkeywords.EXPECTED_QOS_COST_MONTH, sol.getSolutionQuality().getCostMonth());
      }

      if (sol.getSolutionQuality().existResponseTimeRequirement()) {
         qosPropsMap.put(TOSCAkeywords.EXPECTED_QOS_PERFORMANCE_SEC, sol.getSolutionQuality().getResponseTime());

      }

      qosPropsMap.put(TOSCAkeywords.OVERALL_QOS_FITNESS, sol.getSolutionFitness());

      HashMap<String, Object> expectedQuality = new HashMap<String, Object>();
      expectedQuality.put(TOSCAkeywords.EXPECTED_QUALITY_PROPERTIES, qosPropsMap);
      return expectedQuality;
   }
}
