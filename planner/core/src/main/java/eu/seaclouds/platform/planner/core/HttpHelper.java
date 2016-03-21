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
package eu.seaclouds.platform.planner.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.seaclouds.planner.matchmaker.Pair;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HttpHelper {
    private CloseableHttpClient httpclient;
    private String serviceURL;
    private ObjectMapper mapper;
    static Logger log = LoggerFactory.getLogger(HttpHelper.class);

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
        log.info("Getting request for " + this.serviceURL + restPath);

        HttpGet httpGet = new HttpGet(prepareRequestURL(restPath, params));
        CloseableHttpResponse response = null;
        String content ="";
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            content = new Scanner(entity.getContent()).useDelimiter("\\Z").next();
            EntityUtils.consume(entity);
            log.info("Request executed succesfully");
        } catch (IOException e) {
            log.error("IOException", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("IOEXception", e);
            }
        }
        return content;
    }

    public String postInBody(String restPath, String bodyContent){
        log.info("Posting request for " + this.serviceURL + restPath);
        HttpPost post = new HttpPost(prepareRequestURL(restPath));
        String result = "";
        try {
            StringEntity entity = new StringEntity(bodyContent);
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
            result = EntityUtils.toString(response.getEntity());
        }catch (ClientProtocolException e) {
            log.error("ClientProtocolException", e);
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }

        return result;
    }

    /**
     *
     * @param restPath
     * @param params
     * @return
     */
    public Pair<String, String> postRequest(String restPath, List<NameValuePair> params){
        log.info("Posting request for " + this.serviceURL + restPath);
        HttpPost httpPost = new HttpPost(prepareRequestURL(restPath, new ArrayList<NameValuePair>()));
        CloseableHttpResponse response = null;
        String content = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();

            if(status == 200) {
                content = EntityUtils.toString(entity);
            }else{
                content =response.getStatusLine().getReasonPhrase();
            }

            EntityUtils.consume(entity);
            log.info("Post success");
            return new Pair<String, String>(String.valueOf(status), content);

        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException", e);
        } catch (ClientProtocolException e) {
            log.error("ClientProtocolException", e);
        } catch (IOException e) {
            log.error("IOException", e);
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("IOException", e);
            }
        }
        return new Pair<>("500", "Post Exception for: " + httpPost.toString());
    }

    public Pair<String, String> postRequestWithParams(String restPath, List<NameValuePair> params){
        log.info("Posting request for " + this.serviceURL + restPath);
        HttpPost httpPost = new HttpPost(prepareRequestURL(restPath, params));
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = "";
            int status = response.getStatusLine().getStatusCode();

            if(status == 200) {
                content = EntityUtils.toString(entity); //new Scanner(entity.getContent()).useDelimiter("\\Z").next();
            }else{
                content =response.getStatusLine().getReasonPhrase();
            }

            EntityUtils.consume(entity);
            log.info("Post success");
            return new Pair<String, String>(String.valueOf(status), content);

        } catch (UnsupportedEncodingException e) {
            log.error(e.getCause().getMessage(), e);
            return new Pair<String, String>("500", "Exception: " + e.getCause().getMessage());
        } catch (ClientProtocolException e) {
            log.error("ClientProtocolException");
            return new Pair<String, String>("500", "Exception: " + e.getCause().getMessage());
        } catch (IOException e) {
            log.error("IOException");
            return new Pair<String, String>("500", "Exception: " + e.getCause().getMessage());
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("IOException", e);
                return new Pair<String, String>("500", "Exception: " + e.getCause().getMessage());
            }
        }
    }

    private String prepareRequestURL(String restPath){
        return prepareRequestURL(restPath, new ArrayList<NameValuePair>());
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
                log.error(e.getCause().getMessage(), e);
                return "";
            }
        }
        return operationBuilder.toString();
    }
}
