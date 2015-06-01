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

import com.beust.jcommander.internal.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class HttpRequestBuilderTest {
    
    static final String ECHO_JSON_HOST = "echo.jsontest.com";
    static final String ECHO_JSON_RESPONSE = "{\n" +
            "   \"one\": \"two\",\n" +
            "   \"key\": \"value\"\n" +
            "}\n";
    
    static final String HTTP_TEST_HOST = "httpbin.org";

    @Test
    public void testGetRequestWithEcho() {
        try {
            String response = new HttpGetRequestBuilder()
                    .host(ECHO_JSON_HOST)
                    .path("/key/value/one/two")
                    .build();

            Assert.assertEquals(response, ECHO_JSON_RESPONSE);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    @Test
    public void testGetRequest() {
        try {
            Map<String, String[]> params = Maps.newLinkedHashMap();
            params.put("key1", new String[]{"value1"});
            params.put("key2", new String[]{"value2"});

            String response = new HttpGetRequestBuilder()
                    .scheme("http")
                    .host(HTTP_TEST_HOST)
                    .path("/get")
                    .params(params)
                    .build();

            JsonObject root = new JsonParser().parse(response).getAsJsonObject();

            JsonObject jsonArgs = root.get("args").getAsJsonObject();
            Assert.assertEquals(jsonArgs.get("key1").getAsString(), "value1");
            Assert.assertEquals(jsonArgs.get("key2").getAsString(), "value2");

            Assert.assertEquals(root.get("url").getAsString(), 
                    "http://" + HTTP_TEST_HOST + "/get?key1=value1&key2=value2");

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testDeleteRequest() {
        try {
            Map<String, String[]> params = Maps.newLinkedHashMap();
            params.put("key1", new String[]{"value1"});
            params.put("key2", new String[]{"value2"});

            String response = new HttpDeleteRequestBuilder()
                    .scheme("http")
                    .host(HTTP_TEST_HOST)
                    .path("/delete")
                    .params(params)
                    .build();

            JsonObject root = new JsonParser().parse(response).getAsJsonObject();

            JsonObject jsonArgs = root.get("args").getAsJsonObject();
            Assert.assertEquals(jsonArgs.get("key1").getAsString(), "value1");
            Assert.assertEquals(jsonArgs.get("key2").getAsString(), "value2");
            
            Assert.assertEquals(root.get("url").getAsString(), "http://" + HTTP_TEST_HOST + "/delete?key1=value1&key2=value2") ;
            
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testPostRequest() {
        try {
            Map<String, String[]> params = Maps.newLinkedHashMap();
            params.put("key1", new String[]{"value1"});
            params.put("key2", new String[]{"value2"});

            String response = new HttpPostRequestBuilder()
                    .scheme("http")
                    .host(HTTP_TEST_HOST)
                    .path("/post")
                    .params(params)
                    .build();

            JsonObject root = new JsonParser().parse(response).getAsJsonObject();

            JsonObject jsonArgs = root.get("form").getAsJsonObject();
            Assert.assertEquals(jsonArgs.get("key1").getAsString(), "value1");
            Assert.assertEquals(jsonArgs.get("key2").getAsString(), "value2");

            Assert.assertEquals(root.get("url").getAsString(), "http://" + HTTP_TEST_HOST + "/post");

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    
}