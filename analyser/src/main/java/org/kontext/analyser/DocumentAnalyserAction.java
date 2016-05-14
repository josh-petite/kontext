package org.kontext.analyser;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang.SerializationUtils;
import org.kontext.analyser.exception.DocumentAnalyserException;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Row;

import edu.stanford.nlp.util.CoreMap;

public class DocumentAnalyserAction extends RecursiveAction {

	private static int threadCount = 0;
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
		incrementThreadCount();
		
		if (LOG.isInfoEnabled())
			LOG.info("Number of documents : " + length + " | Thread count : " + threadCount);
		
		if (length <= threshold) {
			try {
				analyse();
			} catch (DocumentAnalyserException e) {
				LOG.error("Document Analysis failed for " + length + " documents", e);
			}
			return;
		}

		int split = length / 2;

		List<Row> firstSplit = documents.subList(0, split);
		List<Row> secondSplit = documents.subList(split, length);
		invokeAll(new DocumentAnalyserAction(partition, firstSplit), new DocumentAnalyserAction(partition, secondSplit));
	}

	private synchronized void incrementThreadCount() {
		threadCount ++;
	}

	private void analyse() throws DocumentAnalyserException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Number of documents for analysis : " + length + " | Thread count : " + threadCount);
		
		for (Row document : documents) {
			ByteBuffer byteBuffer = document.getBytes(parsed_out);
			if (byteBuffer == null)
				continue;
			
			@SuppressWarnings("unchecked")
			List<CoreMap> sentences = (List<CoreMap>) SerializationUtils.deserialize(byteBuffer.array());
			DocumentAnalyser docAnalyser = new DocumentAnalyserImpl(document.getUUID(id), sentences);
			docAnalyser.run();
		}
	}

}
