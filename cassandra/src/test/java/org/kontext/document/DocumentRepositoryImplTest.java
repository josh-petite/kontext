package org.kontext.document;

import java.util.List;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.cassandra.documents.exception.DocumentRepositoryException;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.datastax.driver.core.Row;

import junit.framework.Assert;

public class DocumentRepositoryImplTest {
	
	private DocumentRepository docsRepo;
	private PropertiesRepository propsRepo;
	private DataSourceManager dsMgr;
	
	public DocumentRepositoryImplTest() {
		propsRepo = PropertiesRepositoryImpl.getPropsRepo();
		dsMgr = new CassandraManager(propsRepo);
		docsRepo = new DocumentRepositoryImpl(propsRepo, dsMgr);
	}
	
	@BeforeClass
	public void doNothing() {
		
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
	
	@Test
	public void testReadByLimit() {
		String partition = "dummyPartition";
		List<Row> limitedDocuments = (List<Row>) docsRepo.read(partition, 10).all();
		System.out.println("Size of the result set = " + limitedDocuments.size());
		Assert.assertEquals(limitedDocuments.size(), 10);
	}
	
	@AfterClass
	public void clear() {
		propsRepo.clear();
	}

}
