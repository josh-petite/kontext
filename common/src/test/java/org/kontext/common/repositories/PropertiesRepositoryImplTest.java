package org.kontext.common.repositories;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

public class PropertiesRepositoryImplTest {
	PropertiesRepository propRepo = new PropertiesRepositoryImpl();

	@Test
	public void testLoad() {
		String value = propRepo.read(cassandra_document_table);
		Assert.assertNotNull(value);
	}
	
	@BeforeClass
	public void beforeClass() {

	}

	@AfterClass
	public void afterClass() {
	}

}
