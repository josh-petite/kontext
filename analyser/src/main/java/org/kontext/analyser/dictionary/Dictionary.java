package org.kontext.analyser.dictionary;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface Dictionary {
	
	/**
	 * Get the synonyms for the word as a noun POS
	 * 
	 * @param noun
	 * @return Set of synonyms
	 */
	Set<Serializable> getSynonymsForNoun(String noun);
	
	/**
	 * Get the synonyms for the word as a particular POS
	 * @param word
	 * @param pos
	 * @return
	 */
	Set<Serializable> getSynonyms(String word, String pos);
	
	/**
	 * Get synonyms for a set of nouns
	 * 
	 * @param nouns
	 * @return Map of words to their sets of synonyms
	 */
	Map<String, Set<String>> getSynonymsForSet(Set<String> nouns);
	
	/**
	 * Get associated words for the noun passed.
	 * 
	 * @param noun
	 * @return set of associated words for the passed word
	 */
	Set<Serializable> getRelatedForNoun(String noun);
	
	/**
	 * Get related words for the set of words passed.
	 * 
	 * @param nouns
	 * @return
	 */
	Map<String, Set<String>> getRelatedForSet(Set<String> nouns);
	
	/**
	 * Get the associated words for the word as a particular POS
	 * @param word
	 * @param pos
	 * @return
	 */
	Set<Serializable> getRelated(String word, String pos);
}
