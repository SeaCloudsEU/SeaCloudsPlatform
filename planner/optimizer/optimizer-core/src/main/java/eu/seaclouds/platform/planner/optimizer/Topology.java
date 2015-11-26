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

package eu.seaclouds.platform.planner.optimizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Topology {

   static Logger log = LoggerFactory.getLogger(Topology.class);

   private List<TopologyElement> modules;

   public Topology() {
      modules = new ArrayList<TopologyElement>();
   }

   public void addModule(TopologyElement e) {

      if (modules.contains(e)) {
         log.warn("Adding more than one time the same element to the topology");
      }

      // Potential error if it is included an element already contained
      modules.add(e);

   }

   public TopologyElement getModule(String name) {

      for (TopologyElement e : modules) {
         if (e.getName().equals(name)) {
            return e;
         }
      }
      return null;

   }

   public int size() {
      return modules.size();
   }

   public TopologyElement getInitialElement() {
      // We assume that The initial element is such one that is not called by
      // anyone.

      for (TopologyElement pointed : modules) {
         boolean isInitial = true;

         // Check if any of the elements in the topology depends on (calls) it
         for (TopologyElement pointer : modules) {
            if (pointer.dependsOn(pointed.getName())) {
               isInitial = false;
            }

         }

         if (isInitial) {
            return pointed;
         }

      }

      log.warn(
            "Initial element not found. Possible circular dependences in the design. Please, state clearly which is the initial element");
      return null;
   }

   public int indexOf(TopologyElement initialElement) {
      return modules.indexOf(initialElement);
   }

   public void replaceElementName(String modName, String newName) {

      for (TopologyElement e : modules) {
         if (e.getName().equals(modName)) {
            e.setName(newName);
         }
      }

   }

   /**
    * @param element
    * @param toIndex
    *           Swaps the elements indexes. "Element" gets "toIndex" index and
    *           the previous element with index "toIndex" gets the previous
    *           index of "element"
    * 
    */
   public void replaceElementsIndexes(TopologyElement element, int toIndex) {
      int targetIndex = modules.indexOf(element);
      if (log.isDebugEnabled()) {
         if(modules==null){
            log.warn("Modules in topology points to NULL");
         }
         if(element==null){
            log.warn("Element to search in topology points to NULL");
         }
         log.debug("The topology consists of " + modules.size() + " modules. Replacing index " + toIndex + " with "
               + targetIndex + ". The element name whose index was searched was " + element.getName() + "and the topology was composed of modules: " + toString());
      }
      TopologyElement replaced = modules.set(toIndex, element);
      modules.set(targetIndex, replaced);
   }

   // TODO: This operation can break encapsulation. Since it is only used to
   // iterate over the elements in the list
   // implement an iterator in this class over the modules list
   public List<TopologyElement> getModules() {
      return modules;
   }

   public TopologyElement getElementIndex(int index) {
      return modules.get(index);
   }

   public boolean contains(String elementName) {
      return getModule(elementName) != null;
   }

   @Override
   public String toString() {
      String NL = System.getProperty("line.separator");
      String out = "";
      for (TopologyElement mod : modules) {
         out += mod.getName() + " : execTime - " + mod.getDefaultExecutionTime() + " : dependences - {";
         for (TopologyElementCalled modc : mod.getDependences()) {
            out += modc.getElement().getName() + "(" + modc.getProbCall() + "), ";
         }
         out += "}" + NL + NL;

      }

      return out;

   }

   // ITERATORS
   // ITERATOR OVER THE ELEMENTS
   // @Override
   abstract class AbstractIterator<T> implements Iterable<T>, Iterator<T> {
      int currentIndex = 0;

      @Override
      public boolean hasNext() {
         return currentIndex < modules.size();
      }

      @Override
      public void remove() {

      }

      @Override
      public Iterator<T> iterator() {
         return this;
      }

   }

   // Iterator that returns list of dependencies
   class DependencyListsIterator extends AbstractIterator<List<TopologyElementCalled>> {

      @Override
      public List<TopologyElementCalled> next() {
         List<TopologyElementCalled> currentList = modules.get(currentIndex).getDependences();
         currentIndex++;
         return currentList;

      }
   }

   // Iterator that returns module names
   class ModuleNamesIterator extends AbstractIterator<String> {
      @Override
      public String next() {
         String currentModName = modules.get(currentIndex).getName();
         currentIndex++;
         return currentModName;
      }
   }

   public Iterable<List<TopologyElementCalled>> getDependencyListsIterator() {

      return new DependencyListsIterator();
   }

   public Iterable<String> getModuleNamesIterator() {
      return new ModuleNamesIterator();
   }

}
