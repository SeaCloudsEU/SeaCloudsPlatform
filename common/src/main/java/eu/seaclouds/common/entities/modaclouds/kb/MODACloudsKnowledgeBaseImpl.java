/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.common.entities.modaclouds.kb;

import brooklyn.entity.basic.SoftwareProcessImpl;

public class MODACloudsKnowledgeBaseImpl extends SoftwareProcessImpl implements MODACloudsKnowledgeBase {
   @Override
   public Class getDriverInterface() {
      return eu.seaclouds.common.entities.modaclouds.kb.MODACloudsKnowledgeBaseDriver.class;
   }

   @Override
   public String getShortName() {
      return "MODAClouds KB";
   }

   @Override
   protected void connectSensors() {
      super.connectSensors();
      connectServiceUpIsRunning();
   }

   @Override
   protected void disconnectSensors() {
      disconnectServiceUpIsRunning();
      super.disconnectSensors();
   }

}