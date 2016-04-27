package org.kontext.parser;

import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.kontext.cassandra.documents.DocumentRepository;
import com.datastax.driver.core.Row;
import com.google.inject.Inject;

public class ContentParserImpl extends ContentParser {
	private DocumentRepository docsRepo;
	
	@Inject
	public ContentParserImpl(DocumentRepository docsRepo) {
		this.docsRepo = docsRepo;
	}

	@Override
	public void parse() {
		List<Row> documents = docsRepo.read(null, 1).all();
		RecursiveAction action = new ContentParseAction(documents);
		action.invoke();
	}

	@Override
	public void parse(String parseMe) {
		RecursiveAction action = new ContentParseAction(parseMe);
		action.invoke();
	}

}
