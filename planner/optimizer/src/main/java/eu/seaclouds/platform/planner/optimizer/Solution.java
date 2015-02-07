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

public class Solution implements Iterable<String>{

	/*Simple class that serves as a data structure to store a 
	 * solution for the cloud in a Map of (moduleName, CloudOptionUsed)
	 */
	
	
	private Map<String, String> modName_ModOption; 
	
	public Solution(){
		modName_ModOption = new HashMap<String, String>();
	}
	
	public void addItem(String name, String cloudOption){
		modName_ModOption.put(name, cloudOption);
	}
	
	public String getItem(String key){
		return modName_ModOption.get(key);
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
