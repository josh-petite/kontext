package org.kontext.analyser;

public interface ContextAnalyser {

	/**
	 * Consumes the parsed out in every document, analyses the parsed output,
	 * prepares and persists context prepared.
	 */
	void analyse();
}
