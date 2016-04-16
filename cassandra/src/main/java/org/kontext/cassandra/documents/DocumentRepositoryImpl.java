package org.kontext.cassandra.documents;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.inject.Inject;

import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.data.DataSourceManager;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;

import java.util.Properties;
import java.util.UUID;

public class DocumentRepositoryImpl implements DocumentRepository {
    private final PropertiesRepository propertiesRepository;
    private final DataSourceManager dataSourceManager;

    private String documentsKeyspace;
    private String documentsTable;
    private BoundStatement storeDocumentBoundStatement;
	private Session session;

    @Inject
    public DocumentRepositoryImpl(PropertiesRepository propertiesRepository, DataSourceManager datasourceManager) {
        this.propertiesRepository = propertiesRepository;
        this.dataSourceManager = datasourceManager;

        init();
    }

    private void init() {
        session = (Session) dataSourceManager.getConnection();
        Properties properties = propertiesRepository.getAllProperties();
        
        documentsKeyspace = (String) properties.get(cassandra_keyspace);
        documentsTable = (String) properties.get(cassandra_document_table);

        ensureKeyspaceExistence();
        ensureDocumentTableExistence();

        String cqlMask = "INSERT INTO %s (id, html, raw_text, link_count, create_date) VALUES (?, ?, ?, ?, toTimestamp(now()));";
        PreparedStatement statement = session.prepare(String.format(cqlMask, documentsTable));
        storeDocumentBoundStatement = new BoundStatement(statement);
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
        session.execute(storeDocumentBoundStatement.bind(UUID.randomUUID(), html, text, linkCount));
    }

    public void read() {
        Statement select = QueryBuilder.select()
                .all()
                .from(documentsKeyspace, documentsTable);

        Session session = (Session) dataSourceManager.getConnection();
        ResultSet results = session.execute(select);

        for (Row row : results) {
            System.out.format("%s %s\n", row.getUUID("id"), row.getTimestamp("create_date"));
        }
    }

    public void purge() {
        PreparedStatement statement = session.prepare(String.format("TRUNCATE %s;", documentsTable));
        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind());
    }   

    @Override
	public void finalize() throws Throwable {
        super.finalize();
        dataSourceManager.close(session);
    }
}
