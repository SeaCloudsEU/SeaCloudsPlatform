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
import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.swing.JOptionPane;


/**
 * 
 * @author Mattia Buccarella
 *
 */

@SuppressWarnings("serial")
public class CrawlerManager extends HttpServlet implements Runnable {
	/* vars */
	private static Thread backThread;
	
	/* *********************************************************** */
	/* *****                  back thread                    ***** */
	/* *********************************************************** */
	
	private void run_helper() {
		
		/* endless main loop */
		while(true)
		{
			// crawl
			// TODO
		}
		
	}
	
	
	@Override
	public void run() {
		
		try { run_helper(); }
		catch(Exception ex) { ex.printStackTrace();	}
		
	}
	
	
	/* *********************************************************** */
	/* *****                    servlet                      ***** */
	/* *********************************************************** */
	
	public void init() {
		backThread = new Thread(this);
		backThread.start();
	}	
	
	
	
	
}
