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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Solution implements Iterable<String>{

	/*Simple class that serves as a data structure to store a 
	 * solution for the cloud in a Map of (moduleName, CloudOptionUsed)
	 */
	
	static Logger log = LoggerFactory.getLogger(Solution.class);
	
	private Map<String, String> modName_ModOption; 
	private Map<String, Integer> modName_NumInstances; 
	
	public Solution(){
		modName_ModOption = new HashMap<String, String>();
		modName_NumInstances = new HashMap<String, Integer>();
	}
	
	public void addItem(String name, String cloudOption){
		addItem(name, cloudOption,1);
	}
	
	public void addItem(String name, String cloudOption, int numInstances){
		modName_ModOption.put(name, cloudOption);
		modName_NumInstances.put(name, numInstances);
	}
	
	public String getCloudOfferNameForModule(String key){
		return modName_ModOption.get(key);
	}
	
	public int getCloudInstancesForModule(String key){
		return modName_NumInstances.get(key);
	}
	
	public void modifyNumInstancesOfModule(String modulename,	int newInstances) {
		
		if(!modName_NumInstances.containsKey(modulename)){
			log.error("trying to modify the number of instances of a module which does not exist");
		}
		else{
			modName_NumInstances.put(modulename, newInstances);
		}
		
	}
	
	public Solution clone(){
		
		Solution sol= new Solution();
		for(String key : this){
			sol.addItem(key, this.getCloudOfferNameForModule(key), this.getCloudInstancesForModule(key));
		}
		
		return sol;
	}
	
	//ITERATOR
	public Iterator<String> iterator() {
        Iterator<String> it = new Iterator<String>() {

        	private Set<Entry<String, String>> entries = modName_ModOption.entrySet();
        	Iterator<Entry<String, String>> iteratorSet = entries.iterator();

            @Override
            public boolean hasNext() {
                return iteratorSet.hasNext();
                		
            }

            @Override
            public String next() {
            	return iteratorSet.next().getKey();
            	                		
            }

            @Override
            public void remove() {
                // TODO Auto-generated method stub
            }
        };
        return it;
    }


	
	
}
