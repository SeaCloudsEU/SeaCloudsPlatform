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
import eu.seaclouds.common.objectmodel.hosttypes.HostType;
import eu.seaclouds.common.objectmodel.providers.Provider;

import java.util.Collection;
import java.util.HashMap;

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

  public Location getHostedOn() {
    return hostedOn;
  }

  public void setHostedOn(Location hostedOn) {
    this.hostedOn = hostedOn;
  }

  public AppModule(Application app, String name){
    //TODO
  }

  public void setProvider(Class<? extends Provider> p){
    //TODO
  }

  public void setApplicationType(Class<? extends AppType> t){
    //TODO
  }

  public void setHostType(Class<? extends HostType> t){
    //TODO
  }
  public Class<? extends HostType> getHostType(){
    return kind;
  }

  public void setLocation(Location l){
    //TODO
  }

  public Collection<Constraint> getTechnicalRequirements(){
    //TODO
    return null;
  }

  public void addTechnicalRequirement(Constraint c){
    //TODO
  }

  public boolean removeTechnicalRequirement(Constraint c){
    //TODO
    return false;
  }

  public Collection<Feature> getProperties(){
    //TODO
    return null;
  }

  public void addProperty(Feature f){
    //TODO
  }

  public boolean removeProperty(Feature f){
    //TODO
    return false;
  }

  public Collection<Artifact> getArtifacts(){
    //TODO
    return null;
  }

  public void addArtifact(Artifact a){
    //TODO
  }

  public boolean removeArtifact(Artifact a){
    //TODO
    return false;
  }

  public HashMap<String,Constraint> getQoSRequirements(){
    return qoSRequirements;
  }

  public void addQoSRequirements(Constraint c) {
    //TODO
  }

  public boolean removeQoSRequirements(Constraint c) {
    //TODO
    return false;
  }
}
