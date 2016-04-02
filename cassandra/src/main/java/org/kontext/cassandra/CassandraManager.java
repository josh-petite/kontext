package org.kontext.cassandra;

import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_port;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.cassandra_url;

import java.io.Closeable;
import java.io.IOException;

import com.google.inject.Inject;
import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;

import com.datastax.driver.core.Cluster;

public class CassandraManager implements DataSourceManager {

	@Inject
	public CassandraManager() {
	}


	@Override
	public Closeable getConnection() {
		PropertiesRepository propertiesRepository = new PropertiesRepositoryImpl();
		String address = String.format("%s %s", propertiesRepository.read(cassandra_url),
				propertiesRepository.read(cassandra_port));
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
