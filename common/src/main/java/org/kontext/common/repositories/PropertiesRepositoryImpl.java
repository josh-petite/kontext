package org.kontext.common.repositories;

import java.io.*;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;
import static org.kontext.common.repositories.exception.PropertiesRepositoryException.*;
import java.util.Properties;

import org.kontext.common.CassandraManager;
import org.kontext.common.repositories.exception.PropertiesRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class PropertiesRepositoryImpl implements PropertiesRepository {

	private static final String writes_not_allowed = "Modifications to properties not allowed at this moment. ";
	private static final String not_initialized = "-999";
	private static final String properties_value = "value";
	
	private static Logger LOG = LoggerFactory.getLogger(PropertiesRepositoryImpl.class);
	
	private static Properties properties = null;
	private static Session session;
	
	private static PropertiesRepositoryImpl INSTANCE;

	private PropertiesRepositoryImpl() {
		try {
			retrieveConfigContents();
			session = (Session) new CassandraManager(this).getConnection();
			load();
		} catch (PropertiesRepositoryException e) {
			if (LOG.isErrorEnabled())
				LOG.error("Could not initialize properties repository. ", e);
		}
	}
	
	public static PropertiesRepositoryImpl getPropsRepo() {
		if (INSTANCE == null)
			INSTANCE = new PropertiesRepositoryImpl();
		return INSTANCE;
	}

	private void load() throws PropertiesRepositoryException {
		if (LOG.isDebugEnabled())
			LOG.debug("Loading properties");

		/* Create if not present. */
		createPropertiesTable();

		String version = properties.getProperty(properties_version);
		String _version = getVersion();
		
		if (version.equals(_version))
			return;
		
		loadPropertiesToTable();

		if (LOG.isDebugEnabled())
			LOG.debug("Loading properties complete");
	}

	private void loadPropertiesToTable() throws PropertiesRepositoryException {
		if (LOG.isDebugEnabled())
			LOG.debug("Load properties to table.");
		
		String cqlMask = "INSERT INTO %s.%s (module, name, value) VALUES (?, ?, ?);";
		String cql = String.format(cqlMask, properties.get(cassandra_keyspace), properties.get(properties_table));
		PreparedStatement statement = session.prepare(cql);
		BoundStatement insertPropertiesBS = new BoundStatement(statement);
		String _k = null;
		String _v = null;
		String module = null;

		for (Object key : properties.keySet()) {
			_k = (String) key;
			_v = properties.getProperty(_k);
			module = getModule(_k);
			
			if (module == null)
				throw new PropertiesRepositoryException(module_name_required);
			insertPropertiesBS.bind(module, _k, _v);
			session.execute(insertPropertiesBS.bind());
		}

		if (LOG.isDebugEnabled())
			LOG.debug("Load properties to table - Complete.");
	}
	
	private static String getVersion() {
		String cqlMask = "SELECT value from %s.%s WHERE module = ? and name = ?";
		String cql = String.format(cqlMask, properties.get(cassandra_keyspace), properties.get(properties_table));

		PreparedStatement statement = session.prepare(cql);
		BoundStatement boundStatement = new BoundStatement(statement);
		Row row = session.execute(boundStatement.bind(properties_module, properties_version)).one();
		
		if (row == null)
			return not_initialized;
		
		String version = row.getString(properties_value);
		return version;
	}

	private String getModule(String _k) {
		String[] tokens = _k.split("_");
		if (tokens.length > 1)
			return tokens[0];
		return null;
	}

	private void createPropertiesTable() {
		String cqlMask = "CREATE TABLE IF NOT EXISTS %s.%s "
				+ "(module varchar, name varchar, value varchar, editable boolean, PRIMARY KEY (module, name)); ";
		
		String cql = String.format(cqlMask, properties.get(cassandra_keyspace), properties.get(properties_table));

		PreparedStatement statement = session.prepare(cql);
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind());
	}

	private void retrieveConfigContents() throws PropertiesRepositoryException {
		InputStream configFile = null;

		try {
			if (properties != null)
				return;
			
			configFile = ClassLoader.getSystemResourceAsStream(properties_config_file);
			if (configFile == null) {
				throw new PropertiesRepositoryException(config_file_not_found);
			}
			properties = new Properties();
			properties.load(configFile);
		} catch (IOException io) {
			if (LOG.isErrorEnabled())
				LOG.error(io.getMessage());
			properties = null;
			throw new PropertiesRepositoryException(load_to_properties_failed, io);
		} finally {
			if (configFile != null) {
				try {
					configFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (properties == null)
			throw new PropertiesRepositoryException(load_to_properties_failed);
	}

	public String read(String key) {
		if (key == null)
			return null;

		String cqlMask = "SELECT value from %s.%s where module = ? and name = ?;";
		String cql = String.format(cqlMask, properties.get(cassandra_keyspace), properties.get(properties_table));
		PreparedStatement statement = session.prepare(cql);
		BoundStatement insertPropertiesBS = new BoundStatement(statement);
		String _k = (String) key;
		String module = getModule(_k);

		if (module == null)
			return null;

		ResultSet rs = session.execute(insertPropertiesBS.bind(module, _k));

		Row result = rs.one();
		if (result != null)
			return result.getString(0);

		return null;
	}

	@Override
	public void write(String key, String value) throws PropertiesRepositoryException {
		if (properties == null) {
			load();
			return;
		}
		throw new PropertiesRepositoryException(writes_not_allowed);
	}

	@Override
	public Properties getAllProperties() {
		return properties;
	}

	@Override
	public void save() throws PropertiesRepositoryException {
		throw new PropertiesRepositoryException(writes_not_allowed);
	}

	@Override
	public void clear() {
		if (LOG.isDebugEnabled())
			LOG.debug("Dropping properties.");
		String cqlMask = "DROP TABLE %s.%s;";
		String cql = String.format(cqlMask, properties.get(cassandra_keyspace), properties.get(properties_table));
		PreparedStatement statement = session.prepare(cql);
		BoundStatement insertPropertiesBS = new BoundStatement(statement);
		session.execute(insertPropertiesBS.bind());
		if (LOG.isDebugEnabled())
			LOG.debug("Dropping properties - Complete.");
	}
}