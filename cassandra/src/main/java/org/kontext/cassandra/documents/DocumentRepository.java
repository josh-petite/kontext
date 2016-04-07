package org.kontext.cassandra.documents;

public interface DocumentRepository {
    void init();
    void read();
    void purge();
    void storeDocument(String html, String text, int linkCount);
}
