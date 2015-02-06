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

public class SuitableOptions { //implements Iterable<List<String>

	
	static Logger log = LoggerFactory.getLogger(SuitableOptions.class);
	
	ArrayList<List<String>> suitableOptions;
	ArrayList<String> moduleNames;
	
	public SuitableOptions(){
		suitableOptions = new ArrayList<List<String>>();
		moduleNames = new ArrayList<String>();
	}
	
	public void addSuitableOptions(String moduleName, List<String> options){
		
		log.debug("Adding the options of module " + moduleName + " which consists of " + options.size() + " elements");
		
		
		moduleNames.add(moduleName);
		suitableOptions.add(options);

	}
	

/* Better not expose the getter because it's referencing the input yaml, not copying it
 * 
 *
	public List<String> getSuitableOptions(String moduleName){
		
		int i=0;
		boolean found=false;
		while((i<moduleNames.size())&& (!found)){
			if(moduleNames.get(i).equalsIgnoreCase(moduleName)){
				found=true;
			}
			else{
				i++;
			}
		}
		
		if(found){
			return suitableOptions.get(i); 
		}
		else{System.out.println("getSuitableOptions@SuitableOptions: Error, name of module not found: Return NULL");}
		
		return null;
	}
	*/

	public int getSizeOfSuitableOptions(String moduleName){
		
			
		int i=0;
		boolean found=false;
		while((i<moduleNames.size())&& (!found)){
			if(moduleNames.get(i).equalsIgnoreCase(moduleName)){
				found=true;
			}
			else{
				i++;
			}
		}
		
		if(found){
			return suitableOptions.get(i).size(); 
		}
		else{System.out.println("getSuitableOptions@SuitableOptions: Error, name of module not found: Return -1");}
		
		return -1;
	}
	
	public SuitableOptions clone(){
		
		SuitableOptions cloned = new SuitableOptions();
		
		//Clone moduleName
		for(String moduleName : moduleNames){
			cloned.moduleNames.add(moduleName);
		}
		
		
		//Clone suitableOptions
		for(List<String> l : suitableOptions){
			
			List<String> clonedList = (List<String>)  new ArrayList<String>();
			
			for(String option : l){
				clonedList.add(option);
			}
			cloned.suitableOptions.add(clonedList);
		}
		
		
		return cloned;
	}
	
	public String getIthSuitableOptionForModuleName(String moduleName, int optionPosition) {
		
		
		int i=0;
		boolean found=false;
		while((i<moduleNames.size())&& (!found)){
			if(moduleNames.get(i).equalsIgnoreCase(moduleName)){
				found=true;
			}
			else{
				i++;
			}
		}
		
		if(found){
			
			//if module found and there exist suitable options for it (i.e., suitableOptions.get(i).size()>0). 
			if(suitableOptions.get(i).size()>0){
				return suitableOptions.get(i).get(optionPosition); 
			}
			else{
				return null;
			}
			
			
		}
		else{System.out.println("getIthSuitableOptionForModuleName@SuitableOptions: Error, name of module not found: Return NULL");}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	//ITERATOR OVER THE ELEMENTS
	//@Override
    abstract class AbstractIterator<T> implements Iterable<T>, Iterator<T>{
    	 int currentIndex = 0;

         @Override
         public boolean hasNext() {
             return currentIndex < moduleNames.size();
         }
         
         @Override
         public void remove() {
             // TODO Auto-generated method stub
         }
         
         @Override
         public Iterator<T> iterator() {
             return this;
         }
             		
    }
    
    class ListIterator extends AbstractIterator<List<String>>{
    	
    	@Override
         public List<String> next() {
         	List<String> currentList=suitableOptions.get(currentIndex);
         	currentIndex++;
             return currentList;
             		
         }
    }
	
    class StringIterator extends AbstractIterator<String>{
    	@Override
        public String next() {
        	String currentModName=moduleNames.get(currentIndex);
        	currentIndex++;
            return currentModName;
    	}
    }
    
    

	public Iterable<List<String>> getListIterator() {
		
		return new ListIterator();
	}
	
	public Iterable<String> getStringIterator(){
		return new StringIterator();
	}


	
}
