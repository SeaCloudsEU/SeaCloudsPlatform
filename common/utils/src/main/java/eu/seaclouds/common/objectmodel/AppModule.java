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

import eu.seaclouds.common.objectmodel.apptypes.AppType;
import eu.seaclouds.common.objectmodel.constraints.Constraint;
import eu.seaclouds.common.objectmodel.hosttypes.HostType;
import eu.seaclouds.common.objectmodel.providers.Provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class AppModule {
  Application app;
  Location hostedOn; //hostedon should be an appmodule, while location is for the real deployment?
  String name;
  Class<? extends AppType> type; //Jboss, mysql, ...
  Class<? extends HostType> kind;
  Class<? extends Provider> provider;
  Collection<Constraint> technicalRequirements;
  Collection<Feature> properties;
  Collection<Artifact> artifacts;

  HashMap<String, Constraint> qoSRequirements; //TODO: check, it is required by diego

  public AppModule(Application app, String name){
    this.app = app;
    this.name = name;
    technicalRequirements = new HashSet<>();
    properties = new HashSet<>();
    artifacts = new HashSet<>();
  }

  public Location getHostedOn() { return hostedOn; }
  public void setHostedOn(Location hostedOn) { this.hostedOn = hostedOn; }

  public void setProvider(Class<? extends Provider> p){ this.provider = p; }
  public Class<? extends Provider> getProvider(){ return this.provider; }

  public void setApplicationType(Class<? extends AppType> t){ this.type = t; }
  public Class<? extends AppType> getApplicationType() { return this.type;}

  public void setHostType(Class<? extends HostType> t){ this.kind = t; }
  public Class<? extends HostType> getHostType(){ return kind; }


  public void setLocation(Location l){
    //TODO: not sure of what this will be
  }

  public Collection<Constraint> getTechnicalRequirements(){ return technicalRequirements; }
  public void addTechnicalRequirement(Constraint c){ technicalRequirements.add(c); }

  public boolean removeTechnicalRequirement(Constraint c){
     return technicalRequirements.remove(c);
  }

  public Collection<Feature> getProperties(){
    return properties;
  }

  public void addProperty(Feature f){
    properties.add(f);
  }

  public boolean removeProperty(Feature f){
    return properties.remove(f);
  }

  public Collection<Artifact> getArtifacts(){
    return artifacts;
  }

  public void addArtifact(Artifact a){
    artifacts.add(a);
  }

  public boolean removeArtifact(Artifact a){
    return artifacts.remove(a);
  }

  public HashMap<String,Constraint> getQoSRequirements() {
    return qoSRequirements; //TODO: check if needed by Diego
  }

  public void addQoSRequirements(Constraint c) {
    //TODO: see above
  }

  public boolean removeQoSRequirements(Constraint c) {
    //TODO: see above
    return false;
  }
}
