package com.github.mbeier1406.mongodb;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * MongoDB-Implementierung für das Persitieren von E-Rezepten.
 * @author mbeier
 */
public class MongoDbDao implements Dao<Dao.ERezept> {

	private static final Logger LOGGER = LogManager.getLogger(MongoDbDao.class);

	/** URL zur Datenbank */
	private String url;

	public MongoDbDao() throws IOException {
		final var prop = new Properties();
		prop.load(getClass().getResourceAsStream("application.properties"));
		final var url = (String) prop.get("url");
		connect(url);
	}

	public MongoDbDao(final String url) {
		connect(url);
	}

	/** Verbindung zur MongoDB */
	private void connect(final String url) {
		this.url = requireNonNull(url, "URL ist NULL!");
		LOGGER.trace("URL: {}", this.url);
	}

	/** {@inheritDoc} */
	@Override
	public String getConnectionInfo() {
		return url;
	}

	/** {@inheritDoc} */
	@Override
	public ERezept find(String eRezeptId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "MongoDbDao [url=" + url + "]";
	}

}
