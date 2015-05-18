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

package core.modaCloudsMonitoring.modaCloudsMonitoringRules;

import core.RESTCalls.RESTDelete;
import core.RESTCalls.RESTPost;

public class MODACloudsMonitoringRules {

	public static void installMonitoringRules(String IPofMM, String portOfMM,
			String monitoringRules) {

		RESTPost.httpPost("http://" + IPofMM + ":" + portOfMM
				+ "/v1/monitoring-rules", monitoringRules, "xml");
	}

	public static void uninstallMonitoringRules(String IPofMM, String portOfMM,
			String id) {

		RESTDelete.httpDelete("http://" + IPofMM + ":" + portOfMM
				+ "/v1/monitoring-rules/" + id);
	}
}
