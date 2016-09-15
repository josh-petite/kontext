package org.kontext.crawler.wiki;

import java.sql.Timestamp;

import org.json.simple.JSONArray;

public class WikiPersistenceStats {

	private final int numberOfTitles;
	private Timestamp start;
	private Timestamp end;
	private final JSONArray titles;
	private long timeTaken;
	
	public WikiPersistenceStats(JSONArray titles) {
		this.titles = titles;
		numberOfTitles = titles.size();
	}
	
	public void setStart(Timestamp start) {
		this.start = start;
	}
	
	public void setEnd(Timestamp end) {
		if (this.start == null)
			return;
		this.end = end;
		timeTaken = this.end.getTime() - this.start.getTime();
	}
	
	public int getNumberOfTitles() {
		return numberOfTitles;
	}
	
	public JSONArray getTitles() {
		return titles;
	}
	
	public long getTimeTaken() {
		return timeTaken;
	}
}
