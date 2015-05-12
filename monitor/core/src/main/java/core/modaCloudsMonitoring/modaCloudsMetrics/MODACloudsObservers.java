/**
 * Copyright 2014 SeaClouds
 * Contact: Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
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

package core.modaCloudsMonitoring.modaCloudsMetrics;

import core.RESTCalls.RESTPost;

/**
 * 
 * @author Dionysis Athanasopoulos <dionysiscsuoi@gmail.com>
 * 
 */
public class MODACloudsObservers {

	public static void addObserver(String IPofMM, String portOfMM,
			String metricName, String callbackURL) {

		try {

			String monitoringManagerURL = "http://" + IPofMM + ":" + portOfMM
					+ "/v1/metrics/" + metricName + "/observers";

			callbackURL = callbackURL + "/v1/results";

			RESTPost.httpPost(monitoringManagerURL, callbackURL);
		}

		catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	public static void startObserver(String portOfObserver) {

		CVSObServer observer = new CVSObServer(Integer.parseInt(portOfObserver));

		try {

			observer.start();
		}

		catch (Exception ex) {

			ex.printStackTrace();
		}
	}
}
