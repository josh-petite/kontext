package org.kontext.common.repositories;

import java.io.*;
import java.util.Properties;

public class PropertiesRepositoryImpl implements PropertiesRepository {
    private final String configTarget = "config/config.properties";

    private Properties properties = null;

    private void load() {
        File config = new File(configTarget);
        if (!config.exists()) {
            try {
                throw new Exception(String.format("Expected configuration does not exist: %s", configTarget));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        retrieveConfigContents();
    }

    private void retrieveConfigContents() {
        FileInputStream stream = null;

        try {
            stream = new FileInputStream(configTarget);
            properties = new Properties();
            properties.load(stream);
            stream.close();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void ensurePropertiesArePopulated() {
        if (properties == null) {
            load();
        }
    }

    public String read(String key) {
        ensurePropertiesArePopulated();
        return properties.getProperty(key);
    }

    public void write(String key, String value) {
        ensurePropertiesArePopulated();
        properties.setProperty(key, value);
    }

    public void save() throws IOException {
        ensurePropertiesArePopulated();
        FileOutputStream out = new FileOutputStream(configTarget);
        properties.store(out, "Properties updated");
        out.close();
    }
}
