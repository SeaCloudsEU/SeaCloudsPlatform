/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.seaclouds.platform.planner.core.application.topology.modifier.relation;

public class PhpUriStringBuilder {

    public static String buildConnectionString(String targetId, String dbName, String dbUSer, String dbPass) {
        return "$brooklyn:formatString(\"mysql://%s:%s@%s:%s/%s\", " +
                "\"" + dbUSer + "\", " +
                "\"" + dbPass + "\", " +
                "component(\"" + targetId + "\").attributeWhenReady(\"host.address\"), " +
                "component(\"" + targetId + "\").attributeWhenReady(\"mysql.port\"), " +
                "\"" + dbName + "\")";

    }
}
