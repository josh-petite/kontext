package org.kontext.crawler;

import org.kontext.crawler.exception.ContentCrawlerException;

public interface ContentCrawler {
	
	/**
	 * Autonomous search. Starting shall initiate crawling on
	 * bunch of sites and random corpus is built.
	 * 
	 * @throws Exception
	 */
    void crawl() throws ContentCrawlerException;
   
    /**
     * Consumes the criteria, runs search and crawls search resutlts and
     * persists them.
     * 
     * @param searchCriteria
     * @throws ContentCrawlerException
     */
    void crawl(String searchCriteria) throws ContentCrawlerException;
}
