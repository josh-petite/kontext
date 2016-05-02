package org.kontext.analyser.dictionary;

import static org.kontext.analyser.ContextAnalyserConstants.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.kontext.analyser.dictionary.exception.DictionaryException;
import org.kontext.analyser.dictionary.jaxb.EntryListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictionaryImpl implements Dictionary {

	private static final Logger LOG = LoggerFactory.getLogger(DictionaryImpl.class);
	private static final DictionaryImpl dictionary = new DictionaryImpl();
	
	private DictionaryImpl() {}
	
	public static Dictionary getInstance() {
		return dictionary;
	}
	
	@Override
	public Set<String> getSynonymsForNoun(String _noun) {
		return getSynonyms(_noun, noun);
	}
	
	@Override
	public Set<String> getSynonyms(String word, String pos) {
		Response dictionaryResponse = null;
		EntryListType _dictionaryReponse = null;
		
		try {
			dictionaryResponse = DictionaryHelper.getDictionaryResponse(word);
		} catch (DictionaryException e) {
			LOG.error("Dictionary lookup failed. ", e);
		}
		
		if (dictionaryResponse.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + dictionaryResponse.getStatus());
		} 
		
		_dictionaryReponse = dictionaryResponse.readEntity(EntryListType.class);
		
		if (LOG.isDebugEnabled())
			LOG.debug(_dictionaryReponse.toString());
		
		return DictionaryHelper.getSynonyms(_dictionaryReponse, word, pos);
	}

	@Override
	public Map<String, Set<String>> getSynonymsForSet(Set<String> nouns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Serializable> getRelated(String word, String pos) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<Serializable> getRelatedForNoun(String noun) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> getRelatedForSet(Set<String> nouns) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]) {
		new DictionaryImpl().getSynonyms("entropy", noun);
	}

}
