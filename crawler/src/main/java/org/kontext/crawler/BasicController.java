package org.kontext.crawler;

import com.google.inject.Inject;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryConstants;
import org.kontext.crawler.exception.ContentCrawlerException;

public class BasicController implements ContentCrawler {
    private PropertiesRepository propertiesRepository;

    @Inject
    public BasicController(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

    public void crawl() throws ContentCrawlerException {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(propertiesRepository.read("crawler_storage_folder"));
        config.setPolitenessDelay(Integer.parseInt(propertiesRepository.read("crawler_request_delay")));
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
	        /*
	         * For each crawl, you need to add some seed urls. These are the first
	         * URLs that are fetched and then the crawler starts following links
	         * which are found in these pages
	         */
	        controller.addSeed("http://www.ics.uci.edu/");
	        controller.addSeed("http://www.cnn.com/");
	        controller.addSeed("http://www.ics.uci.edu/~lopes/");
	        controller.addSeed("http://www.cnn.com/POLITICS/");
	        controller.addSeed("http://en.wikipedia.org/wiki/Main_Page");
	        controller.addSeed("http://en.wikipedia.org/wiki/Obama");
	        controller.addSeed("http://en.wikipedia.org/wiki/Bing");

	        /*
	         * Start the crawl. This is a blocking operation, meaning that your code
	         * will reach the line after this only when crawling is finished.
	         */
	        controller.start(InitialCrawler.class,
	                Integer.parseInt(propertiesRepository.read(PropertiesRepositoryConstants.crawler_thread_count)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void crawl(String searchCriteria) throws ContentCrawlerException {
		throw new RuntimeException("Not implemented yet.");
	}
}
