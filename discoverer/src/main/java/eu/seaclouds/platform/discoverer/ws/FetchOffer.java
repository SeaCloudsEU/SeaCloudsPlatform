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

package eu.seaclouds.platform.discoverer.ws;

/* servlet */
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/* core */
import eu.seaclouds.platform.discoverer.core.Discoverer;


/* io */
import java.io.*;

/* std */
import java.util.Iterator;


/* json parser */
import eu.seaclouds.platform.discoverer.core.Offering;
import org.json.simple.*;

@SuppressWarnings("serial")
public class FetchOffer extends HttpServlet {
	/* vars */
	private Discoverer core;
	
	
	/* *********************************************************** */
	/* *****                    servlet                      ***** */
	/* *********************************************************** */
	
	public void init() {
		this.core = Discoverer.instance();
	}



	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		/* collecting all the ids within the repository */
		JSONArray ids = new JSONArray();
		Iterator<String> it = core.enumerateOffers();
		while( it.hasNext() ) {
			String cid = it.next();
			ids.add(cid);
		}

		/* response to the caller */
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write(ids.toString());
		out.close();
		return;
	}


	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		/* response setup */
		response.setContentType("application/json");
		JSONObject res = new JSONObject();
		PrintWriter out = response.getWriter();

		/* input check */
		String offerId = request.getParameter("oid");
		if(offerId == null || Offering.validateOfferingId(offerId) == false) {
			res.put("code", "Meh.");
			res.put("errormessage", "Invalid Offering ID: " + ((offerId == null) ? "null" : offerId));
			out.write(res.toString());
			out.close();
			return;
		}

		/* fetching the offering */
		Offering offering = this.core.fetch(offerId);
		if(offering == null) {
			res.put("code", "Meh.");
			res.put("errormessage", "Unable to fetch selected offering.");
		} else {
			res.put("code", "WOOT!");
			res.put(offerId, offering.toTosca());
		}

		/* sending response to the caller */
		out.write(res.toString());
		out.close();
		return;
	}

}
