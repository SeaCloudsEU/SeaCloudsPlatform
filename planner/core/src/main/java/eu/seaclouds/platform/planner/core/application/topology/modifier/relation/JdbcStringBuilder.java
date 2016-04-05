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

public class JdbcStringBuilder {

    private static final String JBOSS_SEPARATOR = "\\\\";
    private static final String DEFAULT_SEPARATOR =  "";

    public static String buildConnectionString(String targetId, String sourceNodeType, String dbName, String dbUSer, String dbPass) {
        return "$brooklyn:formatString(\"jdbc:%s%s?user=%s" + getSeparator(sourceNodeType) + "&password=%s\", " +
                "component(\"" + targetId + "\").attributeWhenReady(\"datastore.url\"), " +
                "\"" + dbName + "\", " +
                "\"" + dbUSer + "\", " +
                "\"" + dbPass + "\")";
    }

    private static  String getSeparator(String sourceNodeType) {
        return sourceNodeType.contains("JBoss") ? JBOSS_SEPARATOR : DEFAULT_SEPARATOR;
    }
}
