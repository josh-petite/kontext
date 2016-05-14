package org.kontext.analyser.dictionary;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_key;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_path;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_thesaurus;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.dictionary_uri;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import org.kontext.analyser.dictionary.exception.DictionaryException;
import org.kontext.analyser.dictionary.jaxb.EntryListType;
import org.kontext.analyser.dictionary.jaxb.EntryType;
import org.kontext.analyser.dictionary.jaxb.SensType;
import org.kontext.analyser.dictionary.jaxb.SensType.Rel;
import org.kontext.analyser.dictionary.jaxb.SensType.Syn;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictionaryHelper {

	private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();

	private static final Logger LOG = LoggerFactory.getLogger(DictionaryHelper.class);

	public static Response getDictionaryResponse(String noun) throws DictionaryException {

		WebTarget dictionaryResource = null;
		Response dictionaryResponse = null;

		try {
			Client client = ClientBuilder.newClient();

			String uri = propsRepo.read(dictionary_uri);
			String path = propsRepo.read(dictionary_path) + noun;
			String token = propsRepo.read(dictionary_thesaurus);

			dictionaryResource = client.target(uri);

			dictionaryResponse = dictionaryResource.queryParam(dictionary_key, token).path(path).request()
					.accept(MediaType.APPLICATION_XML).get();

			if (LOG.isDebugEnabled())
				LOG.debug(dictionaryResource.getUri().toURL().toString());

			MultivaluedMap<String, Object> headers = dictionaryResponse.getHeaders();
			headers.remove(HttpHeaders.CONTENT_TYPE);
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Headers: " + headers.toString());
				LOG.debug(dictionaryResponse.toString() + " Content length:" + dictionaryResponse.getLength());
			}

		} catch (Exception e) {
			throw new DictionaryException(e);
		}
		return dictionaryResponse;
	}

	public static Set<String> getSynonyms(EntryListType _dictionaryResponse, String word, String pos) {
		List<EntryType> entries = _dictionaryResponse.getEntry();
		Set<String> synSet = new HashSet<>();
		
		// Only one entry type is present.
		for (EntryType entry : entries) {
			if (!word.equalsIgnoreCase(entry.getTerm().getHw()) || !pos.equalsIgnoreCase(entry.getFl()))
				continue;

			List<SensType> sensTypes = entry.getSens();
			for (SensType sensType : sensTypes) {
				Syn _synCol = sensType.getSyn();
				
				synSet.addAll(deRefXMLRefs(_synCol));
			}
		}

		return synSet;
	}
	
	private static Set<String> deRefXMLRefs(Syn _syncol) {
		Set<String> _syns = new HashSet<String>();
		for (Serializable _serializable : _syncol.getContent()) {
			if (_serializable instanceof JAXBElement)
				continue;
			
			String[] syns = ((String) _serializable).replaceAll("\\(|\\)", "").split(",");
			_syns.addAll(Arrays.asList(syns));
		}
		return _syns;
	}
	
	public static Set<Serializable> getRelated(EntryListType _dictionaryResponse, String word, String pos) {
		List<EntryType> entries = _dictionaryResponse.getEntry();

		Set<Serializable> relSet = new HashSet<>();
		for (EntryType entry : entries) {
			if (!word.equalsIgnoreCase(entry.getTerm().getHw()) || !pos.equalsIgnoreCase(entry.getFl()))
				continue;

			List<SensType> sensTypes = entry.getSens();
			for (SensType sensType : sensTypes) {
				Rel _relCol = sensType.getRel();
				relSet.addAll(_relCol.getContent());
			}
		}

		return relSet;
	}

}
