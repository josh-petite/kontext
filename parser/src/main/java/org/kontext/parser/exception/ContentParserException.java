package org.kontext.parser.exception;

public class ContentParserException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String generic = "Content could not be parsed. ";

	public ContentParserException() {
		super(generic);
	}
}
