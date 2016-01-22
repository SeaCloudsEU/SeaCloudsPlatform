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

import com.github.fakemongo.Fongo;
import eu.seaclouds.platform.discoverer.api.DiscovererAPI;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;


public class DiscovererTest {
    @Test
    public void testAddOffering() {
        Fongo fongo = new Fongo("Mongo DB");
        Discoverer d = new Discoverer(fongo.getMongo());

        String ofId = "EC2";
        d.addOffering(new Offering(ofId));
        Offering of = d.fetchOffer(ofId);

        Assert.assertNotNull(of);
        Assert.assertEquals(of.getName(), ofId);
    }

    @Test
    public void testRemoveOffering() {
        Fongo fongo = new Fongo("Mongo DB");
        Discoverer d = new Discoverer(fongo.getMongo());

        String ofId = "NonPresentOfferings";
        Assert.assertFalse(d.removeOffering(ofId));

        ofId = "EC2";
        d.addOffering(new Offering(ofId));
        Assert.assertTrue(d.removeOffering(ofId));
    }


    @Test
    public void testFetch() throws IOException {
        Fongo fongo = new Fongo("Mongo DB");
        Discoverer d = new Discoverer(fongo.getMongo());

        ArrayList<String> offerings = new ArrayList<String>();
        offerings.add("EC2");
        offerings.add("Azure");
        offerings.add("Openshift");

        for (String name: offerings) {
            d.addOffering(new Offering(name));
        }

        ArrayList<String> offeringIds = (new DiscovererAPI(d)).getOfferingIds();
        Assert.assertEquals(offeringIds.size(), offerings.size());

        for (String id: offeringIds) {
            Assert.assertTrue(offerings.contains(id));
        }
    }

    @Test
    public void testFetchIf() throws IOException {
        Fongo fongo = new Fongo("Mongo DB");
        Discoverer d = new Discoverer(fongo.getMongo());

        ArrayList<String> offeringNames = new ArrayList<String>();
        offeringNames.add("EC2");
        offeringNames.add("Azure");
        offeringNames.add("Openshift");

        for (String name: offeringNames) {
            d.addOffering(new Offering(name));
        }

        Offering of = new Offering("Example");
        of.addProperty("num_cpus", "8");
        d.addOffering(of);

        ArrayList<DiscovererAPI.OfferingRepresentation> offerings = (new DiscovererAPI(d)).getOfferingsIf("{ \"num_cpus\": \"8\" }");
        Assert.assertEquals(offerings.size(), 1);

        String fetchedOffering = offerings.get(0).getOffering();
        Assert.assertEquals(fetchedOffering, of.toTosca());

    }
}
