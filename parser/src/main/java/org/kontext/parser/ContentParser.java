package org.kontext.parser;

import org.kontext.common.repositories.PropertiesRepository;

import com.google.inject.Inject;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Content parser takes in a batch of documents and parses the contents. Parser
 * shall be called when the subscriber (controller) of the batches find that a
 * batch is ready for parsing. Parsed output pertaining to a document shall be 
 * stored against the document itself for further processing.
 */
public class ContentParser implements Runnable {
	
	private PropertiesRepository propsRepo;
	@Inject
	public ContentParser(PropertiesRepository propsRepo) {
		this.propsRepo = propsRepo;
	}

	@Override
	public void run() {
		StanfordCoreNLP pipeline = new StanfordCoreNLP(propsRepo.getAllProperties());
		
		
	}
	
	

}
