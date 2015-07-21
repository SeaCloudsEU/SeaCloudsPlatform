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
import java.util.Collections;
import java.util.UUID;

public class Application {
    UUID appId;
    String name;
    AppState state;
    DesignState designState;
    Collection<AppModule> modules;
    Collection<Effector> effectors; //TODO: check if is better to put this inside modules
    Collection<Artifact> artifacts; //TODO: check, probably not needed here (iside module?)
    Collection<GuaranteeTerm> slaRules;

    public Application(String name, AppState state, DesignState designState){
        this.appId = UUID.randomUUID();
        this.name = name;
        modules = Collections.EMPTY_SET;
        effectors = Collections.EMPTY_SET;
        artifacts = Collections.EMPTY_SET;
        slaRules = Collections.EMPTY_SET;
        this.state = state;
        this.designState = designState;
    }

    public String getName(){ return name; }
    public UUID getId() { return appId; }
    public AppState getState() { return state; }
    public DesignState getDesignState() { return designState; }
    public Collection<AppModule> getModules() { return modules; }
    public Collection<Effector> getEffectors() { return effectors; }
    public Collection<GuaranteeTerm> getSlaRules() { return slaRules; }

    public void setName(String name) { this.name = name; }
    public void setState(AppState s) { this.state = s; }
    public void setDesignState(DesignState s) { this.designState = s; }

    public void addModule(AppModule m){
        modules.add(m);
    }

    public boolean removeModule(AppModule m){
        return modules.remove(m);
    }

    public void addEffector(Effector e){
        effectors.add(e);
    }

    public boolean removeEffector(Effector e){
        return effectors.remove(e);
    }

    public <T extends GuaranteeTerm> void addSLARule(T r){
        slaRules.add(r);
    }

    public <T extends GuaranteeTerm> boolean removeSLARule(T r){
        return slaRules.remove(r);
    }

}
