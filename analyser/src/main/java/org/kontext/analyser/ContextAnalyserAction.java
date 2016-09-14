package org.kontext.analyser;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import org.kontext.cassandra.documents.DocumentRepository;
import org.kontext.cassandra.documents.DocumentRepositoryImpl;
import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Row;

/**
 * ContextAnalyserAction class defines how the analysis action is split
 * into blocks of smaller work. The algorithm dictating the split is defined
 * in the compute method this class implements.
 * 
 * @author vikram
 *
 */
public class ContextAnalyserAction extends RecursiveAction {

	private static final long serialVersionUID = 1L;

	private final int length;
	private final List<Date> partitions;

	private static int threadCount;

	private static DocumentRepository docRepo;
	private static DataSourceManager datasourceManager;

	private static final PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	private static int partitionThreshold;

	private static final Logger LOG = LoggerFactory.getLogger(ContextAnalyserAction.class);

	static {
		partitionThreshold = Integer.parseInt(propsRepo.read(analyser_partition_threshold));
		datasourceManager = new CassandraManager(propsRepo);
		docRepo = new DocumentRepositoryImpl(propsRepo, datasourceManager);
	}

	public ContextAnalyserAction(List<Date> partitions) {
		this.partitions = partitions;
		this.length = partitions.size();
	}

	@Override
	protected void compute() {
		incrementThreadCount();
		
		if (LOG.isInfoEnabled())
			LOG.info("Number of partitions passed : " + length + " | Thread count : " + threadCount);
		
		if (length <= partitionThreshold) {
			analyse();
			return;
		}
		
		List<Date> firstHalf = partitions.subList(0, length / 2);
		List<Date> secondHalf = partitions.subList(length / 2, length);

		invokeAll(new ContextAnalyserAction(firstHalf), new ContextAnalyserAction(secondHalf));
	}
	
	private synchronized void incrementThreadCount() {
		threadCount ++;
	}

	/*
	 * Break the partition
	 */
	private void analyse() {
		DocumentAnalyserAction docAnalyseAction = null;
		for (Date partition : partitions) {
			List<Row> documents = docRepo.read(partition).all();
			docAnalyseAction = new DocumentAnalyserAction(partition, documents);
			docAnalyseAction.invoke();
		}
	}

}
