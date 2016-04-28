package org.kontext.analyser;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Row;

public class DocumentAnalyserAction extends RecursiveAction {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(DocumentAnalyserAction.class);
	private static final PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	private static final int threshold;
	
	private final Date partition;
	private final int length;
	private final List<Row> documents;
	
	static {
		threshold = Integer.parseInt(propsRepo.read(analyser_document_threshold));
	}
	
	public DocumentAnalyserAction(Date partition, List<Row> documents) {
		this.partition = partition;
		this.documents = documents;
		this.length = documents.size();
	}
	
	@Override
	protected void compute() {
		if (LOG.isDebugEnabled())
			LOG.debug("Number of documents : " + length);

		if (length <= threshold) {
			analyse();
			return;
		}

		int split = length / 2;

		List<Row> firstSplit = documents.subList(0, split);
		List<Row> secondSplit = documents.subList(split, length);
		invokeAll(new DocumentAnalyserAction(partition, firstSplit), new DocumentAnalyserAction(partition, secondSplit));
	}

	private void analyse() {
		if(LOG.isDebugEnabled())
			LOG.debug("Number of documents to be analysed - " + documents.size());
	}

}
