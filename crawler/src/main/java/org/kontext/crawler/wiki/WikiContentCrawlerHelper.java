package org.kontext.crawler.wiki;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiContentCrawlerHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(WikiContentCrawlerHelper.class);

	public static JSONArray getJsonResponse(Response response) {
		String result = response.readEntity(String.class);
		JSONArray jsonResult = null;

		try {
			JSONParser jParser = new JSONParser();
			jsonResult = (JSONArray) jParser.parse(result);
			if (LOG.isDebugEnabled())
				LOG.debug(result);

		} catch (ParseException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return jsonResult;
	}
}
