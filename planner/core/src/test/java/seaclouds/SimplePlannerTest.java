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
package seaclouds;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.*;

public class SimplePlannerTest {

    String topologyFile;
    InputStream topologyInputFile = null;
    Planner planner=null;

    public SimplePlannerTest() throws FileNotFoundException {
        setUp();
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(SimplePlannerTest.class);
    }

    public void setUp() throws FileNotFoundException {
        topologyFile =getClass().getClassLoader().getResource("nuroCase.yaml").getFile();
        planner = new Planner(topologyFile);
    }

    @Test
    public void plan() throws IOException {
        planner.plan();
    }



}
