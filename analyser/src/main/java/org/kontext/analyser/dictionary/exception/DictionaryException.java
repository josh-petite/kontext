package org.kontext.analyser.dictionary.exception;

public class DictionaryException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private static final String NOT_IMPLEMENTED = "Query by set of words feature has not been implemented yet";
	
	public DictionaryException() {
		super(NOT_IMPLEMENTED);
	}
	
	public DictionaryException(Throwable t) {
		super(t);
	}
}
