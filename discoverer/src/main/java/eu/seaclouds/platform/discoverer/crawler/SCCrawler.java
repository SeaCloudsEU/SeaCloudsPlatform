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

package eu.seaclouds.platform.discoverer.crawler;

import eu.seaclouds.platform.discoverer.core.Discoverer;
import eu.seaclouds.platform.discoverer.core.Offering;

import java.util.HashMap;

public abstract class SCCrawler {

    protected static HashMap<String, String> externaltoSCTag;

    static {
        externaltoSCTag = new HashMap<>();

        /* runtimes */
        externaltoSCTag.put("java", "java");
        externaltoSCTag.put("go", "go");
        externaltoSCTag.put("node", "node");
        externaltoSCTag.put("php", "php");
        externaltoSCTag.put("python", "python");
        externaltoSCTag.put("ruby", "ruby");
        externaltoSCTag.put("scala", "scala");

        /* middlewares */
        externaltoSCTag.put("tomcat", "tomcat");
        externaltoSCTag.put("resin", "resin");
        externaltoSCTag.put("jetty", "jetty");
        externaltoSCTag.put("jboss", "jboss");
        externaltoSCTag.put("glassfish", "glassfish");


        /* datastore */
        externaltoSCTag.put("mysql", "mysql");
        externaltoSCTag.put("postgresql", "postgresql");
        externaltoSCTag.put("mongodb", "mongodb");
        externaltoSCTag.put("memcached", "memcached");
        externaltoSCTag.put("redis", "redis");
        externaltoSCTag.put("couchbase", "couchbase");
    }

    protected Discoverer discoverer;

    public abstract void crawl();

    public void addOffering(Offering offering) {
        discoverer.addOffering(offering);
    }
}
