package org.kontext.common;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_port;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_url;

import java.io.Closeable;
import java.io.IOException;

import com.google.inject.Inject;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.data.DataSourceManager;

import com.datastax.driver.core.Cluster;

public class CassandraManager implements DataSourceManager {

    private final PropertiesRepository propertiesRepository;

    @Inject
	public CassandraManager(PropertiesRepository propertiesRepository) {
        this.propertiesRepository = propertiesRepository;
    }

	@Override
	public Closeable getConnection() {
		String address = String.format("%s %s", propertiesRepository.getAllProperties().get(cassandra_url),
				propertiesRepository.getAllProperties().get(cassandra_port));
		Cluster cluster = Cluster.builder().addContactPoint(address).build();
		return cluster.connect();
	}

	@Override
	public void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
