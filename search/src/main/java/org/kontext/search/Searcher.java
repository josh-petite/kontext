package org.kontext.search;

import java.util.Set;

public interface Searcher {
	
	/**
	 * Get the source against which the search is run.
	 * 
	 * @return SearchSource of the searcher
	 */
	public SearchSource getSource();
	
	/**
	 * Run search against the assigned search source for the search criteria passed
	 * 
	 * @param searchCriteria
	 */
	public void Search(SearchCriteria searchCriteria);
	
	/**
	 * Run search for against the source for list of search criteria passed.
	 * 
	 * @param searchCriterias
	 */
	public void Search(Set<SearchCriteria> searchCriterias);
}
