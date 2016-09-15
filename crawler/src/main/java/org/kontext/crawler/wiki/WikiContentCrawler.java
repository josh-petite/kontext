package org.kontext.crawler.wiki;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.crawler.ContentCrawler;
import org.kontext.crawler.ContentCrawlerHelper;
import org.kontext.crawler.CrawlSource;
import org.kontext.crawler.Crawlable;
import org.kontext.crawler.exception.ContentCrawlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiContentCrawler implements ContentCrawler {

	private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	private static final Logger LOG = LoggerFactory.getLogger(WikiContentCrawler.class);
	private static final Object WIKI_REVISIONS = "revisions";
	private static final String WIKI_CONTENT = "*";
	private static final Object WIKI_QUERY = "query";
	private static final Object WIKI_PAGES = "pages";

	private WikiCrawlable crawlable;
	
	private JSONArray searchResultJSON;
	private JSONArray searchRelevantTitles;
	private JSONArray searchResultURLs;
	private JSONArray searchResultContents;
	
	public WikiContentCrawler(Crawlable crawlable) {
		this.crawlable = (WikiCrawlable) crawlable;
	}

	@Override
	public void crawl() throws ContentCrawlerException {
		Response searchResponse = searchWiki(crawlable.getSearchMe());
		searchResultJSON = WikiContentCrawlerHelper.getJsonResponse(searchResponse);
		
		Iterator<?> searchResultJsonIter = searchResultJSON.iterator();
		while (searchResultJsonIter.hasNext()) {
			Object next = searchResultJsonIter.next();
			
			// search string is already populated
			if (!(next instanceof JSONArray))
				continue;
			
			if (searchRelevantTitles == null) {
				searchRelevantTitles = (JSONArray) next;
			} else if (searchResultContents == null) {
				searchResultContents = (JSONArray) next;
			} else if (searchResultURLs == null) {
				searchResultURLs = (JSONArray) next;
			}
		}
		
		WikiPersistenceStats persistenceStats = persistWikiContent(searchRelevantTitles);
		ContentCrawlerHelper.persistStats(persistenceStats, CrawlSource.WIKI);
	}

	@Override
	public void crawl(Collection<? extends Crawlable> crawlables) {

	}
	
	private WikiPersistenceStats persistWikiContent(JSONArray titles) throws ContentCrawlerException {
		// create a table space and store this information
		WebTarget wikiSearchResource = null;
		Response wikiSearchResponse = null;
		
		WikiPersistenceStats stats = new WikiPersistenceStats(titles);
		
		JSONParser parser = new JSONParser();

		try {
			Client client = ClientBuilder.newClient();

			String uri = propsRepo.read(wiki_uri);

			String action = propsRepo.read(wiki_action);
			String searchQueryValue = propsRepo.read(wiki_query_action);

			String prop = propsRepo.read(wiki_query_prop);
			String propValue = propsRepo.read(wiki_query_prop_value);
			
			String format = propsRepo.read(wiki_query_format);
			String formatValue = propsRepo.read(wiki_query_format_value);
			
			String rvlimit = propsRepo.read(wiki_query_rvlimit);
			String rvlimitValue = propsRepo.read(wiki_query_rvlimit_value);
			
			String rvprop = propsRepo.read(wiki_query_rvprop);
			String rvpropValue = propsRepo.read(wiki_query_rvprop_value);
			
			String title = propsRepo.read(wiki_query_titles);
			
			wikiSearchResource = client.target(uri);
			
			while (titles.iterator().hasNext()) {
				
				String _nextTitle = (String) titles.iterator().next();
				wikiSearchResponse = wikiSearchResource
						.queryParam(action, searchQueryValue)
						.queryParam(prop, propValue)
						.queryParam(rvprop, rvpropValue)
						.queryParam(rvlimit, rvlimitValue)
						.queryParam(title, _nextTitle)
						.queryParam(format, formatValue)
						.request()
						.accept(MediaType.APPLICATION_JSON)
						.get();
				
				String _jsonResponse = wikiSearchResponse.readEntity(String.class);
				JSONObject jsonResponse = (JSONObject) parser.parse(_jsonResponse);
				String contentWithMarkup = extractMediaWikiMarkedupContent(jsonResponse);
				String plainContent = removeMediaWikiMarkup(contentWithMarkup);
			}

			if (LOG.isDebugEnabled())
				LOG.debug(wikiSearchResource.getUri().toURL().toString());

			MultivaluedMap<String, Object> headers = wikiSearchResponse.getHeaders();

			if (LOG.isDebugEnabled()) {
				LOG.debug("Headers: " + headers.toString());
				LOG.debug(wikiSearchResponse.toString() + " Content length:" + wikiSearchResponse.getLength());
			}

		} catch (IOException | ParseException e) {
			LOG.error(e.getMessage());
			throw new ContentCrawlerException(e);
		}

		return null;	
	}

	private String removeMediaWikiMarkup(String contentWithMarkup) {
		return contentWithMarkup;
	}

	/*	JSON Structure:
		{
			  "continue": { ... },
			  "query": {
			    "pages": {
			      "534366": {
			        "pageid": 534366,
			        "ns": 0,
			        "title": "Barack Obama",
			        "revisions": [
			          {
			            "contentformat": "text/x-wiki",
			            "contentmodel": "wikitext",
			            "*": "content goes here"
	 */
	private String extractMediaWikiMarkedupContent(JSONObject jsonResponse) {
		String markedupContent = null;
		JSONObject pages = (JSONObject) ((JSONObject) jsonResponse.get(WIKI_QUERY)).get(WIKI_PAGES);
		Set<Object> keys = pages.keySet();
		
		for (Object key : keys) {
			JSONObject _page = (JSONObject) pages.get(key);
			JSONArray _revisions = (JSONArray) _page.get(WIKI_REVISIONS);
			if (_revisions.isEmpty())
				continue;
			
			JSONObject _content = (JSONObject) _revisions.get(0);
			markedupContent = (String) _content.get(WIKI_CONTENT);
		}
		
		return markedupContent;
	}

	private Response searchWiki(String searchMe) throws ContentCrawlerException {

		WebTarget wikiSearchResource = null;
		Response wikiSearchResponse = null;

		try {
			Client client = ClientBuilder.newClient();

			String uri = propsRepo.read(wiki_uri);

			String searchAction = propsRepo.read(wiki_action);
			String searchActionValue = propsRepo.read(wiki_search_action);

			String searchParam = propsRepo.read(wiki_search_param_search);
			wikiSearchResource = client.target(uri);
			wikiSearchResponse = wikiSearchResource
					.queryParam(searchAction, searchActionValue)
					.queryParam(searchParam, searchMe)
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.get();

			if (LOG.isDebugEnabled())
				LOG.debug(wikiSearchResource.getUri().toURL().toString());

			MultivaluedMap<String, Object> headers = wikiSearchResponse.getHeaders();

			if (LOG.isDebugEnabled()) {
				LOG.debug("Headers: " + headers.toString());
				LOG.debug(wikiSearchResponse.toString() + " Content length:" + wikiSearchResponse.getLength());
			}

		} catch (IOException e) {
			LOG.error(e.getMessage());
			throw new ContentCrawlerException(e);
		}
		return wikiSearchResponse;
	}

}
