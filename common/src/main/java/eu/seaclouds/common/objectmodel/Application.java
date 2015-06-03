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

public class Application {
    String appId;
    String name;
    AppState state;
    DesignState designState;
    Collection<AppModule> modules;
    Collection<Effector> effectors; //TODO: check if is better to put this inside modules
    Collection<Artifact> artifacts; //TODO: chekc probably not needed here (in module)
    Collection<GuaranteeTerm> slaRules;

    public Application(String id, String name, AppState state, DesignState designState){
        this.appId = id;
        this.name = name;
        modules = Collections.EMPTY_SET;
        effectors = Collections.EMPTY_SET;
        artifacts = Collections.EMPTY_SET;
        slaRules = Collections.EMPTY_SET;
        this.state = state;
        this.designState = designState;
    }

    public String getName(){ return name; }
    public String getId() { return appId; }
    public AppState getState() { return state; }
    public DesignState getDesignState() { return designState; }
    public Collection<AppModule> getModules() { return modules; }
    public Collection<Effector> getEffectors() { return effectors; }

    public void addModule(AppModule m){
        //TODO:
    }

    public boolean removeModule(AppModule m){
        //TODO:
        return false;
    }

    public void addEffector(Effector e){
        //TODO:
    }

    public boolean removeEffector(Effector e){
        //TODO:
        return false;
    }


}
