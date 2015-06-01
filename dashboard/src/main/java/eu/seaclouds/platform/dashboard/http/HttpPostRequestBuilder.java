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

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Creates a POST HTTP request to another URL, by using Apache HttpClient.
 */
public class HttpPostRequestBuilder extends HttpRequestBuilder {

    static Logger log = LoggerFactory.getLogger(HttpPostRequestBuilder.class);
    
    HttpPost requestBase;
    private HttpEntity entity;
    private boolean isMultipart;

    public HttpPostRequestBuilder uri(String uri) {
        this.requestBase = new HttpPost(uri);
        return this;
    }

    public HttpPostRequestBuilder multipartPostRequest(boolean isMultipart) {
        this.isMultipart = isMultipart;
        return this;
    }

    public HttpPostRequestBuilder entity(HttpEntity entity){
        this.entity = entity;
        return this;
    }

    public String build() throws IOException, URISyntaxException {
        if (!params.isEmpty() && entity == null) {
            if (!isMultipart) {
                this.entity = new UrlEncodedFormEntity(params);
            } else {
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                for (NameValuePair pair : params) {
                    entityBuilder.addTextBody(pair.getName(), pair.getValue());
                }

                this.entity = entityBuilder.build();
            }
        }

        URI uri = new URIBuilder()
                .setHost(host)
                .setPath(path)
                .setScheme(scheme)
                .build();

        this.requestBase = new HttpPost(uri);

        for(NameValuePair header : super.headers){
            requestBase.addHeader(header.getName(), header.getValue());
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            if (this.entity != null) {
                this.requestBase.setEntity(this.entity);
            }
            return httpClient.execute(requestBase, responseHandler, context);
        }
    }

}
