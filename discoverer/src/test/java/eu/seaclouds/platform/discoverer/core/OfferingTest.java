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


import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OfferingTest {
    static String offeringName = "Example with illegal characters : + -";

    static String offeringJSON = "{" +
            "\"offering_name\":\"Rackspace_Cloud_Servers_standard_512mb_LON\"," +
            "\"last_crawl\":1447258085459," +
            "\"type\":\"seaclouds.Nodes.Compute\"," +
            "\"offering_id\":\"1370304911994607448\"" +
            "}";

    @Test
    public void testOffering() {
        Offering of = Offering.fromJSON(offeringJSON);
        Assert.assertNotNull(of);
    }

    @Test
    public void testOfferingNameSanitization() {
        String sanitizedName = Offering.sanitizeName(offeringName);
        Assert.assertEquals(sanitizedName.indexOf(':'), -1);
        Assert.assertEquals(sanitizedName.indexOf(' '), -1);
        Assert.assertEquals(sanitizedName.indexOf('+'), -1);
        Assert.assertEquals(sanitizedName.indexOf('-'), -1);
    }

    @Test
    public void testOfferingJSON() throws ParseException {
        Offering of = Offering.fromJSON(offeringJSON);
        String offeringJSONString = of.toJSON();
        Assert.assertNotNull(offeringJSONString);
        Offering parsedOf = Offering.fromJSON(offeringJSONString);
        Assert.assertNotNull(parsedOf);
        Assert.assertTrue(of.getName().equals(parsedOf.getName()));
    }


}
