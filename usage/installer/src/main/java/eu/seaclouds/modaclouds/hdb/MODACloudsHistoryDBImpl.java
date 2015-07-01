/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package eu.seaclouds.modaclouds.hdb;

import brooklyn.entity.basic.SoftwareProcessImpl;

public class MODACloudsHistoryDBImpl extends SoftwareProcessImpl implements MODACloudsHistoryDB {
   @Override
   public Class getDriverInterface() {
      return eu.seaclouds.modaclouds.hdb.MODACloudsHistoryDBDriver.class;
   }

   @Override
   public String getShortName() {
      return "MODAClouds HDB";
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
