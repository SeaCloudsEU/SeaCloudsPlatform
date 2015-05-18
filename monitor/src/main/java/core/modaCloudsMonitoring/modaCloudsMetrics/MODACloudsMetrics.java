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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import core.RESTCalls.RESTGet;

public class MODACloudsMetrics {

	public static String[] getRunningMetrics(String IPofMM, String portOfMM) {

		try {

			String metrics = RESTGet.httpGet("http://" + IPofMM + ":"
					+ portOfMM + "/v1/metrics");

			JSONParser jsonParser = new JSONParser();

			Object object = jsonParser.parse(metrics);

			JSONObject jsonObject = (JSONObject) object;

			List<String> allArrayElements = new ArrayList<String>();

			getAllArrayElements(jsonObject, allArrayElements);

			String[] result = allArrayElements != null ? allArrayElements
					.toArray(new String[allArrayElements.size()]) : null;

			return result;
		}

		catch (Exception ex) {

			ex.printStackTrace();

			return null;
		}
	}

	private static void getArray(Object object, List<String> allArrayElements)
			throws ParseException {

		JSONArray jsonArr = (JSONArray) object;

		for (int k = 0; k < jsonArr.size(); k++) {

			if (jsonArr.get(k) instanceof JSONObject)
				getAllArrayElements((JSONObject) jsonArr.get(k),
						allArrayElements);

			else
				allArrayElements.add(jsonArr.get(k).toString());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void getAllArrayElements(JSONObject jsonObject,
			List<String> allArrayElements) throws ParseException {

		Set<Object> set = jsonObject.keySet();

		Iterator iterator = set.iterator();

		while (iterator.hasNext()) {

			Object obj = iterator.next();

			if (jsonObject.get(obj) instanceof JSONArray)
				getArray(jsonObject.get(obj), allArrayElements);

			else if (jsonObject.get(obj) instanceof JSONObject)
				getAllArrayElements((JSONObject) jsonObject.get(obj),
						allArrayElements);
		}
	}

	public static String[] getAllMetrics() {

		String[] metrics = { "ResponseTime", "CPUUtilization",
				"MemoryUtilization", "Queries", "Availability" };

		return metrics;
	}
}
