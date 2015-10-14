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

//Version of July 2015
package eu.seaclouds.platform.planner.optimizer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import eu.seaclouds.platform.planner.optimizer.CloudOffer;
import eu.seaclouds.platform.planner.optimizer.Solution;
import eu.seaclouds.platform.planner.optimizer.SuitableOptions;
import eu.seaclouds.platform.planner.optimizer.Topology;
import eu.seaclouds.platform.planner.optimizer.TopologyElement;
import eu.seaclouds.platform.planner.optimizer.TopologyElementCalled;
import eu.seaclouds.platform.planner.optimizer.nfp.QualityInformation;

//Version of September 2015
public class YAMLoptimizerParser {

   private static final boolean IS_DEBUG = false;

   static Logger log = LoggerFactory.getLogger(YAMLoptimizerParser.class);

   public static void cleanSuitableOfferForModule(String modulename, Map<String, Object> appMap) {

      List<String> suitableOptions = getListOfSuitableOptionsForModule(modulename, appMap);

      if (suitableOptions == null) {
         log.warn("Module name " + modulename + " not found in the model");
         return;

      }

      if (IS_DEBUG) {
         log.debug("Found list of suitable services: " + suitableOptions.toString() + " Removing it");
      }

      while (!suitableOptions.isEmpty()) {
         suitableOptions.remove(0);
      }

   }

