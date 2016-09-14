package org.kontext.crawler.wiki;

import java.net.URL;
import java.util.List;

import org.kontext.crawler.CrawlSource;
import org.kontext.crawler.Crawlable;

public class WikiCrawlable implements Crawlable {
	
	private final String searchMe;
	private List<URL> urls;
	
	public WikiCrawlable(String searchMe) {
		this.searchMe = searchMe;
	}
	
	public String getSearchMe() {
		return searchMe;
	}
	
	public List<URL> getUrl() {
		return urls;
	}

	@Override
	public CrawlSource getType() {
		return CrawlSource.WIKI;
	}
	
}
