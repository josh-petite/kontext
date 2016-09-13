package org.kontext.crawler;

import java.util.Collection;

import org.kontext.crawler.exception.ContentCrawlerException;

public interface ContentCrawler {

	/**
	 * Crawls a given crawlable and produces a crawled object which has the associated data.
	 * The content shall be persisted into a datasource.
	 * 
	 * @param crawlable
	 * @throws Exception
	 */
	void crawl(Crawlable crawlable) throws ContentCrawlerException;
	
	/**
	 * Crawls a given collection of crawlables, retrieves crawled content and persists into the
	 * datasource.
	 * 
	 * @param crawlables
	 * @throws Exception
	 */
	void crawl(Collection<? extends Crawlable> crawlables) throws ContentCrawlerException;
}
