package org.kontext.common.repositories;

import java.io.IOException;

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
	 * 
	 * @param key
	 * @param value
	 */
	void write(String key, String value);

	/**
	 * Save the entire property set and make it retrievable.
	 * @throws IOException
	 */
	void save() throws IOException;
}
