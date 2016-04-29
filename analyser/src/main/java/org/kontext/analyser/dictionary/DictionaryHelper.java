package org.kontext.analyser.dictionary;

import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import org.kontext.analyser.dictionary.exception.DictionaryException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class DictionaryHelper {
	
	private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();

	public static WebResource getWebResource() throws DictionaryException {
		
		WebResource dictionaryResource = null;
		try {
			Client client = Client.create();
			
			String uri = propsRepo.read(dictionary_uri);
			String token = propsRepo.read(dictionary_thesaurus);
			
			dictionaryResource = client.resource(uri);
			dictionaryResource.setProperty(dictionary_key, token);
			
//			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
//			System.out.println(response.toString());
//			if (response.getStatus() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
//			}
//
//			String output = response.getEntity(String.class);
//			System.out.println("\n============getFtoCResponse============");
//			System.out.println(output);

		} catch (Exception e) {
			throw new DictionaryException(e);
		}
		return dictionaryResource;
	}
}
