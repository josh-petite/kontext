package org.kontext.analyser.exception;

public class DocumentAnalyserException extends Exception {
	private static final long serialVersionUID = 1L;

	public static final String generic = "Document analysis failed. ";
	
	public DocumentAnalyserException(Throwable t) {
		super(generic, t);
	}
}
