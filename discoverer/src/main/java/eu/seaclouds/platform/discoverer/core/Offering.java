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
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/* a4c tosca parser */
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.tosca.model.ArchiveRoot;
import alien4cloud.tosca.parser.ParsingResult;
import alien4cloud.tosca.parser.ToscaParser;
import eu.seaclouds.common.tosca.ToscaParserSupplier;
import alien4cloud.tosca.parser.ParsingException;

/* tosca serializer */
import eu.seaclouds.common.tosca.ToscaSerializer;

public class Offering {
	/* vars */
	private String offeringId;
	private ParsingResult<ArchiveRoot> a4cOfferingObject; // wrapping

	/* static vars */
	private static ToscaParser parser = null;
	private static ToscaParserSupplier tps = null;
	

	private static void initToscaParser() {
		if( tps == null ) tps = new ToscaParserSupplier();
		if(parser == null) parser = tps.get();
	}
	
	
	
	/**
	 * It performs the validation of any offering ID passed as input.
	 * @param cloudOfferingId The offering ID to validate.
	 * @return <code>true</code> if <code>cloudOfferingId</code> is valid,
	 * <code>false</code> otherwise.
	 */
	public static boolean validateOfferingId(String cloudOfferingId) {
		/* input checks */
		if(cloudOfferingId == null)
			return false;
		if( cloudOfferingId.trim().length() != cloudOfferingId.length() )
			return false;
		if( cloudOfferingId.length() == 0 )
			return false;
		
		/* input check: we do NOT allow dots, slashes and backslashes */
		int n = cloudOfferingId.length();
		for(int i=0; i<n; i++) {
			char ch = cloudOfferingId.charAt(i);
			if( ch == '.' || ch == '/' || ch == '\\' )
				return false;
		}
		
		/* all good */
		return true;
	}
	
	
	
	/* c.tor */
	private Offering() {
		this.offeringId = null;
		this.a4cOfferingObject = null;
	}
	
	
	
	/**
	 * Creates an <code>Offering</code> object, starting from a TOSCA file
	 * representing it. The newly-created <code>Offering</code> does not have an ID.
	 * Use <code>assignID</code> to assign an offering ID to it.
	 * @param fileName The name of the TOSCA input file.
	 * @return An <code>Offering</code> instance corresponding to the TOSCA
	 * representation within the input file.
	 */
	public static Offering fromToscaFile(String fileName) throws IOException, ParsingException {
		/* input check */
		if(fileName == null)
			throw new NullPointerException("The file name cannot be null.");
		fileName = fileName.trim();
		if(fileName.length() == 0)
			throw new IllegalArgumentException("Invalid file name.");
		
		/* a4c parser */
		initToscaParser();

		/* creating offering object (SOM) */
		Offering newOffering = new Offering();
		Path filePath = Paths.get(fileName);
		newOffering.a4cOfferingObject = parser.parseFile(filePath);
		
		/* returning result (missing ID) */
		return newOffering;
	}
	
	
	
	/**
	 * Creates an <code>Offering</code> object starting from the TOSCA within a String.
	 * @param toscaPayload The TOSCA input String.
	 * @return Instance of <code>Offering</code>
	 */
	public static Offering fromTosca(String toscaPayload) throws IOException, ParsingException {
		/* input check */
		if( toscaPayload == null )
			throw new NullPointerException("Parameter toscaPayload cannot be null.");
		toscaPayload = toscaPayload.trim();
		if(toscaPayload.length() == 0)
			throw new IllegalArgumentException("Empty TOSCA is not a valid TOSCA.");
		
		/* creating temporary file for the parser */
		File tempFile = File.createTempFile("offeringTmp", null);
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
		bw.write(toscaPayload);
		bw.close();
		
		/* moving to fromToscaFile */
		Offering ret = fromToscaFile(tempFile.getAbsolutePath());
		tempFile.delete();
		
		/* return value, might be null in case of errors */
		return ret;
	}
	
	
	
	public String toTosca() {
		String ret = ToscaSerializer.toTOSCA(
				this.a4cOfferingObject.getResult().getTopology(), // topology
				"", // author
				"", // name
				""); // description
		return ret;
	}
	

	
	/**
	 * Extracts the TOSCA node_template from the Offering. Since the offerings only have
	 * one node_template, the result is the only <code>NodeTemplate</code> obtained by
	 * the parsing.
	 * @return The extracted <code>NodeTeplate</code>
	 */
	public NodeTemplate getNodeTemplate() {
		Iterator<NodeTemplate> it = this.a4cOfferingObject.getResult().getTopology()
				.getNodeTemplates().values().iterator();
		return it.next();
	}
	


	/**
	 * Assigns a unique ID to the Offering.
	 * @param uniqueId The ID to assign.
	 * @return <code>true</code> if the assignment is successful,
	 * <code>false</code> otherwise.
	 */
	public boolean assignId(String uniqueId) {
		/* input check */
		boolean validId = validateOfferingId(uniqueId);
		if( !validId )
			return false;
		
		/* assignment */
		this.offeringId = uniqueId;
		return true;
	}
	
	
	
	/**
	 * Gets the ID of the Offering.
	 * @return The ID of the Offering.
	 */
	public String getId() {
		if(this.offeringId == null)
			throw new NullPointerException("The offering has not been assigned any ID. " 
					+ "See Offering.assignId(String).");
		return this.offeringId;
	}



	/**
	 * Retrieves information about the date when the offering has been added to the local
	 * repository. Note that this method queries the local repository to retrieve the
	 * <code>Date</code> object: invoking the same method, on the same offering, but on
	 * different machine will produce different results.
	 * @return The <code>Date</code> instance representing the time when the offering has
	 * been added to the Discoverer's repository.
	 */
	public Date getDate() {
		if( this.offeringId == null )
			throw new NullPointerException("The offering has not been added to the repository.");

		/* loading insertion date */
		try {
			Discoverer d = Discoverer.instance();
			String dateFileName = d.getWorkingDirectory() + this.offeringId + ".date";
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



	/**
	 * Retrieves the name of the offering.
	 * @return The name of the offering.
	 */
	public String getName() {
		Map<String, NodeTemplate> nts = this.a4cOfferingObject.getResult()
				.getTopology().getNodeTemplates();
		String nodeTemplateName = nts.keySet().iterator().next();
		return nodeTemplateName;
	}

	/**
	 * Sanitizes a name by replacing every not alphanumeric character with '_'
	 *
	 * @param name the name of the offerings
	 * @return the sanitized name
	 */
	public static String sanitizeName(String name) {

		if (name  == null)
			throw new NullPointerException("Parameter name cannot be null");

		name = name.trim();

		if (name.length() == 0)
			throw new IllegalArgumentException("Parameter name cannot be empty");

		StringBuilder ret = new StringBuilder("");

		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (Character.isLetter(ch) || Character.isDigit(ch)) {
				ret.append(ch);
			} else {
				ret.append("_");
			}

		}

		return ret.toString();
	}
}
