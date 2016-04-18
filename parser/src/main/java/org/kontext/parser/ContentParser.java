package org.kontext.parser;

/**
 * Content parser takes in a batch of documents and parses the contents. Parser
 * shall be called when the subscriber (controller) of the batches find that a
 * batch is ready for parsing. Parsed output pertaining to a document shall be 
 * stored against the document itself for further processing.
 * 
 * @IMPORTANT Batch = partition. 
 */
public abstract class ContentParser {

	private static final long serialVersionUID = 1L;

	public abstract void parse();

}
