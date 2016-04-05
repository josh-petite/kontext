package org.kontext.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.google.inject.Inject;

import java.io.IOException;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

class DocumentRepository {
    private Cluster cluster;
    private Session session;

    @Inject
    DocumentRepository(ApplicationPropertiesRepository applicationPropertiesRepository) throws IOException {
        applicationPropertiesRepository.load();

        cluster = Cluster.builder().addContactPoint(applicationPropertiesRepository.read("url")).build();
        session = cluster.connect(applicationPropertiesRepository.read("keyspace"));
    }

    void storeDocument(String html, String text) {
        PreparedStatement statement = session.prepare("INSERT INTO player (identifier, experience, level, name) VALUES (?, ?, ?, ?);");
        BoundStatement boundStatement = new BoundStatement(statement);

        //session.execute(boundStatement.bind(UUID.randomUUID(), 0, 1, "New Test"));
    }

    void read() {
        Statement select = QueryBuilder.select()
                .all()
                .from("playerkeyspace", "player")
                .where(eq("name", "Josh"));

        ResultSet results = session.execute(select);

        for (Row row : results) {
            System.out.format("%s %s\n", row.getUUID("identifier"), row.getString("name"));
        }
    }

    void delete() {
        PreparedStatement statement = session.prepare("DELETE FROM player WHERE name = ?");
        BoundStatement boundStatement = new BoundStatement(statement);

        session.execute(boundStatement.bind("Josh"));
    }
}
