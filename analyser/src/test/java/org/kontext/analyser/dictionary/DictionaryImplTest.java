package org.kontext.analyser.dictionary;

import static org.kontext.analyser.ContextAnalyserConstants.noun;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
		Set<String> _syns = dictionary.getSynonyms("house", noun);
		Assert.assertNotNull(_syns);
		Assert.assertFalse(_syns.isEmpty());
	}
	
	@Test
	public void testGetSynonyms() {
		Set<String> _syns = dictionary.getSynonymsForNoun("house");
		Assert.assertNotNull(_syns);
		Assert.assertFalse(_syns.isEmpty());
	}

}
