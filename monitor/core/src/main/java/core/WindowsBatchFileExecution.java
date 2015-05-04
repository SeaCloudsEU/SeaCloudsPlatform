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

import java.io.File;

public class WindowsBatchFileExecution {

	public static Process execute(String batchFile) {

		Process p = null;

		try {

			File file = new File(batchFile);

			p = Runtime.getRuntime().exec(
					"cmd /C start /wait " + file.getAbsolutePath());

			p.waitFor();
		}

		catch (Exception ex) {

			ex.printStackTrace();

			System.exit(-1);

			p = null;
		}

		return null;
	}
}
