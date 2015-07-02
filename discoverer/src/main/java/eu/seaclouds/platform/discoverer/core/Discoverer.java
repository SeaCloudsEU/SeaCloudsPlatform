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

/* std imports */
import java.util.Iterator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.lang.Exception;
import java.io.FileNotFoundException;
import java.io.IOException;

/* a4c */
import alien4cloud.tosca.parser.ParsingException;


public class Discoverer {
	/* singleton */
	private static final Discoverer singleInstance = new Discoverer();
	public static Discoverer instance() { return singleInstance; }
	
	/* vars */
	private UniqueFileGen ufg; // offerings ID generator
	
	
	
	/* *************************************************************** */
	/* **                     C.TOR (private)                       ** */
	/* *************************************************************** */
	
	private Discoverer() {
		String prefix = "offer_";
		String suffix = ".yaml";
		// String dir = System.getProperty("user.home") + "/offerings_repo";
		String dir = "/Users/mat/offerings_repo";
		this.ufg = new UniqueFileGen(prefix, suffix, dir);
	}
	
	
	
	
	/* *************************************************************** */
	/* **                      PRIVATE UTILS                        ** */
	/* *************************************************************** */
	
	private Offering fetch_helper(String cloudOfferingId)
			throws FileNotFoundException, IOException, ParsingException {
		/* input check */
		if(cloudOfferingId == null)
			throw new NullPointerException("The parameter \"cloudOfferingId\" cannot be null.");
		
		/* heading to the file corresponding to the offering id */
		String offeringFileName = ufg.getWorkingDirectory()
				+ ufg.getPrefix() + cloudOfferingId + ufg.getSuffix();
				
		/* parsing tosca file */
		Offering ret = Offering.fromToscaFile(offeringFileName);
		ret.assignId(cloudOfferingId);
		return ret;
	}
	
	
	
	private String addOffer_helper(Offering o)
			throws IOException, FileNotFoundException {
		/* creating the new unique file to store the offering */
		File offeringFile = ufg.getUniqueFile();
		
		/* obtaining the ID assigned to the offering */
		String uniqueFileName = offeringFile.getName();
		String offeringId = ufg.extractUniqueCode(uniqueFileName);
		
		/* conversion from SOM to plain TOSCA */
		String toscaContent = o.toTosca();
		
		/* flushing the tosca content into the file */
		FileOutputStream fos = new FileOutputStream(offeringFile);
		fos.write(toscaContent.getBytes());
		fos.close();
		
		/* returning the ID of the added offer */
		return offeringId;
	}
	
	
	
	/* *************************************************************** */
	/* **                   INTERFACE IMPLEMENTATION                ** */
	/* *************************************************************** */
	
	
	/**
	 * Reads an offering from the local repository.
	 * @param cloudOfferingId The ID of the offering to read.
	 * @return The <code>Offering</code> object instance for the fetch'ed ID.
	 */
	public Offering fetch(String cloudOfferingId) {
		Offering ret;
		try { ret = fetch_helper(cloudOfferingId); }
		catch(Exception ex) {
			ex.printStackTrace();
			ret = null;
		}
		return ret;
	}
	
	
	
	/**
	 * Inserts a new offering in the local repository.
	 * @param o The <code>Offering</code> object instance representing the new offering to insert.
	 * @return the ID assigned to the newly-inserted offering.
	 */
	public String addOffer(Offering o) {
		String ret;
		try { ret = addOffer_helper(o); }
		catch(Exception ex) {
			ex.printStackTrace();
			ret = null;
		}
		return ret;
	}

	
	
	/**
	 * Removes an offering from the repository.
	 * @param cloudOfferingId The ID of the offering to remove.
	 * @return <code>true</code> in case of successful removal;
	 * <code>false</code> otherwise.
	 */
	public boolean removeOffer(String cloudOfferingId) {
		/* input check */
		if( !Offering.validateOfferingId(cloudOfferingId) )
			return false;
		
		/* elimination */
		String offeringFileName = ufg.getWorkingDirectory() + ufg.getPrefix()
				+ cloudOfferingId + ufg.getSuffix();
		File offeringFile = new File(offeringFileName);
		return offeringFile.delete();
	}
	
	
	
	/**
	 * Gets an iterator for the content of the offering repository.
	 * @return Iterator of cloud offering IDs that can be used as argument of
	 * <code>fetch</code> method.
	 */
	public Iterator<String> enumerateOffers() {
		/* Getting the list of all the .yaml files */
		File wd = new File(ufg.getWorkingDirectory());
		File[] files = wd.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".yaml");
			}
		} );
		
		/* creating and returning the iterator */
		return new OfferingRepoIterator(files);
	}
	
	
	
	/* implementation of the iterator */
	private class OfferingRepoIterator implements Iterator<String> {
		private File[] files;
		private int count;
		private int current;
		
	
		/* c.tor */
		OfferingRepoIterator(File[] files) {
			this.files = files;
			this.count = this.files.length;
			this.current = 0;
		}
	
		
		@Override
		public boolean hasNext() {
			return (current < count);
		}

		
		@Override
		public String next() {
			/* getting File */
			File f = files[current];
			current++;
			
			/* getting file name and converting to offering ID*/
			String fileName = f.getName();
			String offeringId = ufg.extractUniqueCode(fileName);
			
			/* returning the offering ID */
			return offeringId;
		}
		
	}
	
}

