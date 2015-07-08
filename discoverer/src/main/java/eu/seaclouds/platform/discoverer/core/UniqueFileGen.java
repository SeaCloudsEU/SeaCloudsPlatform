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

package eu.seaclouds.platform.discoverer.core;

import java.io.*;


/**
 * This class is responsible for managing a directory of uniquely-identified files among
 * multiple, independent sessions. Each file will be assigned a pseudo-random unique ID,
 * taking into account all the files that were previously stored in the directory the same
 * way.
 * Within SeaClouds, this class implements the Discoverer Cloud Offering Repository, where
 * each offering is stored into a TOSCA file. The ID of the offering is used as baseline
 * name of the file corresponding to the offering itself.
 */
public class UniqueFileGen {
	/* vars */
	private String prefix;
	private String suffix;
	private String sCurrentDirectory;
	private File currentDirectory;
	
	
	
	/* ************************************** */
	/* **          PRIVATE UTILS           ** */
	/* ************************************** */

	private static boolean allowedChar(char ch) {
		if(ch >= 'a' && ch <= 'z') return true; // lowercase chars. allowed
		if(ch >= 'A' && ch <= 'Z') return true; // uppercase chars. allowed
		if(ch >= '0' && ch <= '9') return true; // digits from 0 to 9 allowed
		if(ch == '_') return true;              // underscore allowed
		return false;
	}
	
	
	
	private static String sanitizePrefix(String prefix) {
		/* checking null */
		if(prefix == null)
			throw new NullPointerException("Parameter \"prefix\" cannot be null.");
		
		/* checking length */
		prefix = prefix.trim();
		if(prefix.length() == 0)
			throw new IllegalArgumentException("Parameter \"prefix\" cannot be the empty string.");
		
		/* checking valid chars */
		int N = prefix.length();
		for(int i=0; i<N; i++) {
			if( allowedChar(prefix.charAt(i)) == false ) {
				String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
				throw new IllegalArgumentException("Parameter \"prefix\" cannot have the char. '"
												   + prefix.charAt(i) + "'. Allowed chars. are "
												   + allowedChars);
			}
		}
		
		/* returning sanitized prefix */
		return prefix;
	}
	
	
	
	private static String sanitizeSuffix(String suffix) {
		/* checking null */
		if(suffix == null)
			throw new NullPointerException("Parameter \"suffix\" cannot be null.");
		
		/* checking length */
		suffix = suffix.trim();
		if(suffix.length() < 2)
			throw new IllegalArgumentException("Parameter \"suffix\" is not a valid suffix.");
		
		/* expecting extension */
		if( suffix.charAt(0) != '.' )
			throw new IllegalArgumentException("Parameter \"suffix\" is not a valid suffix.");
		
		/* checking valid chars */
		int N = suffix.length();
		for(int i=1; i<N; i++) {
			if( allowedChar(suffix.charAt(i)) == false ) {
				String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
				throw new IllegalArgumentException("Parameter \"suffix\" cannot have the char. '"
						+ suffix.charAt(i) + "'. Allowed chars. are " + allowedChars);
			}
		}
		
		/* returning sanitized suffix */
		return suffix;
	}
	
	
	
	/* ************************************** */
	/* **              C.TOR               ** */
	/* ************************************** */

	public UniqueFileGen(String prefix, String suffix, String dir) {
		/* input consistency check */
		if(dir == null) throw new NullPointerException("Parameter \"dir\" cannot be null.");
		this.prefix = sanitizePrefix(prefix);
		this.suffix = sanitizeSuffix(suffix);
		
		/* filesystem check */
		this.currentDirectory = new File(dir);
		if( !this.currentDirectory.exists() )
			this.currentDirectory.mkdir();
		
		if(this.currentDirectory.isDirectory() == false)
			throw new IllegalArgumentException("Parameter \"dir\" must point to a directory: \""
					+ this.currentDirectory.getAbsolutePath() + "\" is not a directory.");
		
		/* all good */
		this.sCurrentDirectory = currentDirectory.getAbsolutePath() + "/";
	}
	
	
	
	/* ************************************** */
	/* **          PUBLIC METHODS          ** */
	/* ************************************** */
	
	public String getWorkingDirectory() {
		return this.sCurrentDirectory;
	}
	
	
	
	public String getPrefix() {
		return this.prefix;
	}
	
	
	
	public String getSuffix() {
		return this.suffix;
	}



	public File getUniqueFile() throws IOException {
		return File.createTempFile(prefix, suffix, currentDirectory);
	}
	
	
	
	public String extractUniqueCode(String cleanFileName) {
		int startIndex = this.prefix.length();
		int endIndex = cleanFileName.indexOf(this.suffix);
		return cleanFileName.substring(startIndex, endIndex);
	}
}

