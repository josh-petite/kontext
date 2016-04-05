package org.kontext.cassandra;

import java.io.IOException;

public interface PropertiesRepository {
    void load();
    String read(String key);
    void write(String key, String value);
    void save() throws IOException;
}
