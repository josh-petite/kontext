package org.kontext.analyser.dictionary;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_key;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_path;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_thesaurus;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_uri;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kontext.analyser.dictionary.exception.DictionaryException;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictionaryHelper {
	
//	private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();

//	private static final Logger LOG = LoggerFactory.getLogger(DictionaryHelper.class);
	
	public static Response getDictionaryResponse(String noun) throws DictionaryException {
		
		WebTarget dictionaryResource = null;
		Response dictionaryResponse = null;
		
		try {
			Client client = ClientBuilder.newClient();
			
			String uri = "http://www.dictionaryapi.com/"; // propsRepo.read(dictionary_uri);
			String path = "api/v1/references/thesaurus/xml/" + noun; //propsRepo.read(dictionary_path);
			String token = "649e53eb-b6e2-4f32-9cfb-eac3549f7e92"; //propsRepo.read(dictionary_thesaurus);
			
			String fullUrl = new StringBuilder(uri).append(path).toString();
			dictionaryResource = client.target(fullUrl).queryParam(dictionary_key, token);
			System.out.println(dictionaryResource.getUri().toURL().toString());
			
			dictionaryResponse = dictionaryResource.path(path).request().accept(MediaType.APPLICATION_XML).get();
			
//			if (LOG.isDebugEnabled())
//				LOG.debug(dictionaryResponse.getEntity().toString());
			
			System.out.println(dictionaryResponse + " " + dictionaryResponse.getLength());
		} catch (Exception e) {
			throw new DictionaryException(e);
		}
		return dictionaryResponse;
	}
	
}
