package org.kontext.crawler.exception;

import org.kontext.crawler.CrawlSource;

public class CrawlSourceNotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String errorMessage = "Crawl source provided is not supported yet. Source : ";

	public CrawlSourceNotSupportedException(CrawlSource source) {
		super(errorMessage + source.name());
	}
}
