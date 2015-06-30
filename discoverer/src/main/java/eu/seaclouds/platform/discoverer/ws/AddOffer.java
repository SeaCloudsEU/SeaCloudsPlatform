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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* core */
import eu.seaclouds.platform.discoverer.core.Discoverer;
import eu.seaclouds.platform.discoverer.core.Offering;

/* io */
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * 
 * @author Mattia Buccarella
 *
 */

@SuppressWarnings("serial")
public class AddOffer extends HttpServlet {
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
		String postPayload = request.getParameter("tosca_in");
		if(postPayload == null)
			return;
		
		Offering newOffer = Offering.fromTosca(postPayload);
		String offerId = this.core.addOffer(newOffer);
		response.getWriter().println(offerId);
	}
	
	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		/* manual feeder within the AddOffer servlet itself */
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		/* obtaining the web page */
		ServletContext servletContext = this.getServletContext();
		InputStream is = servletContext.getResourceAsStream("/resources/static_addoffer.html");
		Scanner sc = new Scanner(is);
		while( sc.hasNext() ) {
			String line = sc.next();
			out.println(line);
		}
		
		/* finishing up */
		sc.close();
		return;
	}

}
