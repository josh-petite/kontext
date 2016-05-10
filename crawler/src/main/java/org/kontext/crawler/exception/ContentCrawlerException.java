package org.kontext.crawler.exception;

public class ContentCrawlerException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ContentCrawlerException(String message) {
		super(message);
	}
	
	public ContentCrawlerException(String message, Throwable t) {
		super(message, t);
	}
}
