package org.kontext.crawler.wiki;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.crawler.ContentCrawler;
import org.kontext.crawler.exception.ContentCrawlerException;
import org.kontext.data.DataSourceManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WikiContentCrawlerTest {
private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	
	private DataSourceManager datasourceMgr;
	private ContentCrawler crawler;
	
	@BeforeClass
	public void beforeClass() {
		datasourceMgr = new CassandraManager(propsRepo);
		new DocumentRepositoryImpl(propsRepo, datasourceMgr);
		crawler = ContentCrawler.getCrawler(new WikiCrawlable("Barack Obama"));
	}
	
	@Test
	public void testCrawler() throws ContentCrawlerException {
		crawler.crawl();
	}

	@AfterClass
	public void afterClass() {
	}

}