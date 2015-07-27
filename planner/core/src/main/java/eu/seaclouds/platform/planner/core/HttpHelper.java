package eu.seaclouds.platform.planner.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
public class HttpHelper {
    private static CloseableHttpClient httpclient;
    private static String serviceURL;
    private ObjectMapper mapper;

    /**
     *
     * @param serviceURL
     */
    public HttpHelper(String serviceURL) {
        this.httpclient = HttpClients.createDefault();
        this.serviceURL = serviceURL;
        this.mapper = new ObjectMapper();
    }

    /**
     *
     * @param restPath
     * @param params
     * @return
     */
    public String getRequest(String restPath, List<NameValuePair> params){

        HttpGet httpGet = new HttpGet(prepareRequestURL(restPath, params));

        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = new Scanner(entity.getContent()).useDelimiter("\\Z").next();
            EntityUtils.consume(entity);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     *
     * @param restPath
     * @param params
     * @return
     */
    public String postRequest(String restPath, List<NameValuePair> params){
        HttpPost httpPost = new HttpPost(prepareRequestURL(restPath, new ArrayList<NameValuePair>()));
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = new Scanner(entity.getContent()).useDelimiter("\\Z").next();
            EntityUtils.consume(entity);
            return content;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private String prepareRequestURL(String restPath, List<NameValuePair> params){
        StringBuilder operationBuilder = new StringBuilder();
        operationBuilder.append(serviceURL);
        operationBuilder.append(restPath);



        if(params.size() > 0) {
            operationBuilder.append("?");
            URLCodec coded = new URLCodec();
            try {
                for(NameValuePair p: params)
                    operationBuilder.append(p.getName() + "=" + coded.encode(p.getValue()));

            } catch (EncoderException e) {
                e.printStackTrace();
                return null;
            }
        }
        return operationBuilder.toString();
    }

    /**
     *
     * @param jsonString
     * @param <T>
     * @return
     */
    public <T> T getObjectFromJson(String jsonString, Class<T> type){
        try {
            return (T) mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
