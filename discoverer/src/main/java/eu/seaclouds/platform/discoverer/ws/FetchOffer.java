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
import java.io.IOException;

/* std */
import java.util.Iterator;


/* json parser */
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
	
	
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		/* getting ids */
		Iterator<String> offerIds = this.core.enumerateOffers();
		
		/* wrapping the result in a json array */
		JSONObject responseData = new JSONObject();
		int offerIndex = 0;
		while( offerIds.hasNext() ) {
			String currentOfferId = offerIds.next();
			responseData.put("#" + offerIndex, currentOfferId);
			offerIndex++;
		}
		
		/* returning the json */
		response.getWriter().print(responseData);
	}

}
