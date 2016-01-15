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
import java.io.IOException;
import java.util.ArrayList;


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
    private String offeringSuffix;
    private String metaSuffix;
    private File currentDirectory;

    private File offeringDirectory;
    private File metaDirectory;

    /* ************************************** */
    /* **              C.TOR               ** */
    /* ************************************** */

    public UniqueFileGen(String prefix, String offeringSuffix, String metaSuffix, String path) {

        /* input consistency check */
        if(path == null) throw new NullPointerException("Parameter \"dir\" cannot be null.");
        this.prefix = prefix;
        this.offeringSuffix = offeringSuffix;
        this.metaSuffix = metaSuffix;

        if (path.startsWith("~" + File.separator)) {
            path = System.getProperty("user.home") + path.substring(1);
        }

        /* filesystem check */
        this.currentDirectory = this.createDirectory(path);
        this.offeringDirectory = this.createDirectory(path + "/offering_directory");
        this.metaDirectory = this.createDirectory(path + "/meta_directory");
    }

    private File createDirectory(String directoryName) {
        File directory = new File(directoryName);
        if(!directory.exists())
            directory.mkdir();

        if(!directory.isDirectory())
            throw new IllegalArgumentException("Parameter \"dir\" must point to a directory: \""
                    + this.currentDirectory.getAbsolutePath() + "\" is not a directory.");

        return directory;
    }

    /* ************************************** */
    /* **          PUBLIC METHODS          ** */
    /* ************************************** */
    public File getOfferingDirectory() {
        return this.offeringDirectory;
    }

    public File getMetaDirectory() {
        return this.metaDirectory;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getOfferingSuffix() {
        return this.offeringSuffix;
    }

    public String getMetaSuffix() {
        return this.metaSuffix;
    }


    public ArrayList<File> getUniqueFile() throws IOException {
        ArrayList<File> files = new ArrayList<>(2);

        File offeringFile = File.createTempFile(prefix, offeringSuffix, offeringDirectory);
        String offeringId = this.extractUniqueCode(offeringFile.getName());

        File metaFile = new File(this.metaDirectory.getAbsolutePath() + "/" + prefix + offeringId + metaSuffix);

        if (metaFile.createNewFile()) {
            files.add(0, metaFile);
            files.add(1, offeringFile);
        } else {
            offeringFile.delete();
        }

        return files;
    }

    public String extractUniqueCode(String cleanFileName) {
        int startIndex = this.prefix.length();
        int endIndex = cleanFileName.indexOf(this.offeringSuffix);

        if (endIndex == -1)
            endIndex = cleanFileName.indexOf(this.metaSuffix);

        return cleanFileName.substring(startIndex, endIndex);
    }
}