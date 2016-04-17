package org.kontext.document;

import org.kontext.cassandra.documents.DocumentRepository;
import org.testng.annotations.Test;

public class DocumentRepositoryImplTest {
	
	private DocumentRepository docsRepo;
	
	public DocumentRepositoryImplTest() {
		
	}
	
	@Test
	public void testRead() {
		docsRepo.read();
	}

}
