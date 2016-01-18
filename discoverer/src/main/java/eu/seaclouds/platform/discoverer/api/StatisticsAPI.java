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

package eu.seaclouds.platform.discoverer.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.seaclouds.platform.discoverer.core.Discoverer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;


@Path("/statistics")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsAPI {

    /* vars */
    private Discoverer discoverer;

    public StatisticsAPI(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    @GET
    public StatisticsRepresentation getStatistics() {
        int crawledTimes = discoverer.crawledTimes;
        int totalCrawledOfferings = discoverer.totalCrawledOfferings;
        Date lastCrawl = discoverer.lastCrawl;

        return new StatisticsRepresentation(crawledTimes, totalCrawledOfferings, lastCrawl);
    }

    private class StatisticsRepresentation {

        private int crawledTimes;
        private int totalCrawledOfferings;
        private Date lastCrawl;

        public StatisticsRepresentation(int crawledTimes, int totalCrawledOfferings, Date lastCrawl) {
            this.crawledTimes = crawledTimes;
            this.totalCrawledOfferings = totalCrawledOfferings;
            this.lastCrawl = lastCrawl;
        }

        @JsonProperty("crawled_times")
        public int getCrawledTimes() {
            return crawledTimes;
        }

        @JsonProperty("total_crawled_offerings")
        public int getTotalCrawledOfferings() {
            return totalCrawledOfferings;
        }

        @JsonProperty("last_crawl")
        public Date getLastCrawl() {
            return lastCrawl;
        }
    }
}
