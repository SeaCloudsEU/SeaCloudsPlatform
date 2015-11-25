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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OfferingManager {

    /* vars */
    private UniqueFileGen ufg;

    public HashMap<String, String> offeringNameToOfferingId =  new HashMap<>();
    /* name of the Offering file containing all node templates */
    private String singleOfferingFileId = "all";

    /* *************************************************************** */
    /* **                     subparts initialization               ** */
    /* *************************************************************** */
    public OfferingManager(String repositoryPath) {
        String prefix = "offer_",
                offeringSuffix = ".yaml",
                metaSuffix = ".json";

        this.ufg = new UniqueFileGen(prefix, offeringSuffix, metaSuffix, repositoryPath); // offerings ID generator
    }

    public File getOfferingDirectory() {
        return this.ufg.getOfferingDirectory();
    }

    public File getMetaDirectory() {
        return this.ufg.getMetaDirectory();
    }

    public String getOfferingId(String offeringName) {
        return offeringNameToOfferingId.get(offeringName);
    }

    /**
     * Removes all the offerings in the repository
     */
    public void emptyRepository() {
        File offeringDirectory =this.getOfferingDirectory();
        File metaDirectory = this.getMetaDirectory();

        /* list of all offerings */
        File[] files = offeringDirectory.listFiles();

        for (File file : files) {
            file.delete();
        }

        files = metaDirectory.listFiles();

        for (File file : files) {
            file.delete();
        }
    }

    /**
     * Get the list of all offering ids
     *
     * @return the list of all offering ids
     */
    public Collection<String> getAllOfferingIds() {
        return offeringNameToOfferingId.values();
    }

    /**
     * Get an offering
     *
     * @param offeringId the id of the offering
     * @return the offering identified by offeringId
     */
    public Offering getOffering(String offeringId) {
        /* input check */
        if(offeringId == null)
            throw new NullPointerException("The parameter \"cloudOfferingId\" cannot be null.");

        /* heading to the file corresponding to the offering id */
        String metaFileName = ufg.getMetaDirectory().getAbsolutePath()
                + "/" + ufg.getPrefix() + offeringId + ufg.getMetaSuffix();

        String offeringMeta;

        /* Retrieves the meta JSON of the offering specified */
        try {
            offeringMeta = new String(Files.readAllBytes(Paths.get(metaFileName)));
        } catch (IOException e) {
            return null;
        }

        return Offering.fromJSON(offeringMeta);
    }

    /**
     * Add a new offering in the repository (by creating the the YAML file
     * containing the TOSCA and the JSON file containing meta information)
     *
     * @param offering the Offering to add
     * @return the id of the added Offering
     */
    public String addOffering(Offering offering) {
        String ret;
        try {
            ArrayList<File> offeringAndMetaFiles = ufg.getUniqueFile();

            if (offeringAndMetaFiles.size() == 0) {
                return null;
            }

            File metaFile = offeringAndMetaFiles.get(0);
            File offeringFile = offeringAndMetaFiles.get(1);

            /* obtaining the ID assigned to the offering */
            String uniqueFileName = offeringFile.getName();
            String offeringId = ufg.extractUniqueCode(uniqueFileName);

            /* flushing the tosca content into the file */
            FileOutputStream fos = new FileOutputStream(offeringFile);
            fos.write(offering.toTosca().getBytes());
            fos.close();

            /* now that the offeringId is know it is possible to set it */
            offering.setId(offeringId);
            /* it is also possible to set the path of the offering file */
            offering.setOfferingPath(offeringFile.getAbsolutePath());

            /* flushing the meta information into the meta file */
            fos = new FileOutputStream(metaFile);
            fos.write(offering.toJSON().getBytes());
            fos.close();

            this.offeringNameToOfferingId.put(offering.getName(), offeringId);

            /* returning the ID of the added offer */
            ret = offeringId;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            ret = null;
        }

        return ret;
    }

    /**
     * Remove an offering
     *
     * @param offeringId the id of the offering to remove
     * @return
     */
    public boolean removeOffering(String offeringId) {
        /* elimination of the offering*/
        String offeringFileName = ufg.getOfferingDirectory().getAbsolutePath() +
                "/" + ufg.getPrefix() + offeringId + ufg.getOfferingSuffix();

        String metaFileName = ufg.getMetaDirectory().getAbsolutePath() +
                "/" + ufg.getPrefix() + offeringId + ufg.getMetaSuffix();

        File offeringFile = new File(offeringFileName);
        File metaFile = new File(metaFileName);

        boolean ret = true;

        /* the offering file containing TOSCA is removed */
        if (offeringFile.exists()) {
            ret = offeringFile.delete();
        }

        /* then we remove the meta file (containing information about the offering) */
        if (metaFile.exists()) {
            metaFile.delete();
        }

        String offeringName = null;

        for (String name : offeringNameToOfferingId.keySet()) {
            if (offeringNameToOfferingId.get(name).equals(offeringId)) {
                offeringName = name;
                break;
            }
        }

        if (offeringName != null)
            offeringNameToOfferingId.remove(offeringName);

        /* return the status */
        return ret;
    }

    /**
     * Initialize the list of offerings known by the discoverer
     *
     */
    public void initializeOfferings() {
        File metaDirectory = ufg.getMetaDirectory();

        /* list of all offerings */
        File[] files = metaDirectory.listFiles();

        for (File file : files) {

            // in this case it is a date file (useless for this task)
            if (file.getName().startsWith("offer_") == false)
                continue;

            String offeringId = this.ufg.extractUniqueCode(file.getName());

            try {
                String json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                Offering offering = Offering.fromJSON(json);
                offeringNameToOfferingId.put(offering.getName(), offering.getId());

            } catch (Exception e) {
                this.removeOffering(offeringId);
            }
        }
    }

    /**
     * Generates a single offering file containing all node templates fetched
     *
     * @param offeringNodeTemplates node templates to write on file
     */
    public void generateSingleOffering(String offeringNodeTemplates) {
        String offeringFileName = ufg.getOfferingDirectory().getAbsolutePath() +
                "/" + ufg.getPrefix() + this.singleOfferingFileId + ufg.getOfferingSuffix();

        File offeringFile = new File(offeringFileName);

        /* if the file already exists it is first deleted */
        if (offeringFile.exists()) {
            offeringFile.delete();
        }

        /* then node templates are written on that file */
        try {
            FileOutputStream fos = new FileOutputStream(offeringFile);
            fos.write(offeringNodeTemplates.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSingleOffering() {
        String offeringFileName = ufg.getOfferingDirectory().getAbsolutePath() +
                "/" + ufg.getPrefix() + this.singleOfferingFileId + ufg.getOfferingSuffix();

        File offeringFile = new File(offeringFileName);
        String ret = null;

        if (offeringFile.exists()) {
            try {
                ret = new String(Files.readAllBytes(Paths.get(offeringFileName)));
            } catch (IOException e) { }
        }

        return ret;
    }

    public void initializeFromRemote(String remoteInitializationPath) {
        try {
            URL url = new URL(remoteInitializationPath);
            ZipInputStream zin = new ZipInputStream(url.openStream());
            ZipEntry zipEntry = null;

            File metaDirectory = this.getMetaDirectory();
            File offeringDirectory = this.getOfferingDirectory();

            while ((zipEntry = zin.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) { /* Taking only files */
                    String filePath = zipEntry.getName();
                    String fileName = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
                    if (filePath.startsWith("meta_directory/")) { /* meta file (.json) */
                        this.extractFile(zin, fileName, metaDirectory);
                    } else if (filePath.startsWith("offering_directory/")) { /* offering file (.yaml) */
                        this.extractFile(zin, fileName, offeringDirectory);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractFile(ZipInputStream zin, String fileName, File outdir) {
        try {
            OutputStream out = new FileOutputStream(outdir.getAbsolutePath() + File.separator + fileName);

            byte[] buffer = new byte[8192];
            int len;
            while ((len = zin.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

