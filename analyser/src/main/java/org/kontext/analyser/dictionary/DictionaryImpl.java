package org.kontext.analyser.dictionary;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.kontext.analyser.dictionary.exception.DictionaryException;

public class DictionaryImpl implements Dictionary {

	@Override
	public Set<String> getSynonyms(String noun) {

		Response dictionaryResponse = null;
		try {
			dictionaryResponse = DictionaryHelper.getDictionaryResponse(noun);
		} catch (DictionaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(dictionaryResponse.toString());
		if (dictionaryResponse.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + dictionaryResponse.getStatus());
		} else {
			System.out.println(dictionaryResponse.bufferEntity());
		}
//
//		String output = response.getEntity(String.class);
//		System.out.println("\n============getFtoCResponse============");
//		System.out.println(output);
		return null;
	}

	@Override
	public Map<String, Set<String>> getSynonymsForSet(Set<String> nouns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getRelated(String noun) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> getRelatedForSet(Set<String> nouns) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]) {
		new DictionaryImpl().getSynonyms("entropy");
	}

}
