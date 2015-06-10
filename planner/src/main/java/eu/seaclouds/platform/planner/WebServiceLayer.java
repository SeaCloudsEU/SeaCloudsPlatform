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

package eu.seaclouds.platform.planner;

/* old leo's parser */
import seaclouds.utils.toscamodel.*;

/* servlet */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/* json parser */
import org.json.simple.parser.*;
import org.json.simple.*;

/* Map */
import java.util.*;



/*
 * Created by Mattia Buccarella (UPI).
 *
 */

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


	/* *********************************************************** */
	/* *****                  utilities                      ***** */
	/* *********************************************************** */


    /**
     * Returns the number of seconds since the web service started.
     **/
    private int getUptime() {
        Calendar currentTime = Calendar.getInstance();
        long currentTimeInMillis = currentTime.getTimeInMillis();
        return (int)((currentTimeInMillis - startupMillis)/1000);
    }



    /**
     * Converts seconds in days
     **/
    private static int getNumberOfDays(int seconds) {
        return (((seconds/60)/60)/24);
    }


    /**
     * Returns one IToscaEnvironment as String.
     * NOTE: It is useful to have a string rather than a file because
     *       we are returning some IToscaEnvironment to the dashboard
     *       as a string wrapped into a json array.
     *       This methos is invoked at the very end of the execution
     *       of the web service.
     **/

    private static String iteToString(IToscaEnvironment ite) {
		try {
			StringWriter sw = new StringWriter();
			ite.writeFile(sw);
			sw.flush();
			return sw.toString();
		} catch(NoSuchMethodError eee) {
			eee.printStackTrace();
			return eee.getMessage();
		}
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
        } catch(Exception pe) {
			/* error */
            out.println("");
            pe.printStackTrace(out);
            return;
        }

		/* putting on stream and parsing */
        StringReader sr = new StringReader(strAam);
        IToscaEnvironment aam = Tosca.newEnvironment();
        aam.readFile(sr);

		/* moving up to the core layer */
        Planner p = new Planner();
        List<IToscaEnvironment> optOffers = p.plan(aam);

		/* wrapping the result in a json array */
        JSONObject responseData = new JSONObject();
        int offerIndex = 0;
        for(IToscaEnvironment currentOffer : optOffers) {
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