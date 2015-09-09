package eu.seaclouds.platform.planner.optimizer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.optimizer.nfp.MonitoringConditions;


public class YAMLMonitorParser {

   private static final boolean IS_DEBUG                                           = true;
   static Logger log = LoggerFactory.getLogger(YAMLMonitorParser.class);
   
   
   /**
    * @param aam
    * @return  the monitoring conditions, which are the QoS requirements of all modules
    */
   public static List<MonitoringConditions> getModuleConditions(String aam) {
      
      // Get aam into a map 
      Map<String, Object> appMap = YAMLMonitorParser.GetMAPofAPP(aam);
      return YAMLMonitorParser.getModuleConditionsFromMap(appMap); 
   }
   
   @SuppressWarnings("unchecked")
   private static List<MonitoringConditions> getModuleConditionsFromMap(Map<String, Object> appMap) {

         Map<String, Object> groupsMap = YAMLMonitorParser.getGroupMapFromAppMap(appMap);
         List<MonitoringConditions> qualityOfModules = new ArrayList<MonitoringConditions>();

         // FOR EACH OF THE APP MODULES (but in this level there are more concepts
         // than only the modules)
         for (Map.Entry<String, Object> entry : groupsMap.entrySet()) {
            String potentialGroupName = entry.getKey();

            if (IS_DEBUG) {
               log.info("checking if group '" + potentialGroupName + "' is an element which has quality requirements");
            }
            // If it has requirements but nobody requires it..
            if (YAMLMonitorParser.GroupHasQoSRequirements(entry.getValue())) {
               // there are requirements.
               // Now, 1) for each of the get the name of modules that are members
               // of the group.
               // Since it should be only one member in the group in as list
               // we get the first module name found
               for (String moduleName : YAMLMonitorParser.getListOfMemberNames(entry.getValue())) {
                  MonitoringConditions qosInfoOfGroup = YAMLMonitorParser
                        .getQoSRequirementsOfGroup(entry.getValue());
                  if (qosInfoOfGroup != null) {
                     qualityOfModules.add(qosInfoOfGroup);
                  }

               }
            }
         }

         // 2) return the first element of this list
         if (qualityOfModules.size() > 0) {
            return qualityOfModules;
         }

         log.warn("List of quality requirements was empty. Not requirements found. Returning null");
         return null;

      
   }

   /**
    * @param appModel
    * @return a Map that has loaded the String passed as argument if it complains with YAML grammar description
    */
   @SuppressWarnings("unchecked")
   private static Map<String, Object> GetMAPofAPP(String appModel) {
      Yaml yamlApp = new Yaml();
      return (Map<String, Object>) yamlApp.load(appModel);
   }

   
   
   private static Map<String, Object> getGroupMapFromAppMap(Map<String, Object> appMap) {
      Map<String, Object> groupsMap;
      try {
         if (IS_DEBUG) {
            log.info("Opening TOSCA for obtaining groups");
         }
         groupsMap = (Map<String, Object>) appMap.get(TOSCAkeywords.GROUP_ELEMENT_TAG);

      } catch (NullPointerException E) {
         log.error("It was not found '" + TOSCAkeywords.GROUP_ELEMENT_TAG
               + "' . Cannot be unveiled the dependencies between modules calls");
         return null;
      }
      return groupsMap;
   }
   
   private static boolean GroupHasQoSRequirements(Object group) {

      Map<String, Object> groupInfo = null;
      List<Object> groupPoliciesList = null;
      Map<String, Object> groupReqs = null;
      
      
      try {
         groupInfo = (Map<String, Object>) group;
         groupReqs = YAMLMonitorParser.getPolicySubInfoFromGroupInfo(groupInfo, TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS);
         
         
      } catch (ClassCastException E) {
         log.warn("Error casting the group raw object information to a Map");
         
         return false;
      }
      return groupReqs!=null;
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
   
   private static List<String> getListOfMemberNames(Object group) {
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
   
   private static MonitoringConditions getQoSRequirementsOfGroup(Object group) {

      Map<String, Object> qosInformation = YAMLgroupsOptimizerParser.getQoSInformationInPolicies(group);

      if (qosInformation == null) {
         log.warn("There was not found QoS information in the policies of the group. returning null");
         return null;
      }
      return YAMLMonitorParser.parseQoS(qosInformation);

   }
   
   private static MonitoringConditions parseQoS(Map<String, Object> qosInformation) {

      MonitoringConditions quality = new MonitoringConditions();

      // check availability
      if (qosInformation.containsKey(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_AVAILABILITY)) {
         Map<String, Object> availabilityMapValue = YAMLMonitorParser.getMapValueFromQuality(qosInformation,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_AVAILABILITY);
         quality.setAvailability(YAMLMonitorParser.getDoubleValueFromMapValue(availabilityMapValue)); 
      }

      // check performance
      if (qosInformation.containsKey(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_RESPONSETIME)) {
         
         Map<String, Object> responsetimeMapValue = YAMLMonitorParser.getMapValueFromQuality(qosInformation,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_RESPONSETIME);
         quality.setResponseTimeSecs(YAMLMonitorParser.getDoubleValueFromMapValue(responsetimeMapValue));
      }

      // check cost
      if (qosInformation.containsKey(TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_COST_MONTH)) {
         Map<String, Object> costMapValue = YAMLMonitorParser.getMapValueFromQuality(qosInformation,
               TOSCAkeywords.GROUP_POLICY_QOSREQUIREMENTS_COST_MONTH);
         quality.setCostMonth(YAMLMonitorParser.getDoubleValueFromMapValue(costMapValue));
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
   
}
