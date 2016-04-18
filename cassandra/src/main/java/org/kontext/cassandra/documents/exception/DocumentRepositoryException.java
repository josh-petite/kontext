package org.kontext.cassandra.documents.exception;

public class DocumentRepositoryException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public static final String generic = "Failed to retrieve count. Check if the table exists. ";

	public DocumentRepositoryException() {
		super(generic);
	}
}
