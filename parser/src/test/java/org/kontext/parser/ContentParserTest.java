package org.kontext.parser;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;
import org.kontext.parser.exception.ContentParserException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ContentParserTest {

	private DocumentRepository docsRepo;
	private PropertiesRepository propsRepo;
	private DataSourceManager dsMgr;
	private ContentParser cParser;
	
	public ContentParserTest() {
		propsRepo = new PropertiesRepositoryImpl();
		dsMgr = new CassandraManager(propsRepo);
		docsRepo = new DocumentRepositoryImpl(propsRepo, dsMgr);
		cParser = new ContentParserImpl(docsRepo);
	}

	@BeforeClass
	public void doNothing() {

	}

	@Test
	public void testRun() throws ContentParserException {
		cParser.parse();
		
	}
}
