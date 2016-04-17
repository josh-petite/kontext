package org.kontext.document;

import java.util.List;

import org.junit.AfterClass;
import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.cassandra.documents.exception.DocumentRepositoryException;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;
import org.testng.annotations.Test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import junit.framework.Assert;

public class DocumentRepositoryImplTest {
	
	private DocumentRepository docsRepo;
	private PropertiesRepository propsRepo;
	private DataSourceManager dsMgr;
	
	public DocumentRepositoryImplTest() {
		propsRepo = new PropertiesRepositoryImpl();
		dsMgr = new CassandraManager(propsRepo);
		docsRepo = new DocumentRepositoryImpl(propsRepo, dsMgr);
	}
	
	@Test
	public void testCount() throws DocumentRepositoryException {
		long count = docsRepo.count();
		System.out.println(count);
		Assert.assertTrue(count > 0);
	}
	
	@Test
	public void testRead() {
		String partition = "dummyPartition";
		List<Row> allDocuments = docsRepo.read(partition).all();
		System.out.println("Size of the result set = " + allDocuments.size());
		Assert.assertFalse(allDocuments.isEmpty());
	}
	
	@AfterClass
	public void clear() {
		propsRepo.clear();
	}

}
