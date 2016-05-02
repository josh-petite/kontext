package org.kontext.analyser;

import org.kontext.analyser.context.Context;
import org.kontext.analyser.exception.DocumentAnalyserException;

public interface DocumentAnalyser {

	/**
	 * Consumes the parsed out in every document, analyses the parsed documenht,
	 * prepares and persists context prepared.
	 * @throws DocumentAnalyserException 
	 */
	void analyse() throws DocumentAnalyserException;

	/**
	 * 
	 * @return Context of the document prepared.
	 */
	Context getContext();
}