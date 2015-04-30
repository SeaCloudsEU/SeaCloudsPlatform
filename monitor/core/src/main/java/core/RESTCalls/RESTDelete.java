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

package core.RESTCalls;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

public class RESTDelete {

	public static String httpDelete(String urlStr) throws Exception {

		HttpClient client = new DefaultHttpClient();

		HttpDelete delete = new HttpDelete(urlStr);

		HttpResponse response = client.execute(delete);

		String result = null;

		if (response != null && response.getEntity() != null) {

			BufferedReader r = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuilder total = new StringBuilder();

			String line = null;

			while ((line = r.readLine()) != null)
				total.append(line + "\n");

			result = total.toString();
		}

		return result;
	}
}
