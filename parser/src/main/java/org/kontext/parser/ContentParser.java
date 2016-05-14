package org.kontext.parser;

import java.util.Date;

/**
 * Content parser takes in a batch of documents and parses the contents. Parser
 * shall be called when the subscriber (controller) of the batches find that a
 * batch is ready for parsing. Parsed output pertaining to a document shall be 
 * stored against the document itself for further processing.
 * 
 * @IMPORTANT Batch = partition. 
 */
public abstract class ContentParser {

	/**
	 * Parse all the documents present in the corpus
	 */
	public abstract void parse();

	/**
	 * Parse the given string and print the parsed
	 * output.
	 * 
	 * @param parseMe
	 */
	public abstract void parse(String parseMe);
	
	/**
	 * Parse the partition passed.
	 * 
	 * @param partition
	 */
	public abstract void parse(Date partition);

}
