/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
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

package eu.seaclouds.platform.dashboard.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Creates a GET HTTP request to another URL, by using Apache HttpClient.
 */
public class HttpGetRequestBuilder extends HttpRequestBuilder {

    static Logger log = LoggerFactory.getLogger(HttpGetRequestBuilder.class);


    HttpGet requestBase;

    @Override
    public String build() throws IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(scheme);
        if (host != null && path != null){
            uriBuilder.setHost(host);
            uriBuilder.setPath(path);
            if (!params.isEmpty()) {
                uriBuilder.setParameters(params);
            }
        }
        requestBase = new HttpGet(uriBuilder.build());

        for(NameValuePair header : super.headers){
            requestBase.addHeader(header.getName(), header.getValue());
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            return httpClient.execute(requestBase, responseHandler, context);
        }
    }

}
