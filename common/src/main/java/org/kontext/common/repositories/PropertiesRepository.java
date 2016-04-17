package org.kontext.common.repositories;

import java.io.IOException;
import java.util.Properties;

import org.kontext.common.repositories.exception.PropertiesRepositoryException;

public interface PropertiesRepository {
	
	/**
	 * Read the value for the property key passed.
	 * 
	 * @param key
	 * @return
	 */
	String read(String key);

	/**
	 * Write the value of the property if it is changeable.
	 * Write once. Read many many times. Do not write unnecessarily.
	 * 
	 * @param key
	 * @param value
	 * @throws PropertiesRepositoryException 
	 */
	void write(String key, String value) throws PropertiesRepositoryException;

	/**
	 * Save the entire property set and make it retrievable.
	 * @throws IOException
	 * @throws PropertiesRepositoryException 
	 */
	void save() throws PropertiesRepositoryException;

	/**
	 * Returns the entire system property set.
	 * 
	 * @return
	 */
	Properties getAllProperties();

	/**
	 * Clear properties from memory and Cassandra.
	 * 
	 */
	void clear();
}
