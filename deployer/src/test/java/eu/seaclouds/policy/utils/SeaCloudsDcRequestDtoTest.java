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
package eu.seaclouds.policy.utils;


import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SeaCloudsDcRequestDtoTest {

    private static final String fakeTargetUrl = "http://127.0.0.1:8080";
    private static final String fakeType = "moduleType";
    private SeaCloudsDcRequestDto requestDto;


    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        requestDto = new SeaCloudsDcRequestDto.Builder()
                .type(fakeType)
                .url(fakeTargetUrl)
                .build();
    }

    @Test
    public void testGenerationSeaCloudsDcRequestDto() {
        assertEquals(requestDto.getType(), fakeType);
        assertEquals(requestDto.getUrl(), fakeTargetUrl);
        assertEquals(requestDto.getId(), fakeType + SeaCloudsDcRequestDto.ID_SUFIX);
    }

    @Test
    public void testConvertSeaCloudsDcRequestDtoToJson() throws JsonProcessingException {
        //{"id":"moduleType_ID","type":"moduleType","url":"http://127.0.0.1:8080"}
        String jsonBody = new ObjectMapper().writeValueAsString(requestDto);
        assertEquals(jsonBody, "{\"id\":\"moduleType_ID\",\"type\":\"moduleType\",\"url\":\"http://127.0.0.1:8080\"}");
    }


}
