package org.kontext.search.wiki;

import java.util.List;
import java.util.Set;

import org.kontext.search.SearchCriteria;
import org.kontext.search.SearchSource;
import org.kontext.search.Searcher;

public class WikiSearcher implements Searcher {

	SearchSource source;
	SearchCriteria criteria;
	List<SearchCriteria> criteriaList;
	
	public WikiSearcher(SearchSource source) {
		this.source = source;
	}
	
	@Override
	public SearchSource getSource() {
		return source;
	}

	@Override
	public void Search(SearchCriteria searchCriteria) {
		
	}

	@Override
	public void Search(Set<SearchCriteria> searchCriterias) {
		// TODO Auto-generated method stub
		
	}

}
