package org.kontext.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
    private CrawlConfig config;
    private final int numberOfCrawlers = 7;
    private final int politenessDelay = 300; // 200 is default, but I don't want to get banned from wikipedia.com

    public Controller() {

        config = new CrawlConfig();
        config.setCrawlStorageFolder("~/crawler");
        config.setPolitenessDelay(politenessDelay);

    }

    public void start() throws Exception {
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.setCustomData(new String[]{"http://en.wikipedia.org/"});

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("http://en.wikipedia.org/wiki/Main_Page");
        controller.addSeed("http://en.wikipedia.org/wiki/Obama");
        controller.addSeed("http://en.wikipedia.org/wiki/Bing");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(InitialCrawler.class, numberOfCrawlers);
    }
}
