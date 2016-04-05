package org.kontext.cassandra;

import java.io.*;
import java.util.Properties;

public class ApplicationPropertiesRepositoryImpl implements ApplicationPropertiesRepository {
    private final String configTarget = "config.properties";

    private Properties applicationProps;

    public void load() throws IOException {
        File configuration = new File(configTarget);
        if (!configuration.exists()) {
            scaffold();
        } else {
            FileInputStream stream = new FileInputStream(configTarget);
            applicationProps = new Properties();
            applicationProps.load(stream);
            stream.close();
        }
    }

    private void scaffold() {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream(configTarget);

            // set the properties value
            prop.setProperty("keyspace", "document_keyspace");
            prop.setProperty("url", "127.0.0.1");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String read(String key) {
        return applicationProps.getProperty(key);
    }

    public void write(String key, String value) {
        applicationProps.setProperty(key, value);
    }

    public void save() throws IOException {
        FileOutputStream out = new FileOutputStream(configTarget);
        applicationProps.store(out, "---No Comment---");
        out.close();
    }
}
