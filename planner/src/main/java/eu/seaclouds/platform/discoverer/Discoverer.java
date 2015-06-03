/*
 * Copyright 2015 Universita' di Pisa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.seaclouds.platform.discoverer;

import seaclouds.utils.toscamodel.IToscaEnvironment;
import seaclouds.utils.toscamodel.Tosca;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Discoverer {
    private final IToscaEnvironment offerings;
    private static final Discoverer instance = new Discoverer();
	
	
 
	/* singleton */
	public static Discoverer instance() {
        return instance;
    }

	
	
	/* c.tor */
    private Discoverer() {
        offerings = Tosca.newEnvironment();
        /* try {
            //configure discoverer
            InputStream stream;
            CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry e = zip.getNextEntry();
                while (e != null) {
                    String name = e.getName();
                    if (name.startsWith("seaclouds/planner/offerings/") && name.endsWith(".yaml")){
                        stream = this.getClass().getResourceAsStream(name);
                        offerings.readFile(new InputStreamReader(stream));
                    }
                }

            }
        } catch (IOException e) {

        } */

		InputStream stream;
		String offer1 = "offerings/amazon_c1_xlarge.yaml";
		String offer2 = "offerings/platform_offerings_test.yaml";
		
		stream = this.getClass().getResourceAsStream(offer1);
		if(stream == null)
			throw new NullPointerException("Cannot find resource file " + offer1);
        offerings.readFile(new InputStreamReader(stream));
		
		stream = this.getClass().getResourceAsStream(offer2);
		if(stream == null)
			throw new NullPointerException("Cannot find resource file " + offer2);
        offerings.readFile(new InputStreamReader(stream));
    }

	
	
    public IToscaEnvironment getOfferings(){
        return  offerings;
    }
}