   private static List<String> getListOfSuitableOptionsForModule(String modulename, Map<String, Object> appMap) {

      // FOR EACH OF THE APP MODULES (but in this level there are more concepts
      // than only the modules)
      for (Map.Entry<String, Object> entry : appMap.entrySet()) {

         List<String> options = findSuitableOptionsForEntry(modulename, entry);
         if (options != null) {
            return options;
         }
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   private static List<String> findSuitableOptionsForEntry(String modulename, Map.Entry<String, Object> entry) {

      // If module found, go deeper to find for its suitable Options
      if (modulename.equals(entry.getKey())) {
         if (IS_DEBUG) {
            log.debug("Found module " + modulename + " cleaning the potential options");
         }
         return getListOfSuitableOptionsForAlreadyFoundModule(entry.getValue());
      }

      // If module has not been found, look in depth for the module name (only
      // useful in case that the YAML changes the hierarchy and module names
      // come deeper in the tree)
      else {

         // If follow the try/catch because I like more than the "instanceof"
         // way.
         // Trying to avoid the utilization of "instanceof"...

         Map<String, Object> moduleInfo = null;
         try {
            moduleInfo = (Map<String, Object>) entry.getValue();
         } catch (ClassCastException E) {
            return null;
         }

         List<String> options = getListOfSuitableOptionsForModule(modulename, moduleInfo);
         if (options != null) {
            return options;
         }
      }
      return null;

   }

   private static List<String> getListOfSuitableOptionsForAlreadyFoundModule(Object appSubMap) {
      // TODO: Implement this with the new information of the matchmaker
      // regarding the matching options found.
      // Probably the input to this method should not be appSubMap but a string
      // with the module name and the object
      // that contains the suitable optios of all modules
      return null;

   }

   @SuppressWarnings("unchecked")
   public static void addQualityOfSolution(Solution sol, Map<String, Object> applicationMapComplete) {

      // 1 get name of initial element. 2) getGroupOfinitial element, 3) get its
      // policies, 4) write the info.

      String initialElementName = getInitialElementName(applicationMapComplete);
      Map<String, Object> groups = YAMLoptimizerParser.getGroupMapFromAppMap(applicationMapComplete);
      YAMLgroupsOptimizerParser.addQualityOfSolutionToGroup(sol, initialElementName, groups);

   }

   private static double getCloudLatency(String suitableCloudOffers, String latencyKeyword) {

      Map<String, Object> cloudOffersMap = getMAPofAPP(suitableCloudOffers);
      try {
         @SuppressWarnings("unchecked")
         Map<String, Object> cloudMap = (Map<String, Object>) cloudOffersMap.get(TOSCAkeywords.NODE_TEMPLATE);
         if (cloudMap.containsKey(latencyKeyword)) {

            return (Double) cloudMap.get(latencyKeyword);
         }
      } catch (ClassCastException E) {
         return 0.0;
      }

      return 0.0;
   }

   public static double castToDouble(Object object) {
      double result = -1.0;
      boolean success = false;

      if (!success) {
         try {
            result = (Double) object;
            success = true;
         } catch (ClassCastException E) {
            // nothing to do, it was not double
         }
      }

      if (!success) {
         try {
            result = ((Integer) object).doubleValue();
            success = true;
         } catch (ClassCastException E) {
            // nothing to do, it was not Integer
         }
      }

      if (!success) {
         try {
            Pattern p = Pattern.compile("\\d+.?\\d*");
            Matcher m = p.matcher((String) object);
            m.find();
            result = Double.parseDouble(m.group());
            success = true;
         } catch (ClassCastException E) {
            // nothing to do, it was not String
         }
      }

      return result;

   }

   public static void addSuitableOfferForModule(String moduleName, String solutionName, int instances,
         Map<String, Object> applicationMap) {

      Map<String, Object> hostInfo = setUpHostInfoForModule(moduleName, applicationMap);
      if (IS_DEBUG) {
         log.debug(
               "Adding selected offer " + solutionName + " to module " + moduleName + " and instances " + instances);
      }
      hostInfo.put(TOSCAkeywords.MODULE_REQUIREMENTS_HOST, solutionName);
      hostInfo.put(TOSCAkeywords.MODULE_PROPOSED_INSTANCES, instances);

   }

   private static Map<String, Object> setUpHostInfoForModule(String moduleName, Map<String, Object> applicationMap) {
      Map<String, Object> templates = null;
      try {
         if (IS_DEBUG) {
            log.debug("Opening TOSCA for obtaining modules");
         }
         applicationMap = (Map<String, Object>) applicationMap.get(TOSCAkeywords.TOPOLOGY_TEMPLATE);
         templates = (Map<String, Object>) applicationMap.get(TOSCAkeywords.NODE_TEMPLATE);
      } catch (NullPointerException E) {
         log.error("It was not found '" + TOSCAkeywords.TOPOLOGY_TEMPLATE + "' or '" + TOSCAkeywords.NODE_TEMPLATE
               + "' in the TOSCA model");
         return null;
      }

      if (!templates.containsKey(moduleName)) {
         log.warn("Module name '" + moduleName + "' not found for setting its host");
         return null;
      }
      Map<String, Object> moduleInfo = (Map<String, Object>) templates.get(moduleName);

      HashMap<String, Object> hostReqs = null;
      if (moduleInfo.containsKey(TOSCAkeywords.MODULE_REQUIREMENTS)) {
         List<Object> requirementsInfo = (List<Object>) moduleInfo.get(TOSCAkeywords.MODULE_REQUIREMENTS);
         hostReqs = new HashMap<String, Object>();
         requirementsInfo.add(hostReqs);
      } else {// Module did not contain "requirements". Create the structure for
              // adding them: Map+List+Map
         List<Object> listReqs = new ArrayList<Object>();
         hostReqs = new HashMap<String, Object>();
         listReqs.add(hostReqs);
         moduleInfo.put(TOSCAkeywords.MODULE_REQUIREMENTS, listReqs);

      }
      return hostReqs;

   }

   /**
    * @param appModel
    * @return a Map with the information of the String aapModel if it represents
    *         a valid YAML document
    */
   @SuppressWarnings("unchecked")
   public static Map<String, Object> getMAPofAPP(String appModel) {
      Yaml yamlApp = new Yaml();
      return (Map<String, Object>) yamlApp.load(appModel);
   }

   public static Map<String, Object> getMAPofCloudOffers(String cloudOfferString) {
      Yaml yamlApp = new Yaml();
      @SuppressWarnings("unchecked")
      Map<String, Object> cloudOfferFileMap = (Map<String, Object>) yamlApp.load(cloudOfferString);
      if (!cloudOfferFileMap.containsKey(TOSCAkeywords.NODE_TEMPLATE)) {
         log.warn(
               "YAML with Cloud offers information malformed (not found template). Expecting errors in the execution");
         return null;
      }

      Map<String, Object> cloudOffers = null;

      try {
         cloudOffers = (Map<String, Object>) cloudOfferFileMap.get(TOSCAkeywords.NODE_TEMPLATE);
      } catch (ClassCastException E) {
         log.warn(
               "YAML with Cloud offers information malformed (mapping of information of cloud offers). Expecting errors in the execution");
         cloudOffers = null;
      }
      return cloudOffers;
   }

   public static String fromMAPtoYAMLstring(Map<String, Object> appMap) {
      DumperOptions options = new DumperOptions();
      options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());

      Yaml yamlApp = new Yaml(options);

      return yamlApp.dump(appMap);
   }

   @SuppressWarnings("unchecked")
   public static void replaceSuitableServiceByHost(Map<String, Object> appMap) {

      Map<String, Object> templates;
      try {
         if (IS_DEBUG) {
            log.debug("Opening TOSCA for obtaining modules");
         }
         appMap = (Map<String, Object>) appMap.get(TOSCAkeywords.TOPOLOGY_TEMPLATE);
         templates = (Map<String, Object>) appMap.get(TOSCAkeywords.NODE_TEMPLATE);
      } catch (NullPointerException E) {
         log.error("It was not found '" + TOSCAkeywords.TOPOLOGY_TEMPLATE + "' or '" + TOSCAkeywords.NODE_TEMPLATE
               + "' in the TOSCA model");
         return;
      }

      // FOR EACH OF THE APP MODULES (again, in this level there are more
      // concepts than only the modules)
      for (Map.Entry<String, Object> entry : templates.entrySet()) {

         if (containsSingleSuitableServiceAndInstances(entry)) {
            List<String> suitableOptions = findSuitableOptionsForEntry(entry.getKey(), entry);
            String suitableService = suitableOptions.get(0);

            int numInstances = Integer.valueOf(suitableOptions.get(1)).intValue();

            Map<String, Object> moduleInfo = (Map<String, Object>) entry.getValue();
            Map<String, Object> moduleRequirements = (Map<String, Object>) moduleInfo
                  .get(TOSCAkeywords.MODULE_REQUIREMENTS);
            moduleRequirements.remove(TOSCAkeywords.MODULE_REQUIREMENTS_CONSTRAINTS);
            moduleRequirements.put(TOSCAkeywords.MODULE_REQUIREMENTS_HOST, suitableService);
            moduleRequirements.put(TOSCAkeywords.MODULE_PROPOSED_INSTANCES, numInstances);
         }

      }
   }

   private static boolean containsSingleSuitableServiceAndInstances(Entry<String, Object> module) {
      List<String> suitableService = findSuitableOptionsForEntry(module.getKey(), module);
      if (suitableService != null) {
         if (suitableService.size() == 2) {
            try {
               if (Integer.valueOf(suitableService.get(1)).intValue() > 0) {
                  return true;
               }
            } catch (NumberFormatException e) {
               return false;
            }

         }
      }
      return false;

   }


   public static QualityInformation getQualityRequirements(Map<String, Object> applicationMap) {

      Map<String, Object> groupsMap = YAMLoptimizerParser.getGroupMapFromAppMap(applicationMap);
      List<QualityInformation> qualityOfModules = new ArrayList<QualityInformation>();

      // FOR EACH OF THE APP MODULES (but in this level there are more concepts
      // than only the modules)
      for (Map.Entry<String, Object> entry : groupsMap.entrySet()) {
         String potentialGroupName = entry.getKey();

         if (IS_DEBUG) {
            log.debug("checking if group '" + potentialGroupName + "' is an element which has quality requirements");
         }
         // If it has requirements but nobody requires it..
         if (YAMLgroupsOptimizerParser.groupHasQoSRequirements(entry.getValue())) {
            // there are requirements.
            // Now, 1) for each of the get the name of modules that are members
            // of the group.
            // Since it should be only one member in the group in as list
            // we get the first module name found
            for (String moduleName : YAMLgroupsOptimizerParser.getListOfMemberNames(entry.getValue())) {
               QualityInformation qosInfoOfGroup = YAMLmodulesOptimizerParser
                     .getQoSRequirementsOfGroup(entry.getValue());
               if (qosInfoOfGroup != null) {
                  qualityOfModules.add(qosInfoOfGroup);
               }

            }
         }
      }

      // 2) return the first element of this list
      if (qualityOfModules.size() > 0) {
         return qualityOfModules.get(0);
      }

      log.warn("List of quality requirements was empty. Not requirements found. Returning null");
      return null;

   }

 
   public static double getApplicationWorkload(Map<String, Object> applicationMap) {

      // The initial element is such one that has QoSrequirements in the
      // "groups" part.

      Map<String, Object> groupsMap = YAMLoptimizerParser.getGroupMapFromAppMap(applicationMap);

      // FOR EACH OF THE APP MODULES (but in this level there are more concepts
      // than only the modules)
      for (Map.Entry<String, Object> entry : groupsMap.entrySet()) {
         String potentialGroupName = entry.getKey();

         if (IS_DEBUG) {
            log.debug("checking if group '" + potentialGroupName + "' is an element which receceives user requests");
         }
         // If it has requirements but nobody requires it..
         if (YAMLgroupsOptimizerParser.groupHasQoSRequirements(entry.getValue())) {
            // group found.
            return YAMLgroupsOptimizerParser.getReceivedWorkloadOfGroup(entry.getValue());

         }
      }

      log.warn("not found workload information in any group. Returning -1.");
      return -1;

   }

   @SuppressWarnings("unchecked")
   public static Topology getApplicationTopology(Map<String, Object> appMap, SuitableOptions appInfoSuitableOptions) {

      Map.Entry<String, Object> initialElement = getInitialElement(appMap);

      Topology topology = new Topology();

      // gets the topology of the connected graph to element passed as argument.
      topology = getApplicationTopologyRecursive(initialElement.getKey(),
            (Map<String, Object>) initialElement.getValue(), topology,
            YAMLoptimizerParser.getModuleMapFromAppMap(appMap), YAMLoptimizerParser.getGroupMapFromAppMap(appMap),
            appInfoSuitableOptions);
      if (IS_DEBUG) {
         log.debug("Reading topology. Next step: replace the module name by the name of the host");
      }
      // TODO: study these two lines and uncomment them if necessary. It may be required in the future (it was in previous
      // versions). Change done in the phase of module testing.
      // replaceModuleNameByHostName(topology, (Map<String, Object>)
      // appMap.get(TOSCAkeywords.NODE_TEMPLATE));
      return topology;
   }

   private static void replaceModuleNameByHostName(Topology topology, Map<String, Object> modules) {

      for (String modName : topology.getModuleNamesIterator()) {
         String newName = getFinalHostNameOfModule(modules, modName);
         topology.replaceElementName(modName, newName);
      }

   }

   private static String getFinalHostNameOfModule(Map<String, Object> modules, String modName) {

      Map<String, Object> module = null;
      try {
         if (modules.containsKey(modName)) {
            module = (Map<String, Object>) modules.get(modName);
         } else {
            log.warn(
                  "Looking for module that do not exist in the set of application modules. Expecting errors in the execution");
            return null;
         }
      } catch (ClassCastException E) {
         log.warn("set of application modules does not look well-formed . Expecting errors in the execution");
         return null;
      }

      if (modules.containsKey(YAMLmodulesOptimizerParser.getHostOfModule(module))) {
         return getFinalHostNameOfModule(modules, YAMLmodulesOptimizerParser.getHostOfModule(module));
      } else {
         return modName;
      }

   }

   private static Topology getApplicationTopologyRecursive(String elementName, Map<String, Object> element,
         Topology topology, Map<String, Object> modules, Map<String, Object> groups,
         SuitableOptions appInfoSuitableOptions) {

      if (topology.contains(elementName)) {
         return topology;
      }

      TopologyElement newelement = new TopologyElement(elementName);
      double hostPerformance = appInfoSuitableOptions.getCloudCharacteristics(elementName,
            YAMLmodulesOptimizerParser.getMeasuredPerformanceHost(elementName, groups)).getPerformance();


      newelement.setExecTimeMillis(
            YAMLmodulesOptimizerParser.getMeasuredExecTimeMillis(elementName, groups) * hostPerformance);

      // The module does not have requiremetns
      if (!YAMLmodulesOptimizerParser.moduleHasModuleRequirements(elementName, groups)) {
         // Include it directly
         topology.addModule(newelement);
         return topology;
      }

      // module has requirements
      for (String moduleReqName : YAMLmodulesOptimizerParser.moduleRequirementsOfAModule(elementName, groups)) {
         // For each requiremnt of teh element (that is not its host but it's a
         // module in the system)
         if (topology.contains(moduleReqName)) {

            // The dependence may already exist as well... Check it.
            // (Correction: if the topology is creted in the proper way, the
            // dependence cannot exist yet)
            // Read the operational profile for the number of calls.

            double opProfileBetweenModules = YAMLgroupsOptimizerParser.getOpProfileWithModule(elementName,
                  moduleReqName, groups);
            // create the dependence between these two modules by
            // addelementcalled.
            newelement.addElementCalled(
                  new TopologyElementCalled(topology.getModule(moduleReqName), opProfileBetweenModules));

         } else {
            // Recursive call for the moduleReqNAme, and this element and
            // associate with this element.
            topology = getApplicationTopologyRecursive(moduleReqName, (Map<String, Object>) modules.get(moduleReqName),
                  topology, modules, groups, appInfoSuitableOptions);
            double opProfileBetweenModules = YAMLgroupsOptimizerParser.getOpProfileWithModule(elementName,
                  moduleReqName, groups);
            // create the dependence between these two modules by
            // addelementcalled.
            newelement.addElementCalled(
                  new TopologyElementCalled(topology.getModule(moduleReqName), opProfileBetweenModules));
         }
      }

      // add it as a element of the topology (only if it is not already
      // included), with its qos characteristics (performance)
      topology.addModule(newelement);
      return topology;

   }

   /**
    * @return Default qualityInformation. Used just for testing the full execution. 
    */
   public static QualityInformation getQualityRequirementsForTesting() {

      // TODO This method should not exist in the future, when quality
      // requirements exist as input
      log.error("Dummy requirements are: responseTime=1second , availability=0.9, cost=10");

      QualityInformation requirements = new QualityInformation();

      requirements = new QualityInformation();
      requirements.setResponseTimeSecs(1.0);
      requirements.setAvailability(0.9);
      requirements.setCostHour(10.0);
      requirements.setWorkload(-1.0);

      return requirements;
   }

   public static double getApplicationWorkloadTest() {
      log.error("Dummy workload is assumed to be 10 requests per second");
      return 10.0;
   }

   public static void addReconfigurationThresholds(HashMap<String, ArrayList<Double>> thresholds,
         Map<String, Object> applicationMap) {
      if (thresholds != null) {
         thresholdsFromSecondsToMinutes(thresholds);
         applicationMap.put(TOSCAkeywords.RECONFIGURATION_WORKLOAD_TAG, thresholds);
      }
   }

   private static void thresholdsFromSecondsToMinutes(HashMap<String, ArrayList<Double>> thresholds) {

      for (Map.Entry<String, ArrayList<Double>> entry : thresholds.entrySet()) {
         ArrayList<Double> list = entry.getValue();

         // multiply each element by the ratio between seconds and minutes (60)
         for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i) * 60.0);
         }

      }

   }

   /**
    * @param yamlMap
    * @return a clone of the Map It uses the funcionaalities to save as string
    *         "dump" and load to create a new Map
    */
   public static Map<String, Object> cloneYAML(Map<String, Object> yamlMap) {

      String stringyaml = YAMLoptimizerParser.fromMAPtoYAMLstring(yamlMap);
      Map<String, Object> newMap = YAMLoptimizerParser.getMAPofAPP(stringyaml);

      return newMap;

   }

   @SuppressWarnings("unchecked")
   private static Map.Entry<String, Object> getInitialElement(Map<String, Object> appMap) {
      // The initial element is such one that has QoSrequirements in the
      // "groups" part.

      Map<String, Object> groupsMap = YAMLoptimizerParser.getGroupMapFromAppMap(appMap);

      // FOR EACH OF THE APP MODULES (but in this level there are more concepts
      // than only the modules)
      for (Map.Entry<String, Object> entry : groupsMap.entrySet()) {
         String potentialGroupName = entry.getKey();

         if (IS_DEBUG) {
            log.debug("checking if group '" + potentialGroupName + "' is an element which receceives user requests");
         }
         // If it has requirements but nobody requires it..
         if (YAMLgroupsOptimizerParser.groupHasQoSRequirements(entry.getValue())) {
            // group found.
            // Now, 1) get the name of modules that are members of the group.
            // Since it should be only one member in the group in as list
            // we get the first module name found
            String moduleName = YAMLgroupsOptimizerParser.getFirstMemberName(entry.getValue());
            // 2) find the module in the topology with this name.

            Map<String, Object> modulesMap = YAMLoptimizerParser.getModuleMapFromAppMap(appMap);
            return YAMLoptimizerParser.getModuleInfoFromModulesMap(modulesMap, moduleName);

         }
      }

      log.warn(
            "Initial element not found unveiling the topology. Possible circular dependences in the design. Please, state clearly which the initial element is");
      return null;
   }

   @SuppressWarnings("unchecked")
   private static String getInitialElementName(Map<String, Object> appMap) {
      // The initial element is such one that has QoSrequirements in the
      // "groups" part.

      Map<String, Object> groupsMap = YAMLoptimizerParser.getGroupMapFromAppMap(appMap);

      // FOR EACH OF THE APP MODULES (but in this level there are more concepts
      // than only the modules)
      for (Map.Entry<String, Object> entry : groupsMap.entrySet()) {
         String potentialGroupName = entry.getKey();

         if (IS_DEBUG) {
            log.debug("checking if group '" + potentialGroupName + "' is an element which receceives user requests");
         }
         // If it has requirements but nobody requires it..
         if (YAMLgroupsOptimizerParser.groupHasQoSRequirements(entry.getValue())) {
            // group found.
            // The name of modules that are members of the group.
            // Since it should be only one member in the group in as list
            // we get the first module name found
            return YAMLgroupsOptimizerParser.getFirstMemberName(entry.getValue());

         }
      }

      log.warn(
            "Initial element not found unveiling the topology. Possible circular dependences in the design. Please, state clearly which the initial element is");
      return null;
   }

   private static Entry<String, Object> getModuleInfoFromModulesMap(Map<String, Object> modulesMap, String moduleName) {
      if (modulesMap.containsKey(moduleName)) {
         for (Map.Entry<String, Object> entry : modulesMap.entrySet()) {
            if (entry.getKey().equals(moduleName)) {
               if (IS_DEBUG) {
                  log.debug("return entry that describes module with name '" + entry.getKey() + "'");
               }
               return entry;
            }
         }
      }
      log.warn("Module description not found. Check correcness of name given as group members and modules name");
      return null;
   }

   @SuppressWarnings("unchecked")
   private static Map<String, Object> getModuleMapFromAppMap(Map<String, Object> appMap) {

      Map<String, Object> modulesMap;
      try {
         if (IS_DEBUG) {
            log.debug("Opening TOSCA for obtaining modules");
         }
         modulesMap = (Map<String, Object>) appMap.get(TOSCAkeywords.TOPOLOGY_TEMPLATE);
         modulesMap = (Map<String, Object>) modulesMap.get(TOSCAkeywords.NODE_TEMPLATE);
      } catch (NullPointerException E) {
         log.error("It was not found '" + TOSCAkeywords.TOPOLOGY_TEMPLATE + "' or '" + TOSCAkeywords.NODE_TEMPLATE
               + "' in the TOSCA model");
         return null;
      }

      return modulesMap;
   }

   private static Map<String, Object> getGroupMapFromAppMap(Map<String, Object> appMap) {
      Map<String, Object> groupsMap;
      try {
         if (IS_DEBUG) {
            log.debug("Opening TOSCA for obtaining groups");
         }
         groupsMap = (Map<String, Object>) appMap.get(TOSCAkeywords.GROUP_ELEMENT_TAG);

      } catch (NullPointerException E) {
         log.error("It was not found '" + TOSCAkeywords.GROUP_ELEMENT_TAG
               + "' . Cannot be unveiled the dependencies between modules calls");
         return null;
      }
      return groupsMap;
   }

   private static boolean moduleIsRequiredByOthers(Map<String, Object> appMap, String potentialModuleName) {

      for (Map.Entry<String, Object> entry : appMap.entrySet()) {
         if (YAMLmodulesOptimizerParser.moduleRequirementFromTo(entry.getValue(), potentialModuleName)) {
            return true;
         }
      }
      return false;
   }

}
