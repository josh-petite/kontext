package org.kontext.common.repositories;

import java.io.*;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;
import static org.kontext.common.repositories.exception.PropertiesRepositoryException.*;
import java.util.Properties;

import org.kontext.common.repositories.exception.PropertiesRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class PropertiesRepositoryImpl implements PropertiesRepository {

	private static Logger LOG = LoggerFactory.getLogger(PropertiesRepositoryImpl.class);
	private Properties properties = null;
	private Session session;

	private void load() throws PropertiesRepositoryException {
		if (LOG.isDebugEnabled())
			LOG.debug("Loading properties");
		
		InputStream configFile = ClassLoader.getSystemResourceAsStream(properties_config_file);
		if (configFile == null) {
			throw new PropertiesRepositoryException(config_file_not_found);
		}
		
		retrieveConfigContents(configFile);
		
		loadToCassandra();
        
		if (LOG.isDebugEnabled())
			LOG.debug("Loading properties complete");
	}

	private void loadToCassandra() {
		createPropertiesTable();
		
		
	}

	private void createPropertiesTable() {
		String cqlMask = "CREATE TABLE IF NOT EXISTS %s.%s (module varchar, name varchar, value varchar, editable boolean) PRIMARY KEY (module, name) ";
        String cql = String.format(cqlMask, properties.get(PropertiesRepositoryConstants.cassandra_document_table), 
        		PropertiesRepositoryConstants.properties_table);
        
        PreparedStatement statement = session.prepare(cql);
        BoundStatement boundStatement = new BoundStatement(statement);
        session.execute(boundStatement.bind());
	}

	private void retrieveConfigContents(InputStream configFile) {

		try {
			properties = new Properties();
			properties.load(configFile);
		} catch (IOException io) {
			if (LOG.isErrorEnabled())
				LOG.error(io.getMessage());
		} finally {
			if (configFile != null) {
				try {
					configFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void ensurePropertiesArePopulated() throws PropertiesRepositoryException {
		if (properties == null) {
			load();
		}
	}

	public String read(String key) {
		try {
			ensurePropertiesArePopulated();
		} catch (PropertiesRepositoryException e) {
			if (LOG.isErrorEnabled())
				LOG.error("Error during population of properties.", e);
		}
		return properties.getProperty(key);
	}

	/**
	 * Find module name, create batches based on module names and insert the props
	 */
	public void write(String key, String value) {
		try {
			ensurePropertiesArePopulated();
			if (properties.get(key) == null) {
				if (LOG.isWarnEnabled())
					LOG.warn("Property not editable. Update did not succeed.");
				return;
			}
		} catch (PropertiesRepositoryException e) {
			if (LOG.isErrorEnabled())
				LOG.error("Error during population of properties.", e);
		}
		properties.setProperty(key, value);
	}

	/**
	 * TODO: Re-think writing back to file.
	 * 
	 * If we are deploying this in a container from a jar, writing back to the
	 * same file will become a problem.
	 * @throws PropertiesRepositoryException 
	 */
	public void save() throws PropertiesRepositoryException {
		try {
			ensurePropertiesArePopulated();
		} catch (PropertiesRepositoryException e) {
			if (LOG.isErrorEnabled())
				LOG.error("Error saving properties.", e);
			throw e;
		}
//		FileOutputStream out = new FileOutputStream(config_file);
//		properties.store(out, "Properties updated");
//		out.close();
	}

	@Override
	public Properties getAllProperties() {
		return properties;
	}
}
