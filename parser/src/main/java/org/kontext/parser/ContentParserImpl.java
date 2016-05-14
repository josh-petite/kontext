package org.kontext.parser;

import java.util.Date;
import java.util.List;
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
		List<Date> partitions = docsRepo.getAllPartitions();
		for (Date partition : partitions) {
			parse(partition);
		}
	}

	@Override
	public void parse(String parseMe) {
		new ContentParseAction(parseMe).invoke();
	}

	@Override
	public void parse(Date partition) {
		List<Row> documents = docsRepo.read(partition).all();
		new ContentParseAction(documents).invoke();
	}

}
