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
package eu.atos.sla.service.rest.helpers.exception;

public class HelperException extends Exception {
	public enum Code{ DB_DELETED, PARSER, DB_EXIST, DB_MISSING, INTERNAL};
	Code code;
	String message;
	private static final long serialVersionUID = -6211381925574253221L;

	public HelperException(Code code, String message){
		this.code = code;
		this.message = message;
	}

	public Code getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
	
	
}
