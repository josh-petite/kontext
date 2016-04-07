package org.kontext.data;

import java.io.Closeable;

public interface DataSourceManager {

	/**
	 * Gets a connection for the data source.
	 * 
	 * @return an open connection
	 */
	public Closeable getConnection();
	
	/**
	 * Closes the passed connection
	 * 
	 * @param closeable
	 */
	public void close(Closeable closeable);
	
}
