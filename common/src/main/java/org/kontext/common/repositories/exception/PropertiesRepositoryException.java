package org.kontext.common.repositories.exception;

public class PropertiesRepositoryException extends Exception {

	private static final long serialVersionUID = -8890500031359800367L;

	private static final String generic = "Configuruation repository setup failed. ";
	public static final String config_file_not_found = "Configuration file could not be loaded. ";
	
	public PropertiesRepositoryException() {
		super(generic);
	}
	
	public PropertiesRepositoryException(String message) {
		super(message);
	}
	
	public PropertiesRepositoryException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
