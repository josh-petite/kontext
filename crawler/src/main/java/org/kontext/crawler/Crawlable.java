package org.kontext.crawler;

import org.kontext.crawler.exception.ContentCrawlerException;

public interface Crawlable {

	void isCrawlable() throws ContentCrawlerException;
}
