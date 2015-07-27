package eu.seaclouds.platform.planner.core;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class PlannerClient {

    public static void main(String[] args) throws IOException {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("aam", "test http healper"));

        String content = new HttpHelper("http://localhost:8080").getRequest("/plan", params);
        System.out.println(content);

        content = new HttpHelper("http://localhost:8080").postRequest("/plan", params);
        System.out.println("POST!!!! " + content);

//        CloseableHttpClient httpclient = HttpClients.createDefault();
//
//
//        HttpGet hhtpGet = new HttpGet("http://localhost:8080/plan?aam=qwerty");
//
//
//        CloseableHttpResponse response2 = httpclient.execute(hhtpGet);
//
////        HttpPost httpPost = new HttpPost("http://targethost/login");
////        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
////        nvps.add(new BasicNameValuePair("username", "vip"));
////        nvps.add(new BasicNameValuePair("password", "secret"));
////        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
////        CloseableHttpResponse response2 = httpclient.execute(httpPost);
//
//        try {
//            //System.out.println(response2.getStatusLine());
//            HttpEntity entity2 = response2.getEntity();
//            // do something useful with the response body
//            // and ensure it is fully consumed
//
//            String content = new Scanner(entity2.getContent()).useDelimiter("\\Z").next();
//            System.out.println(content);
//
//            EntityUtils.consume(entity2);
//        } finally {
//            response2.close();
//        }
    }
}
