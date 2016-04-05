package org.kontext.cassandra;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ApplicationPropertiesRepository {
    void load() throws IOException;
    String read(String key);
    void write(String key, String value);
    void save() throws IOException;
}
