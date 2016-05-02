package org.kontext.analyser.dictionary.exception;

public class DictionaryException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String NOT_IMPLEMENTED = "Query by set of words feature has not been implemented yet";

	public static final String dictionary_response_interleaved_with_serializables = "The response from the dictionary has elements that cannot be consumed by cassandra. ";
	public static final String dictionary_api_call_failed = "Dictionary API call failed. Check if the service is available. ";

	public DictionaryException() {
		super(NOT_IMPLEMENTED);
	}

	public DictionaryException(String message) {
		super(message);
	}

	public DictionaryException(Throwable t) {
		super(t);
	}
}
