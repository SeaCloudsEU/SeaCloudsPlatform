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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * 
 * Class that provides methods to get the information of cloud offers for each module in the format agreed in September 2015
 *
 */
public class YAMLmatchmakerToOptimizerParser {

   static Logger log = LoggerFactory.getLogger(YAMLmatchmakerToOptimizerParser.class);
   
   @SuppressWarnings("unchecked")
   public static List<Object> GetListofOptions(String appModel) {
      Yaml yamlApp = new Yaml();
      log.info("Loading String to a YAML using sknakeyaml.Yaml");
      return (List<Object>) yamlApp.load(appModel);
   }

   public static String FromListtoYAMLstring(List<Object> appMap) {
      DumperOptions options = new DumperOptions();
      options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());

      Yaml yamlApp = new Yaml(options);

      return yamlApp.dump(appMap);
      
   }
   
}
