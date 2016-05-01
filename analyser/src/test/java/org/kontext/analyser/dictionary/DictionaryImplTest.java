package org.kontext.analyser.dictionary;

import static org.kontext.analyser.ContextAnalyserConstants.*;

import java.io.Serializable;
import java.util.Set;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

public class DictionaryImplTest {

	private Dictionary dictionary;

	@BeforeClass
	public void beforeClass() {
		dictionary = DictionaryImpl.getInstance();
	}

	@AfterClass
	public void afterClass() {
	}

	@Test
	public void testGetSynonymsByType() {
		Set<Serializable> _syns = dictionary.getSynonyms("house", noun);
		Assert.assertNotNull(_syns);
		Assert.assertFalse(_syns.isEmpty());
	}
	
	@Test
	public void testGetSynonyms() {
		Set<Serializable> _syns = dictionary.getSynonymsForNoun("house");
		Assert.assertNotNull(_syns);
		Assert.assertFalse(_syns.isEmpty());
	}

}
