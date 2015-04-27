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
package model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Application implements Module {

    // TODO: Search application/module by ID

    private String id;
    private String name;
    private List<Module> children;

    public Application(String id, String name) {
        this.id = id;
        this.name = name;
        this.children = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<Module> getChildren() {
        return children;
    }

    @Override
    public void addChild(Module child) {
        children.add(child);
    }

    @Override
    public Module getParentApplication() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Application that = (Application) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
