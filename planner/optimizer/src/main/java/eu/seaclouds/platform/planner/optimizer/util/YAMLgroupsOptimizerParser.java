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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YAMLgroupsOptimizerParser {

   public static final boolean IS_DEBUG = true;

   static Logger log = LoggerFactory.getLogger(YAMLgroupsOptimizerParser.class);

   public static boolean GroupHasQoSRequirements(Object group) {

      Map<String, Object> groupInfo = null;
      List<Object> groupPoliciesList = null;
      Map<String, Object> groupReqs = null;
      try {
         groupInfo = (Map<String, Object>) group;
         if (!groupInfo.containsKey(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG)) {
            return false;
         }
      } catch (ClassCastException E) {
         return false;
      }

      try {
         groupPoliciesList = (List<Object>) groupInfo.get(TOSCAkeywords.GROUP_ELEMENT_POLICY_TAG);
      } catch (ClassCastException E) {
         log.info("  No policies found here in this gorup");
         return false;
      }

      for (Object listElement : groupPoliciesList) {
         Map<String, Object> mapEntryListElement;
         try {
            mapEntryListElement = (Map<String, Object>) listElement;
         } catch (ClassCastException E) {
            log.info("  Casting of list of policies has not been successful");
            return false;
         }

         if (mapEntryListElement.containsKey(TOSCAkeywords.GROUP_ELEMENT_QOSREQUIREMENTS)) {
            // found QoS requirements in a module
            log.info("  Check successful, it receives user requests");
            return true;
         }
      }
      log.info("  Check NOT successful, it does not receive user requests");
      return false;

   }

   @SuppressWarnings({ "unchecked", "null" })
   public static String getFirstMemberName(Object group) {
      
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
         return memberList.get(0);
      }

      log.warn("  It was found a group with declaration of members but without information of who where the members");
      return null;
   }

}
