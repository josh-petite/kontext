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
	public void testRunParagraph() throws ContentParserException {
		String parseMe = "Freeform search allows users to prepare sophisticated search criteria based on dtSearch syntax offline or online, and then save the search criteria by running a search on ingested case data (legal search). Searches can continue to be refined online based on the search results and can also be rerun for verification. Freeform searches are performed at the case level and on data that has been ingested into the case. Freeform search is flexible and powerful. It uses advanced search features to specify the order of precedence and can contain hundreds of search terms. One advantage of Freeform search is that its syntax is similar to the syntax used for full-text searching. This is helpful because you can take complex and extensive search queries from opposing counsel, in-house litigation support departments or other third-party tools (which conform to the syntax) and then run the queries directly in Freeform search.";
		cParser.parse(parseMe);
	}
	
	@Test
	public void testRunComplexSentence() throws ContentParserException {
		String parseMe = "Search is a feature that many software systems cannot function without.";
		cParser.parse(parseMe);
	}
	
	@Test
	public void testRunFromCassandra() throws ContentParserException {
		cParser.parse();
	}
}
