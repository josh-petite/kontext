package org.kontext.common.repositories;

import java.io.IOException;

public interface PropertiesRepository {
    String read(String key);
    void write(String key, String value);
    void save() throws IOException;
}
