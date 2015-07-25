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
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.lang.Exception;

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
		String dir = System.getProperty("user.home") + "/offerings_repo";
		this.ufg = new UniqueFileGen(prefix, suffix, dir);
	}
	


	/* *************************************************************** */
	/* **                       PUBLIC UTILS                        ** */
	/* *************************************************************** */

	public String getWorkingDirectory() {
		return ufg.getWorkingDirectory();
	}


	
	/* *************************************************************** */
	/* **                      PRIVATE UTILS                        ** */
	/* *************************************************************** */
	
	private Offering fetch_helper(String cloudOfferingId)
			throws IOException, ParsingException {
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
	
	
	
	private String addOffer_helper(Offering o) throws IOException {
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

		/* creating the date file */
		String dateFileName = ufg.getWorkingDirectory() + offeringId + ".date";
		fos = new FileOutputStream(dateFileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(Calendar.getInstance().getTime());
		oos.close();
		
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
		
		/* elimination of the offering*/
		String offeringFileName = ufg.getWorkingDirectory() + ufg.getPrefix()
				+ cloudOfferingId + ufg.getSuffix();
		File offeringFile = new File(offeringFileName);
		boolean ret = offeringFile.delete();

		/* elimination of the date */
		String dateFileName = ufg.getWorkingDirectory() + cloudOfferingId + ".date";
		File dateFile = new File(dateFileName);
		if(dateFile.exists())
			dateFile.delete();
		else
			System.err.println("WARNING: The offering " + cloudOfferingId
					+ " did not have a date file.");

		/* return the status */
		return ret;
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



	public Date getDate(String cloudOfferingId) {
		if( !Offering.validateOfferingId(cloudOfferingId) )
			throw new IllegalArgumentException("Invalid offering ID: " + cloudOfferingId);

		/* loading insertion date */
		try {
			String dateFileName = getWorkingDirectory() + cloudOfferingId + ".date";
			FileInputStream fis = new FileInputStream(dateFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Date ret = (Date) ois.readObject();
			return ret;
		} catch(IOException | ClassNotFoundException ex) {
			/* something is wrong */
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
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

		@Override
		public void remove() {

		}
		
	}
	
}

