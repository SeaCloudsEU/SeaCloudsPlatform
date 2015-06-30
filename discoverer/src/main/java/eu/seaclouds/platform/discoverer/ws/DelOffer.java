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

/**
 * 
 * @author Mattia Buccarella
 *
 */

@SuppressWarnings("serial")
public class DelOffer extends HttpServlet {
	/* vars */
	private Discoverer core;
	
	
	/* *********************************************************** */
	/* *****                    servlet                      ***** */
	/* *********************************************************** */
	
	public void init() {
		this.core = Discoverer.instance();
	}
	
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		/* input */
		String cloudOfferingId = request.getParameter("offer_id");
		if(cloudOfferingId == null)
			return;
		
		/* removal */
		String ret;
		if( this.core.removeOffer(cloudOfferingId) ) ret = "Ok.";
		else ret = "Fail.";
		
		/* response */
		response.getWriter().println(ret);
	}

}
