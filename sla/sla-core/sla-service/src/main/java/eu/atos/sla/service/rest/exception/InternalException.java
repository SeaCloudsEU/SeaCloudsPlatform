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
package eu.atos.sla.service.rest.exception;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class InternalException extends Exception implements Serializable {
	private static final long serialVersionUID = 3056566523068112767L;

	public InternalException(String s, Throwable t){
		super(s + " - " +serialize(t));
	}

	static private String serialize(Throwable t){
		StringWriter errors = new StringWriter();
		t.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	
	public InternalException(String msg) {
        super(msg);
    }
}