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

package eu.seaclouds.platform.discoverer.ws;

import java.util.Collection;

public abstract class SCSpider {

	public abstract CrawlingResult[] crawl();


	/**
	 * Emulates the behavior of the java8 <code>String.join</code> method.
	 * @param delimiter Delimiter between the strings.
	 * @param strings The collection of strings to join together.
	 * @return A string obtained by the concatenation of all the strings, separated by
	 * <code>delimiter</code>.
	 */
	public static String join(String delimiter, Collection<String> strings) {
		StringBuilder sb = new StringBuilder();
		int count = strings.size();
		for(String current : strings) {
			sb.append(current);
			count--;
			if(count != 0)
				sb.append(delimiter);
		}
		return sb.toString();
	}
	
}
