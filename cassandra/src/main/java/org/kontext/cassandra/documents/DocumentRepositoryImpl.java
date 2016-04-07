package org.kontext.cassandra.documents;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.inject.Inject;

import org.kontext.cassandra.CassandraManager;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.data.DataSourceManager;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.util.UUID;

public class DocumentRepositoryImpl implements DocumentRepository {
    private PropertiesRepository propertiesRepository;

    private String documentsKeyspace;
    private String documentsTable;
    private PreparedStatement storeDocumentStatement;
    private CassandraManager cassandraManager;
	private Session session;

    @Inject
    public DocumentRepositoryImpl(PropertiesRepository propertiesRepository, DataSourceManager datasourceManager) {
        this.propertiesRepository = propertiesRepository;
        cassandraManager = (CassandraManager) datasourceManager;
		session = (Session) cassandraManager.getConnection();
    }

    public void init() {
        documentsKeyspace = propertiesRepository.read(cassandra_keyspace);
        documentsTable = propertiesRepository.read(cassandra_document_table);

        ensureKeyspaceExistence();
        ensureDocumentTableExistence();

        String cqlMask = "INSERT INTO %s (id, html, raw_text, link_count, create_date) VALUES (?, ?, ?, ?, toTimestamp(now()));";
        storeDocumentStatement = session.prepare(String.format(cqlMask, documentsTable));
    }

    private void ensureDocumentTableExistence() {
        String cqlMask = "CREATE TABLE IF NOT EXISTS %s.%s (id UUID PRIMARY KEY, html text, raw_text text, link_count int, create_date timestamp);";
        String cql = String.format(cqlMask, documentsKeyspace, documentsTable);
        
        PreparedStatement statement = session.prepare(cql);
        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind());
    }

    private void ensureKeyspaceExistence() {
        String cqlMask = "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};";

        PreparedStatement statement = session.prepare(String.format(cqlMask, documentsKeyspace));
        BoundStatement boundStatement = new BoundStatement(statement);

        session.execute(boundStatement.bind());
        session.execute(String.format("USE %s", documentsKeyspace));
    }

    public void storeDocument(String html, String text, int linkCount) {
        BoundStatement boundStatement = new BoundStatement(storeDocumentStatement);
        session.execute(boundStatement.bind(UUID.randomUUID(), html, text, linkCount));
    }

    public void read() {
        Statement select = QueryBuilder.select()
                .all()
                .from(documentsKeyspace, documentsTable);

        Session session = (Session) cassandraManager.getConnection();
        ResultSet results = session.execute(select);

        for (Row row : results) {
            System.out.format("%s %s\n", row.getUUID("id"), row.getTimestamp("created_date"));
        }
    }

    public void purge() {
        PreparedStatement statement = session.prepare(String.format("TRUNCATE %s;", documentsTable));
        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind());
    }   

    @Override
	public void finalize() {
		cassandraManager.close(session);
    }
}
