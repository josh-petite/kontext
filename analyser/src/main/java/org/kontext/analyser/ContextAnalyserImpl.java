package org.kontext.analyser;

import java.util.Date;
import java.util.List;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;

public class ContextAnalyserImpl implements ContextAnalyser {
	
	private static final PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();

	private DocumentRepository docsRepo;
	
	public ContextAnalyserImpl(DocumentRepository docsRepo) {
		this.docsRepo = docsRepo;
	}

	/*
	 * Works by batches (= create_date)
	 * 
	 * (non-Javadoc)
	 * @see org.kontext.analyser.ContextAnalyser#analyser()
	 */
	@Override
	public void analyse() {
		List<Date> partitions = docsRepo.getAllPartitions();
		ContextAnalyserAction contextAnalyserAction = new ContextAnalyserAction(partitions);
		contextAnalyserAction.invoke();
	}

}
