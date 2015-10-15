/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.brooklyn.entity;



public class SourceNameResolver {

    public static String getNameOfRepositoryGitFromHttpsUrl(String url){
        String nameOfRepository="";
        nameOfRepository=url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));
        return nameOfRepository;
    }

    /**
     * Return the id of the resource pointed by the url without extension
     * E.g. http://example.com/resource.tar return resource
     * @param url
     * @return
     */

    public static String getIdOfTarballFromUrl(String url){
        String nameOfTarballResource=getTarballResourceNameFromUrl(url);
        return nameOfTarballResource.substring(0,nameOfTarballResource.lastIndexOf("."));
    }

    /**
     * Return the name of the tarball resource.
     * E.g. http://example.com/resource.tar return resource.tar
     * @param url
     * @return
     */
    public static String getTarballResourceNameFromUrl(String url) {
        String resourceName = url.substring(url.lastIndexOf('/') + 1);
        return resourceName;
    }





}
