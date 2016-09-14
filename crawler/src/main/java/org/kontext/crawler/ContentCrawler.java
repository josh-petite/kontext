package org.kontext.crawler;

import java.util.Collection;

import org.kontext.crawler.exception.ContentCrawlerException;
import org.kontext.crawler.wiki.WikiContentCrawler;
import org.kontext.crawler.wiki.WikiCrawlable;

public interface ContentCrawler {
	
	static ContentCrawler getCrawler(Crawlable crawlable) {
		if (crawlable instanceof WikiCrawlable)
			return new WikiContentCrawler(crawlable);
		return null;
	}

	/**
	 * Crawls a given crawlable and produces a crawled object which has the associated data.
	 * The content shall be persisted into a datasource.
	 * 
	 * @param crawlable
	 * @throws Exception
	 */
	void crawl() throws ContentCrawlerException;
	
	/**
	 * Crawls a given collection of crawlables, retrieves crawled content and persists into the
	 * datasource.
	 * 
	 * @param crawlables
	 * @throws Exception
	 */
	void crawl(Collection<? extends Crawlable> crawlables) throws ContentCrawlerException;
}
