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

package core;


public class UnixFileExecution {

	public static Process execute(String exportUnixFile, String initUnixFile) {

		Process p = null;

		try {

			if (!exportUnixFile.equals(""))
				p = Runtime.getRuntime().exec(
						"/bin/bash -c 'source " + exportUnixFile + "'");

			p = Runtime.getRuntime().exec("/bin/bash " + initUnixFile + "");

			p.waitFor();
		}

		catch (Exception ex) {

			ex.printStackTrace();

			p = null;
		}

		return null;
	}

}
