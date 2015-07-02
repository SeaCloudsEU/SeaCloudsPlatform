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
package eu.seaclouds.platform.planner.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import seaclouds.utils.toscamodel.INamedEntity;
import seaclouds.utils.toscamodel.IToscaEnvironment;
import seaclouds.utils.toscamodel.Tosca;

/* servlet */
/* json parser */
/* Map */


public class WebServiceLayer extends HttpServlet {
    /* constants */
    public static final String POST_PAR = "aam_json";
    public static final String JSON_INPUT_ID = "yaml";

    /* vars */
    private JSONParser jsonParser;

    /* stats */
    private int numberOfPosts;
    private int numberOfServedPosts;

    /* startup */
    private Calendar rightNow;
    private Date startup;
    private long startupMillis;


    /**
     * Returns the number of seconds since the web service started.
     */
    private int getUptime() {
        Calendar currentTime = Calendar.getInstance();
        long currentTimeInMillis = currentTime.getTimeInMillis();
        return (int) ((currentTimeInMillis - startupMillis) / 1000);
    }


    /**
     * Converts seconds in days
     */
    private static int getNumberOfDays(int seconds) {
        return (((seconds / 60) / 60) / 24);
    }


    /**
     * Returns one IToscaEnvironment as String.
     * NOTE: It is useful to have a string rather than a file because
     * we are returning some IToscaEnvironment to the dashboard
     * as a string wrapped into a json array.
     * This methos is invoked at the very end of the execution
     * of the web service.
     */

    private static String iteToString(IToscaEnvironment ite) {
        StringWriter sw = new StringWriter();
        ite.writeFile(sw);
        sw.flush();
        return sw.toString();
    }


    /* *********************************************************** */
    /* *****                    c.tor                        ***** */
    /* *********************************************************** */

    public WebServiceLayer() throws ServletException {
    }



    /* *********************************************************** */
    /* *****                    servlet                      ***** */
    /* *********************************************************** */

    public void init() {
        this.jsonParser = new JSONParser();
        this.numberOfPosts = 0;
        this.numberOfServedPosts = 0;

        /* startup time */
        this.rightNow = Calendar.getInstance();
        this.startup = rightNow.getTime();
        this.startupMillis = rightNow.getTimeInMillis();
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        /* some stats */
        int uptime = getUptime();
        int nDays = getNumberOfDays(uptime);

        /* response */
        PrintWriter out = response.getWriter();
        out.println("Hello.");
        out.println("The planner has been running for about " + nDays + " days.");
        out.println(numberOfPosts + " POST requests arrived.");
        out.println(numberOfServedPosts + " POSTS have been served so far.");
        return;
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // NOTE: check if explicit multithreading needed

        /* stats */
        numberOfPosts = numberOfPosts + 1;

        /* the output stream */
        PrintWriter out = response.getWriter();

        /* getting the AAM data */
        String strAam = null;
        try {
            String postContent = request.getParameter(POST_PAR);
            JSONObject jsonData = (JSONObject) jsonParser.parse(postContent);
            strAam = (String) jsonData.get(JSON_INPUT_ID);
        } catch (Exception pe) {
            /* error */
            out.println("");
            pe.printStackTrace(out);
            return;
        }

        /* putting on stream and parsing */
        StringReader sr = new StringReader(strAam);
        IToscaEnvironment aam = Tosca.newEnvironment();
        aam.readFile(sr, true);

        Planner p = new Planner();

        List<IToscaEnvironment> optOffers = p.plan(aam);

        /* wrapping the result in a json array */
        JSONObject responseData = new JSONObject();
        int offerIndex = 0;
        for (IToscaEnvironment currentOffer : optOffers) {
            INamedEntity ee = (INamedEntity) currentOffer;
            responseData.put("offer_" + offerIndex, iteToString(currentOffer));
            offerIndex++;
        }

        /* response to the caller */
        out.print(responseData);

        /* stats */
        numberOfServedPosts = numberOfServedPosts + 1;
        return;
    }

}
