package org.kontext.cassandra.documents;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.inject.Inject;

import org.kontext.cassandra.documents.exception.DocumentRepositoryException;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.data.DataSourceManager;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class DocumentRepositoryImpl implements DocumentRepository {
	private final PropertiesRepository propertiesRepository;
	private final DataSourceManager dataSourceManager;
	
	/* Batch = Date */
	private final String todaysDate;
	
	private String documentsKeyspace;
	private String documentsTable;
	private BoundStatement storeDocumentBoundStatement;
	private Session session;

	@Inject
	public DocumentRepositoryImpl(PropertiesRepository propertiesRepository, DataSourceManager datasourceManager) {
		this.propertiesRepository = propertiesRepository;
		this.dataSourceManager = datasourceManager;
		todaysDate = getTodaysDate();
		init();
	}
	
	private static String getTodaysDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	}

	private void init() {
		session = (Session) dataSourceManager.getConnection();
		Properties properties = propertiesRepository.getAllProperties();

		documentsKeyspace = (String) properties.get(cassandra_keyspace);
		documentsTable = (String) properties.get(cassandra_document_table);

		ensureKeyspaceExistence();
		ensureDocumentTableExistence();

		String cqlMask = "INSERT INTO %s (id, html, raw_text, link_count, create_date) "
				+ "VALUES (?, ?, ?, ?, toTimestamp('" + todaysDate + "'));";
		PreparedStatement statement = session.prepare(String.format(cqlMask, documentsTable));
		storeDocumentBoundStatement = new BoundStatement(statement);
	}

	private void ensureDocumentTableExistence() {
		String cqlMask = "CREATE TABLE IF NOT EXISTS %s.%s "
				+ "(id UUID, html text, raw_text text, link_count int, create_date timestamp, "
				+ "parsed_out text, PRIMARY KEY (create_date, id));";
		String cql = String.format(cqlMask, documentsKeyspace, documentsTable);

		PreparedStatement statement = session.prepare(cql);
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind());
	}

	private void ensureKeyspaceExistence() {
		String cqlMask = "CREATE KEYSPACE IF NOT EXISTS %s WITH "
				+ "replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};";

		PreparedStatement statement = session.prepare(String.format(cqlMask, documentsKeyspace));
		BoundStatement boundStatement = new BoundStatement(statement);

		session.execute(boundStatement.bind());
		session.execute(String.format("USE %s", documentsKeyspace));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kontext.cassandra.documents.DocumentRepository#storeDocument(java.
	 * lang.String, java.lang.String, int)
	 * 
	 * bing(UUID.randomUUID(), html, text, linkCount, partition)
	 */
	@Override
	public void storeDocument(String html, String text, int linkCount) {
		session.execute(storeDocumentBoundStatement.bind(UUID.randomUUID(), html, text, linkCount));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kontext.cassandra.documents.DocumentRepository#read(java.lang.String)
	 * 
	 * QueryBuilder.select().all().from(...).where(partition)
	 */
	@Override
	public ResultSet read(Date partition, int limit) {
		Statement select = QueryBuilder.select().from(documentsKeyspace, documentsTable).limit(limit);
		Session session = (Session) dataSourceManager.getConnection();
		ResultSet results = session.execute(select);
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.kontext.cassandra.documents.DocumentRepository#read(java.lang.String)
	 * 
	 * QueryBuilder.select().all().from(...).where(partition)
	 */
	@Override
	public ResultSet read(Date createDate) {
		Statement select = QueryBuilder.select().from(documentsKeyspace, documentsTable);
		Session session = (Session) dataSourceManager.getConnection();
		ResultSet results = session.execute(select);
		return results;
	}

	@Override
	public void purge(Date partition) {
		PreparedStatement statement = session
				.prepare(String.format("TRUNCATE %s.%s;", documentsKeyspace, documentsTable));
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind());
	}

	@Override
	public void finalize() throws Throwable {
		super.finalize();
		dataSourceManager.close(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kontext.cassandra.documents.DocumentRepository#count()
	 * 
	 * Ideally, count should take a partition as well. Will do it as the bridge
	 * is being crossed.
	 */
	@Override
	public long count(Date partition) throws DocumentRepositoryException {
		PreparedStatement statement = session
				.prepare(String.format("SELECT count(*) from %s.%s WHERE create_date = ?;", documentsKeyspace, documentsTable));
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet countRS = session.execute(boundStatement.bind(partition));
		Row countRow = countRS.one();

		if (countRow == null)
			return 0;

		return countRow.getLong(0);
	}

	@Override
	public List<Date> getAllPartitions() {
		PreparedStatement statement = session
				.prepare(String.format("SELECT distinct create_date from %s.%s;", documentsKeyspace, documentsTable));
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet countRS = session.execute(boundStatement.bind());
		
		List<Date> createDates = new ArrayList<>();
		for (Row row : countRS.all()) {
			createDates.add(row.getTimestamp(create_date));
		}
		
		return createDates;
	}
}
