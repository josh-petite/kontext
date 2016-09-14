package org.kontext.crawler;

import org.kontext.crawler.exception.CrawlSourceNotSupportedException;
import org.kontext.crawler.wiki.WikiCrawlable;

public interface Crawlable {

	static Crawlable getCrawlable(String searchMe, CrawlSource source) throws CrawlSourceNotSupportedException {
		switch (source) {
		case WIKI:
			return new WikiCrawlable(searchMe);

		default:
			throw new CrawlSourceNotSupportedException(source);
		}
		
	}
	
	CrawlSource getType();
}
