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


import org.testng.annotations.Test;
import org.testng.Assert;


public class DiscovererTest {

    @Test
    public void testSignificantInstance() {
        Discoverer d = Discoverer.instance();
        Assert.assertNotNull(d);
    }


    @Test
    public void testWorkingDirectoryNotNull() {
        Discoverer d = Discoverer.instance();
        String wd = d.getWorkingDirectory();
        Assert.assertNotNull(wd);
    }


    @Test
    public void testWorkingDirectoryLengthNotZero() {
        Discoverer d = Discoverer.instance();
        String wd = d.getWorkingDirectory();
        int len = wd.length();
        Assert.assertFalse(len == 0);
    }


    @Test
    public void testWorkingDirectoryEndingWithSlash() {
        Discoverer d = Discoverer.instance();
        String wd = d.getWorkingDirectory();
        int len = wd.length();
        char ending = wd.charAt(len - 1);
        Assert.assertEquals(ending, '/');
    }
}
