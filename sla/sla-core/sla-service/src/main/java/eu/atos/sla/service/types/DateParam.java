/**
 * Copyright 2015 SeaClouds
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
package eu.atos.sla.service.types;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * http://programmers.stackexchange.com/questions/138391/should-i-use-the-date-type-in-jax-rs-pathparam
 * 
 * Be careful with the time zone. If the date is not interpreted with timezone (i.e. blablaZ+0000),
 * the date is parsed with local timezone, wich may not be the expected result. The timezone spec of SimpleDateFormat
 * is not iso8601 compliant, so a replacing could be made, or not using SimpleDateFormat at all, but 
 * javax.xml.bind.DatatypeConverter.parseDateTime("2010-01-01T12:00:00Z")
 * 
 */

public class DateParam {
	private static final Logger logger = LoggerFactory.getLogger(DateParam.class);
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private final Date date;

	public static DateParam valueOf(String dateStr) {
	
		return new DateParam(dateStr);
	}

	public static Date getDate(DateParam instance) {

		return instance == null? null : instance.getDate();
	}
	
	public DateParam(String dateStr) throws WebApplicationException {
		if ("".equals(dateStr)) {
			logger.debug("DateParam({})", dateStr);
			this.date = null;
			return;
		}
	
		
		
		try {
			/*
			 * SimpleDateFormat is not thread-safe. 
			 */
			synchronized (dateFormat) {
				date = dateFormat.parse(dateStr);
			}
			logger.debug("DateParam({}) = {}", dateStr, date);
		} catch (ParseException e) {
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity("Couldn't parse date string: " + e.getMessage())
					.build());
		}
	}

	public Date getDate() {
		return date;
	}
	
	@Override
	public String toString() {
		return String.format("DateParam[%s]", getDate());
	}
}
