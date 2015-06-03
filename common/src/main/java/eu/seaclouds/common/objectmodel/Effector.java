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
package eu.seaclouds.common.objectmodel;

import java.util.Collection;

public class Effector { //TODO: find better name? And check if this should go inside the AppModule
  String name;
  Collection<AppModule> owners;
  Number avgUsageCount;
  Collection<Feature> qosInfo;
  Collection<Constraint> qosRequirement;
  Collection<Dependency> dependencyInOtherEffectors;

  public Effector(String name, Number avgUsageCount){
    //TODO
  }

  public Effector(String name){
    this(name, 0);
  }

  public Collection<AppModule> getOwners(){
    return owners;
  }

  public void addOwner(AppModule m){
    owners.add(m);
  }

  public boolean removeOwner(AppModule m){
    return owners.remove(m);
  }

  public Number getAvgUsageCount() { return avgUsageCount; }

  public Collection<Feature> getQoSInfo(){
    return  qosInfo;
  }

  public void addQoSInfo(Feature f){ qosInfo.add(f); }
  public boolean removeQoSInfo(Feature f) { return qosInfo.remove(f); }

  public Collection<Constraint> getQosRequirement(){
    return qosRequirement;
  }

  public void addQoSRequirements(Constraint c){ qosRequirement.add(c); }
  public boolean removeQOSRequirement(Constraint c) { return qosInfo.remove(c); }

  public Collection<Dependency> getDependencyInOtherEffectors(){
    return dependencyInOtherEffectors;
  }
}
