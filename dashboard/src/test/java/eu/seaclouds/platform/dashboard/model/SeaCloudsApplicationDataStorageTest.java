/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.model;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class SeaCloudsApplicationDataStorageTest {
    private static final String TOSCA_DAM_FILE_PATH = "fixtures/tosca-dam.yml";
    private static final SeaCloudsApplicationDataStorage dataStore = SeaCloudsApplicationDataStorage.getInstance();
    private static final int INITIAL_DATASTORE_SIZE = 100;

    @BeforeMethod
    public void setUp() throws IOException {
        Yaml yamlParser = new Yaml();
        URL resource = Resources.getResource(TOSCA_DAM_FILE_PATH);
        Map toscaDamMap = (Map) yamlParser.load(FileUtils.openInputStream(new File(resource.getFile())));
        // Fill SeaCloudsApplicationDataStorage with empty applications with distinct SeaCloudsID
        for(int i = 0; i < INITIAL_DATASTORE_SIZE; i++){
            dataStore.addSeaCloudsApplicationData(new SeaCloudsApplicationData(toscaDamMap));
        }
    }

    @AfterMethod
    public void tearDown() throws Exception {
        // Clear SeaCloudsApplicationDataStorage
        dataStore.clearDataStore();
    }

    @Test
    public void testAddSeaCloudsApplicationData() throws IOException {
        Yaml yamlParser = new Yaml();
        URL resource = Resources.getResource(TOSCA_DAM_FILE_PATH);
        Map toscaDamMap = (Map) yamlParser.load(FileUtils.openInputStream(new File(resource.getFile())));

        int oldSize = dataStore.listSeaCloudsApplicationData().size();
        SeaCloudsApplicationData seaCloudsApplicationData = new SeaCloudsApplicationData(toscaDamMap);
        dataStore.addSeaCloudsApplicationData(seaCloudsApplicationData);
        int newSize = dataStore.listSeaCloudsApplicationData().size();
        assertEquals(oldSize, newSize-1);

        SeaCloudsApplicationData seaCloudsApplicationDataById = dataStore.getSeaCloudsApplicationDataById(seaCloudsApplicationData.getSeaCloudsApplicationId());
        assertEquals(seaCloudsApplicationData, seaCloudsApplicationDataById);
    }

    @Test
    public void testRemoveSeaCloudsApplicationData() throws IOException {
        Yaml yamlParser = new Yaml();
        URL resource = Resources.getResource(TOSCA_DAM_FILE_PATH);
        Map toscaDamMap = (Map) yamlParser.load(FileUtils.openInputStream(new File(resource.getFile())));

        int oldSize = dataStore.listSeaCloudsApplicationData().size();
        SeaCloudsApplicationData seaCloudsApplicationData = new SeaCloudsApplicationData(toscaDamMap);
        dataStore.addSeaCloudsApplicationData(seaCloudsApplicationData);

        SeaCloudsApplicationData seaCloudsApplicationDataById = dataStore.removeSeaCloudsApplicationDataById(seaCloudsApplicationData.getSeaCloudsApplicationId());
        assertEquals(seaCloudsApplicationData, seaCloudsApplicationDataById);

        int newSize = dataStore.listSeaCloudsApplicationData().size();
        assertEquals(oldSize, newSize);
    }

    @Test
    public void testGetSeaCloudsApplicationData() throws IOException {
        Yaml yamlParser = new Yaml();
        URL resource = Resources.getResource(TOSCA_DAM_FILE_PATH);
        Map toscaDamMap = (Map) yamlParser.load(FileUtils.openInputStream(new File(resource.getFile())));

        SeaCloudsApplicationData seaCloudsApplicationData = new SeaCloudsApplicationData(toscaDamMap);
        dataStore.addSeaCloudsApplicationData(seaCloudsApplicationData);

        SeaCloudsApplicationData seaCloudsApplicationDataById = dataStore.getSeaCloudsApplicationDataById(seaCloudsApplicationData.getSeaCloudsApplicationId());
        assertEquals(seaCloudsApplicationData, seaCloudsApplicationDataById);
    }

    @Test
    public void testListSeaCloudsApplicationData() {
        assertEquals(dataStore.listSeaCloudsApplicationData().size(), INITIAL_DATASTORE_SIZE);
    }

}
