package org.kontext.common.repositories;

import java.io.*;
import static org.kontext.common.repositories.PropertiesRepositoryConstants.*;
import java.util.Properties;

public class PropertiesRepositoryImpl implements PropertiesRepository {

	private Properties properties = null;

	private void load() {
		InputStream configFile = ClassLoader.getSystemResourceAsStream(config_file);
		if (configFile == null) {
			try {
				throw new Exception(String.format("Expected configuration does not exist: %s", config_file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		retrieveConfigContents(configFile);
	}

	private void retrieveConfigContents(InputStream configFile) {

		try {
			properties = new Properties();
			properties.load(configFile);
			configFile.close();

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (configFile != null) {
				try {
					configFile.close();
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

	/**
	 * TODO: Re-think writing back to file. 
	 * 
	 * If we are deploying this in a container from a jar, writing back
	 * to the same file will become a problem.
	 */
	public void save() throws IOException {
		ensurePropertiesArePopulated();
		FileOutputStream out = new FileOutputStream(config_file);
		properties.store(out, "Properties updated");
		out.close();
	}
}
