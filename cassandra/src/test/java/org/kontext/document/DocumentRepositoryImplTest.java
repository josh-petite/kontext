package org.kontext.document;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.testng.annotations.Test;

public class DocumentRepositoryImplTest {
	
	private DocumentRepository docsRepo;
	
	public DocumentRepositoryImplTest() {
		docsRepo = new DocumentRepositoryImpl();
	}
	
	@Test
	public void testRead() {
		docsRepo.read();
	}

}
