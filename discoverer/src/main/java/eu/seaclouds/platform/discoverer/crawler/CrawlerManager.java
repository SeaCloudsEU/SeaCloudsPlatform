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

package eu.seaclouds.platform.discoverer.crawler;

import eu.seaclouds.platform.discoverer.core.Discoverer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


@SuppressWarnings("serial")
public class CrawlerManager implements Runnable {

    private Discoverer discoverer;
    private ArrayList<SCCrawler> activeCrawlers = new ArrayList<>();
    private static HashMap<String, SCCrawler> availableCrawlers = new HashMap<>();
    static {
        availableCrawlers.put(CloudHarmonyCrawler.Name, new CloudHarmonyCrawler());
        availableCrawlers.put(PaasifyCrawler.Name, new PaasifyCrawler());
    }

    public int crawledTimes;
    public int totalCrawledOfferings;
    public Date lastCrawl;


    public CrawlerManager(ArrayList<String> activeCrawlers) {
        this.discoverer = Discoverer.instance();
        /* stats */
        crawledTimes = 0;
        totalCrawledOfferings = 0;
        lastCrawl = null;

        for (String crawlerName : activeCrawlers) {
            SCCrawler crawler = availableCrawlers.get(crawlerName);

            if (crawler != null)
                this.activeCrawlers.add(crawler);
        }
    }

    private void crawl() {
        /* crawl */
        for(SCCrawler crawler : activeCrawlers)
            crawler.crawl();

        /* crawlers have crawled, it is possible to create single file offering */
        this.discoverer.generateSingleOffering();

        /* statistics */
        this.discoverer.crawledTimes++;
        this.discoverer.lastCrawl = Calendar.getInstance().getTime();
    }

    @Override
    public void run() {
        try {
            this.crawl();
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            this.discoverer.setRefreshing(false);
        }
    }
}
