package org.kontext.analyser;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;
import org.testng.annotations.AfterClass;

public class ContextAnalyserImplTest {

	private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	
	private DataSourceManager datasourceMgr;
	private ContextAnalyser contextAnalyser;
	private DocumentRepository docsRepo;
	
	@BeforeClass
	public void beforeClass() {
		datasourceMgr = new CassandraManager(propsRepo);
		docsRepo = new DocumentRepositoryImpl(propsRepo, datasourceMgr);
		contextAnalyser = new ContextAnalyserImpl(docsRepo);
	}
	
	@Test
	public void testAnalyser() {
		contextAnalyser.analyse();
	}

	@AfterClass
	public void afterClass() {
	}

}
