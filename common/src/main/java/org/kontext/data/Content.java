package org.kontext.data;

import java.util.List;
import java.util.UUID;

import edu.stanford.nlp.util.CoreMap;

/**
 * Representation of a piece of knowledge. The knowledge could be a document, transcribed audio, video, etc.
 * The object representation has an identifier, the content or text from the piece of knowledge. The content
 * shall also have the parsed out after parsing which allows analysis of the knowledge.
 */

public class Content {

	private UUID id;
	private String text;
	private List<CoreMap> sentences;
	
	public UUID getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public List<CoreMap> getSentences() {
		return sentences;
	}
}
