package org.kontext.cassandra;

import java.io.*;
import java.util.Properties;

public class PropertiesRepositoryImpl implements PropertiesRepository {
    private final String configTarget = "config/config.properties";

    private Properties properties;

    public void load() {
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

    public String read(String key) {
        return properties.getProperty(key);
    }

    public void write(String key, String value) {
        properties.setProperty(key, value);
    }

    public void save() throws IOException {
        FileOutputStream out = new FileOutputStream(configTarget);
        properties.store(out, "Properties updated");
        out.close();
    }
}
