package org.kontext.cassandra.documents;

import org.kontext.cassandra.documents.exception.DocumentRepositoryException;

import com.datastax.driver.core.ResultSet;

public interface DocumentRepository {
	
	/**
	 * Get all the rows for a given partition. 
	 * TODO Right now, partitions have not been defined.
	 * 
	 * @return
	 */
    ResultSet read(String partition);
    
    /**
     * Purge all items by partition
     * 
     */
    void purge(String partition);
    
    /**
     * Add documents to a partition
     * 
     * @param html
     * @param text
     * @param linkCount
     */
    void storeDocument(String html, String text, int linkCount);
    
    /**
     * Get the count of the training set under consideration.
     * 
     * @return
     */
    long count() throws DocumentRepositoryException;
}
