package org.kontext.crawler.wiki;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.wiki_search_action;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.wiki_search_action_value;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.wiki_search_param_search;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.wiki_uri;

import java.util.Collection;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.crawler.ContentCrawler;
import org.kontext.crawler.Crawlable;
import org.kontext.crawler.exception.ContentCrawlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiContentCrawler implements ContentCrawler {

	private static PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	private static final Logger LOG = LoggerFactory.getLogger(WikiContentCrawler.class);

	private WikiCrawlable crawlable;
	
	public WikiContentCrawler(Crawlable crawlable) {
		this.crawlable = (WikiCrawlable) crawlable;
	}

	@Override
	public void crawl() throws ContentCrawlerException {
		Response searchResponse = searchWiki(crawlable.getSearchMe());
		JSONObject result = (JSONObject) searchResponse.getEntity();
		result.get(crawlable.getSearchMe());
	}

	@Override
	public void crawl(Collection<? extends Crawlable> crawlables) {

	}

	private Response searchWiki(String searchMe) throws ContentCrawlerException {

		WebTarget wikiSearchResource = null;
		Response wikiSearchResponse = null;

		try {
			Client client = ClientBuilder.newClient();

			String uri = propsRepo.read(wiki_uri);
			
			String searchAction = propsRepo.read(wiki_search_action);
			String searchActionValue = propsRepo.read(wiki_search_action_value);
			
			String searchParam = propsRepo.read(wiki_search_param_search);

			wikiSearchResource = client.target(uri);

			wikiSearchResponse = wikiSearchResource
					.queryParam(searchAction, searchActionValue)
					.queryParam(searchParam, searchMe)
					.request()
					.accept(MediaType.APPLICATION_JSON).get();

			if (LOG.isDebugEnabled())
				LOG.debug(wikiSearchResource.getUri().toURL().toString());

			MultivaluedMap<String, Object> headers = wikiSearchResponse.getHeaders();

			if (LOG.isDebugEnabled()) {
				LOG.debug("Headers: " + headers.toString());
				LOG.debug(wikiSearchResponse.toString() + " Content length:" + wikiSearchResponse.getLength());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new ContentCrawlerException(e);
		}
		return wikiSearchResponse;
	}

}
