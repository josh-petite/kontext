package org.kontext.analyser;

import org.kontext.analyser.context.Context;

public interface DocumentAnalyser {

	/**
	 * Consumes the parsed out in every document, analyses the parsed documenht,
	 * prepares and persists context prepared.
	 */
	void analyse();

	/**
	 * 
	 * @return Context of the document prepared.
	 */
	Context getContext();
}