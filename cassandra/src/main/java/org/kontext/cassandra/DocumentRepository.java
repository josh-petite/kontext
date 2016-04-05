package org.kontext.cassandra;

public interface DocumentRepository {
    void init();
    void read();
    void purge();
    void storeDocument(String html, String text, int linkCount);
}
