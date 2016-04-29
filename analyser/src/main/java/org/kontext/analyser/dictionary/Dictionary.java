package org.kontext.analyser.dictionary;

import java.util.Map;
import java.util.Set;

public interface Dictionary {
	
	/**
	 * Get the synonyms for the word as a particular POS
	 * 
	 * @param noun
	 * @return Set of synonyms
	 */
	Set<String> getSynonyms(String noun);
	
	/**
	 * Get synonyms for a set of nouns
	 * 
	 * @param nouns
	 * @return Map of words to their sets of synonyms
	 */
	Map<String, Set<String>> getSynonymsForSet(Set<String> nouns);
	
	/**
	 * Get associated words for the word passed.
	 * 
	 * @param noun
	 * @return set of associated words for the passed word
	 */
	Set<String> getRelated(String noun);
	
	/**
	 * Get related words for the set of words passed.
	 * 
	 * @param nouns
	 * @return
	 */
	Map<String, Set<String>> getRelatedForSet(Set<String> nouns);
}
